package se.kth.app.broadcast.GBEB;

import se.sics.kompics.PortType;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class GossipingBestEffortBroadcast extends PortType{
    {
        indication(GBEB_Deliver.class);
        request(GBEB_Broadcast.class);
    }
}
