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
package se.kth.app;

import app.document.evaluator.Evaluator;
import app.document.evaluator.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.CB.CB_Broadcast;
import se.kth.app.broadcast.CB.CB_Deliver;
import se.kth.app.broadcast.CB.CausalOrderReliableBroadcast;
import se.kth.app.sim.behaviour.Behaviour;
import se.kth.app.test.Ping;
import se.kth.app.test.Pong;
import se.kth.croupier.util.CroupierHelper;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

import java.util.List;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class AppComp extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(AppComp.class);
  private String logPrefix = " ";

  //*******************************CONNECTIONS********************************
  Positive<Timer> timerPort = requires(Timer.class);
  Positive<Network> networkPort = requires(Network.class);
  Positive<CroupierPort> croupierPort = requires(CroupierPort.class);
  Positive<CausalOrderReliableBroadcast> cb = requires(CausalOrderReliableBroadcast.class);
  //**************************************************************************
  private KAddress selfAdr;

  private Behaviour behaviour;
  private Evaluator evaluator;

  public AppComp(Init init) {
    selfAdr = init.selfAdr;
    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    this.behaviour = init.behaviour;
    this.evaluator = new Evaluator(selfAdr.hashCode()); //should be unique

    subscribe(handleStart, control);
    subscribe(handleCroupierSample, croupierPort);
    subscribe(handlePing, networkPort);
    subscribe(handlePong, networkPort);
    subscribe(cb_deliverHandler, cb);

  }

  Handler handleStart = new Handler<Start>() {
    @Override
    public void handle(Start event) {
      LOG.info("{}starting...", logPrefix);

      if(behaviour != null){
        behaviour.setup(evaluator);
      }
    }
  };

  Handler handleCroupierSample = new Handler<CroupierSample>() {
    @Override
    public void handle(CroupierSample croupierSample) {
      if (croupierSample.publicSample.isEmpty()) {
        return;
      }
      List<KAddress> sample = CroupierHelper.getSample(croupierSample);
      for (KAddress peer : sample) {
        KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
        KContentMsg msg = new BasicContentMsg(header, new Ping());
        trigger(msg, networkPort);
      }


      if(behaviour != null){
        behaviour.onSample(sample);
      }

      trigger(new CB_Broadcast(new SendQueueEvent(evaluator.send())), cb);

      if(sample.size() > 3){
        //trigger(new GBEB_Broadcast(m), gbeb);
        //trigger(new GBEB_Broadcast(new Ping()), gbeb);
        //trigger(new RB_Broadcast(message), rb);
        // trigger(new RB_Broadcast(new Ping()), rb);

        //trigger(new CB_Broadcast(new Ping()), cb);

      }

    }
  };

  protected final Handler<CB_Deliver> cb_deliverHandler = new Handler<CB_Deliver>() {
      @Override
      public void handle(CB_Deliver cb_deliver) {
        //System.out.println("Got deliver in app comp: " + selfAdr);
        if (cb_deliver.m.getM() instanceof SendQueueEvent) {
          SendQueueEvent e = (SendQueueEvent) cb_deliver.m.getM();
          for (Operation op : e.getOperations()) {
            evaluator.receive(op);
          }
        }
      }
  };


  ClassMatchedHandler handlePing
    = new ClassMatchedHandler<Ping, KContentMsg<?, ?, Ping>>() {

      @Override
      public void handle(Ping content, KContentMsg<?, ?, Ping> container) {
        //LOG.info("{}received ping from:{}", logPrefix, container.getHeader().getSource());
        trigger(container.answer(new Pong()), networkPort);
      }
    };

  ClassMatchedHandler handlePong
    = new ClassMatchedHandler<Pong, KContentMsg<?, KHeader<?>, Pong>>() {

      @Override
      public void handle(Pong content, KContentMsg<?, KHeader<?>, Pong> container) {
        //LOG.info("{}received pong from:{}", logPrefix, container.getHeader().getSource());
      }
    };

  public static class Init extends se.sics.kompics.Init<AppComp> {

    public final KAddress selfAdr;
    public final Identifier gradientOId;
    public final Behaviour behaviour;

    public Init(KAddress selfAdr, Identifier gradientOId, Behaviour behaviour) {
      this.selfAdr = selfAdr;
      this.gradientOId = gradientOId;
      this.behaviour = behaviour;
    }
  }
}
