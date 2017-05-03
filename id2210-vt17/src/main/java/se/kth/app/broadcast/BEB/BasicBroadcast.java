package se.kth.app.broadcast.BEB;

import se.kth.app.link.PL_Deliver;
import se.kth.app.link.PL_Send;
import se.kth.app.link.PerfectLink;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by victoraxelsson on 2017-04-26.
 */
public class BasicBroadcast extends ComponentDefinition{

    //Ports
    protected final Positive<PerfectLink> pLink = requires(PerfectLink.class);
    protected final Negative<BestEffortBroadcast> beb = provides(BestEffortBroadcast.class);

    //Fields
    private KAddress selfAdr;

    public BasicBroadcast(Init init) {
        selfAdr = init.selfAdr;
        subscribe(broadcastHandler, beb);
        subscribe(plDeliverHandler, pLink);
    }

    //Handlers
    protected final Handler<BEB_Broadcast> broadcastHandler = new Handler<BEB_Broadcast>() {
        @Override
        public void handle(BEB_Broadcast beb_broadcast) {
            for(KAddress adr : beb_broadcast.procs){
                trigger(new PL_Send(selfAdr, adr, beb_broadcast), pLink);
            }
        }
    };

    protected final ClassMatchedHandler<BEB_Broadcast, PL_Deliver> plDeliverHandler = new ClassMatchedHandler<BEB_Broadcast, PL_Deliver>() {
        @Override
        public void handle(BEB_Broadcast beb_broadcast, PL_Deliver pl_deliver) {
            System.out.println("pLink deliver");
        }
    };

    //Initializator for the component
    public static class Init extends se.sics.kompics.Init<BasicBroadcast> {

        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
        }
    }
}
