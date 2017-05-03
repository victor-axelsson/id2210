package se.kth.app.link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.Ping;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

/**
 * Created by victoraxelsson on 2017-04-26.
 */
public class PerfectLinkComponent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(PerfectLinkComponent.class);
    //******* Ports ******
    protected final Positive<Network> network = requires(Network.class);
    protected final Negative<PerfectLink> pLink = provides(PerfectLink.class);

    protected final Handler<PL_Send> sendHandler = new Handler<PL_Send>() {
        @Override
        public void handle(PL_Send pl_send) {
            //TODO: tcp is needed for it to be a pefect link, but will croupier work with TCP?
            KHeader header = new BasicHeader(pl_send.src, pl_send.to, Transport.TCP);
            KContentMsg msg = new BasicContentMsg(header, pl_send);
            trigger(msg, network);
        }
    };

    protected final ClassMatchedHandler plSendHandler = new ClassMatchedHandler<PL_Send, KContentMsg<?, ?, PL_Send>>() {
        @Override
        public void handle(PL_Send pl_send, KContentMsg<?, ?, PL_Send> pl_sendKContentMsg) {
            trigger(new PL_Deliver(pl_sendKContentMsg.getHeader().getSource(), pl_send.payload), pLink);
        }
    };

    {
        subscribe(sendHandler, pLink);
        subscribe(plSendHandler, network);
    }
}

