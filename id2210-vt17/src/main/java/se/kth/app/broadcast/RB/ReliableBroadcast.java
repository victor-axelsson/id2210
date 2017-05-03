package se.kth.app.broadcast.RB;

import se.sics.kompics.PortType;

/**
 * Created by victoraxelsson on 2017-04-28.
 */
public class ReliableBroadcast extends PortType{
    {
        indication(RB_Deliver.class);
        request(RB_Broadcast.class);
    }
}
