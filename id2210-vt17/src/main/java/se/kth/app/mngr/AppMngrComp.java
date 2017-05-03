/*
 * 2016 Royal Institute of Technology (KTH)
 *
 * LSelector is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.app.mngr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BEB.BasicBroadcast;
import se.kth.app.broadcast.BEB.BestEffortBroadcast;
import se.kth.app.broadcast.CB.CausalOrderReliableBroadcast;
import se.kth.app.broadcast.CB.NoWaitingCausalBroadcast;
import se.kth.app.broadcast.GBEB.GossipingBEBComponent;
import se.kth.app.broadcast.GBEB.GossipingBestEffortBroadcast;
import se.kth.app.broadcast.RB.EagerReliableBroadcast;
import se.kth.app.broadcast.RB.ReliableBroadcast;
import se.kth.app.link.PerfectLink;
import se.kth.app.link.PerfectLinkComponent;
import se.kth.croupier.util.NoView;
import se.kth.app.AppComp;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapClientComp;
import se.sics.ktoolbox.overlaymngr.OverlayMngrPort;
import se.sics.ktoolbox.overlaymngr.events.OMngrCroupier;
import se.sics.ktoolbox.util.identifiable.overlay.OverlayId;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.overlays.view.OverlayViewUpdate;
import se.sics.ktoolbox.util.overlays.view.OverlayViewUpdatePort;
import sun.nio.ch.ChannelInputStream;
import sun.nio.ch.Net;
import sun.nio.cs.ext.COMPOUND_TEXT;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class AppMngrComp extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(BootstrapClientComp.class);
  private String logPrefix = "";
  //*****************************CONNECTIONS**********************************
  Positive<OverlayMngrPort> omngrPort = requires(OverlayMngrPort.class);
  //***************************EXTERNAL_STATE*********************************
  private ExtPort extPorts;
  private KAddress selfAdr;
  private OverlayId croupierId;
  //***************************INTERNAL_STATE*********************************
  private Component appComp;
  private Component beb;
  private Component pLink;
  private Component gbeb;
  private Component rb;
  private Component cb;
  //******************************AUX_STATE***********************************
  private OMngrCroupier.ConnectRequest pendingCroupierConnReq;
  //**************************************************************************


  public AppMngrComp(Init init) {
    selfAdr = init.selfAdr;
    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    extPorts = init.extPorts;
    croupierId = init.croupierOId;

    beb = create(BasicBroadcast.class, new BasicBroadcast.Init(selfAdr));
    pLink = create(PerfectLinkComponent.class, se.sics.kompics.Init.NONE);
    gbeb = create(GossipingBEBComponent.class, new GossipingBEBComponent.Init(selfAdr));
    rb = create(EagerReliableBroadcast.class, new EagerReliableBroadcast.Init(selfAdr));
    cb = create(NoWaitingCausalBroadcast.class, new NoWaitingCausalBroadcast.Init(selfAdr));

    connect(pLink.getNegative(Network.class),extPorts.networkPort, Channel.TWO_WAY);

    subscribe(handleStart, control);
    subscribe(handleCroupierConnected, omngrPort);
  }

  Handler handleStart = new Handler<Start>() {
    @Override
    public void handle(Start event) {
      LOG.info("{}starting...", logPrefix);

      pendingCroupierConnReq = new OMngrCroupier.ConnectRequest(croupierId, false);
      trigger(pendingCroupierConnReq, omngrPort);
    }
  };

  Handler handleCroupierConnected = new Handler<OMngrCroupier.ConnectResponse>() {
    @Override
    public void handle(OMngrCroupier.ConnectResponse event) {
      LOG.info("{}overlays connected", logPrefix);
      connectAppComp();
      connectBroadcasts();
      trigger(Start.event, appComp.control());
      trigger(new OverlayViewUpdate.Indication<>(croupierId, false, new NoView()), extPorts.viewUpdatePort);
    }
  };

  private void connectAppComp() {
    appComp = create(AppComp.class, new AppComp.Init(selfAdr, croupierId));
    connect(appComp.getNegative(Timer.class), extPorts.timerPort, Channel.TWO_WAY);
    connect(appComp.getNegative(Network.class), extPorts.networkPort, Channel.TWO_WAY);
    connect(appComp.getNegative(CroupierPort.class), extPorts.croupierPort, Channel.TWO_WAY);
  }

  private void connectBroadcasts(){
    //Connect beb
    connect(beb.getNegative(PerfectLink.class), pLink.getPositive(PerfectLink.class), Channel.TWO_WAY);

    //Connect gbeb
    connect(gbeb.getNegative(PerfectLink.class), pLink.getPositive(PerfectLink.class), Channel.TWO_WAY);
    connect(gbeb.getNegative(CroupierPort.class), extPorts.croupierPort, Channel.TWO_WAY);

    //Reliable broadcast
    connect(rb.getNegative(GossipingBestEffortBroadcast.class), gbeb.getPositive(GossipingBestEffortBroadcast.class), Channel.TWO_WAY);

    //Causal broadcast
    connect(cb.getPositive(CausalOrderReliableBroadcast.class), appComp.getNegative(CausalOrderReliableBroadcast.class), Channel.TWO_WAY);
    connect(cb.getNegative(ReliableBroadcast.class), rb.getPositive(ReliableBroadcast.class), Channel.TWO_WAY);

  }

  public static class Init extends se.sics.kompics.Init<AppMngrComp> {

    public final ExtPort extPorts;
    public final KAddress selfAdr;
    public final OverlayId croupierOId;

    public Init(ExtPort extPorts, KAddress selfAdr, OverlayId croupierOId) {
      this.extPorts = extPorts;
      this.selfAdr = selfAdr;
      this.croupierOId = croupierOId;
    }
  }

  public static class ExtPort {

    public final Positive<Timer> timerPort;
    public final Positive<Network> networkPort;
    public final Positive<CroupierPort> croupierPort;
    public final Negative<OverlayViewUpdatePort> viewUpdatePort;

    public ExtPort(Positive<Timer> timerPort, Positive<Network> networkPort, Positive<CroupierPort> croupierPort,
      Negative<OverlayViewUpdatePort> viewUpdatePort) {
      this.networkPort = networkPort;
      this.timerPort = timerPort;
      this.croupierPort = croupierPort;
      this.viewUpdatePort = viewUpdatePort;
    }
  }
}
