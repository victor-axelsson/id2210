package se.kth.app.broadcast.GBEB;

import se.kth.app.link.PL_Deliver;
import se.kth.app.link.PL_Send;
import se.kth.app.link.PerfectLink;
import se.kth.croupier.util.CroupierHelper;
import se.sics.kompics.*;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class GossipingBEBComponent extends ComponentDefinition {
    protected final Positive<PerfectLink> pLink = requires(PerfectLink.class);
    protected final Negative<GossipingBestEffortBroadcast> gbeb = provides(GossipingBestEffortBroadcast.class);
    protected final Positive<CroupierPort> bs = requires(CroupierPort.class);

    //Fields
    private KAddress selfAdr;
    private List<HistoryElement> past;

    public GossipingBEBComponent(Init init){
        selfAdr = init.selfAdr;
        past = new ArrayList<>();
        subscribe(croupierSampleHandler, bs);
        subscribe(gbeb_broadcastHandler, gbeb);
        subscribe(pl_deliverGBEB_historyRequestClassMatchedHandler, pLink);
        subscribe(gbeb_historyResponsePL_deliverClassMatchedHandler, pLink);
    }

    private static List<HistoryElement> copyList(List<HistoryElement> toCpy){
        List<HistoryElement> res = new ArrayList<>();
        for(HistoryElement elem : toCpy){
            res.add(elem);
        }

        return res;
    }


    protected final Handler<CroupierSample> croupierSampleHandler = new Handler<CroupierSample>() {
        @Override
        public void handle(CroupierSample croupierSample) {
            if (croupierSample.publicSample.isEmpty()) {
                return;
            }
            List<KAddress> sample = CroupierHelper.getSample(croupierSample);

            for(KAddress p : sample){
                trigger(new PL_Send(selfAdr, p, new GBEB_HistoryRequest()), pLink);
            }
        }
    };

    protected final Handler<GBEB_Broadcast> gbeb_broadcastHandler = new Handler<GBEB_Broadcast>() {
        @Override
        public void handle(GBEB_Broadcast gbeb_broadcast) {
            past.add(new HistoryElement(selfAdr, gbeb_broadcast.m));
        }
    };


    protected final ClassMatchedHandler<GBEB_HistoryRequest, PL_Deliver> pl_deliverGBEB_historyRequestClassMatchedHandler = new ClassMatchedHandler<GBEB_HistoryRequest, PL_Deliver>() {
        @Override
        public void handle(GBEB_HistoryRequest gbeb_historyRequest, PL_Deliver pl_deliver) {
            trigger(new PL_Send(selfAdr, pl_deliver.src, new GBEB_HistoryResponse(copyList(past))), pLink);

        }
    };


    protected final ClassMatchedHandler<GBEB_HistoryResponse, PL_Deliver>  gbeb_historyResponsePL_deliverClassMatchedHandler = new ClassMatchedHandler<GBEB_HistoryResponse, PL_Deliver>() {
        @Override
        public void handle(GBEB_HistoryResponse gbeb_historyResponse, PL_Deliver pl_deliver) {

            List<HistoryElement> unseen = new ArrayList<>();
            for (HistoryElement element : gbeb_historyResponse.getHistory()) {
                if(!past.contains(element)){
                    unseen.add(element);
                }
            }

            for (HistoryElement element : unseen){
                trigger(new GBEB_Deliver(element.getP(),element.getM()), gbeb);
                past.add(element);
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<GossipingBEBComponent>{
        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
        }
    }
}
