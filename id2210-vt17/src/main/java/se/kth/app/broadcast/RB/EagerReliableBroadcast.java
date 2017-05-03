package se.kth.app.broadcast.RB;

import se.kth.app.broadcast.GBEB.GBEB_Broadcast;
import se.kth.app.broadcast.GBEB.GBEB_Deliver;
import se.kth.app.broadcast.GBEB.GossipingBestEffortBroadcast;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.*;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class EagerReliableBroadcast extends ComponentDefinition {

    protected final Negative<ReliableBroadcast> rb = provides(ReliableBroadcast.class);
    private final Positive<GossipingBestEffortBroadcast> beb = requires(GossipingBestEffortBroadcast.class);

    private KAddress selfAdr;
    private Set<Data> delivered;


    public EagerReliableBroadcast(Init init){
        this.selfAdr = init.selfAdr;
        delivered = new HashSet<>();
        subscribe(rb_broadcastHandler, rb);
        subscribe(gbeb_deliverHandler, beb);
    }

    Handler<RB_Broadcast> rb_broadcastHandler = new Handler<RB_Broadcast>() {
        @Override
        public void handle(RB_Broadcast rb_broadcast) {
            trigger(new GBEB_Broadcast(new Data(selfAdr, rb_broadcast.message)), beb);
        }
    };


    ClassMatchedHandler<Data, GBEB_Deliver> gbeb_deliverHandler = new ClassMatchedHandler<Data, GBEB_Deliver>() {
        @Override
        public void handle(Data data, GBEB_Deliver gbeb_deliver) {
            if(!delivered.contains(data)){
                delivered.add(data);
                trigger(new RB_Deliver(data.getP(), data.getM()), rb);
                trigger(new GBEB_Broadcast(data), beb);
            }
        }
    };


    public static class Init extends se.sics.kompics.Init<EagerReliableBroadcast>{
        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
        }
    }
}
