package se.kth.app.broadcast.CB;

import se.sics.kompics.PortType;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class CausalOrderReliableBroadcast extends PortType{
    {
        indication(CB_Deliver.class);
        request(CB_Broadcast.class);
    }
}
