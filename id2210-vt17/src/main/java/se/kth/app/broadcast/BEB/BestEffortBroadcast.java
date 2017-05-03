package se.kth.app.broadcast.BEB;

import se.sics.kompics.PortType;

/**
 * Created by victoraxelsson on 2017-04-26.
 */
public class BestEffortBroadcast extends PortType {
    {
        request(BEB_Broadcast.class);
        indication(BEB_Deliver.class);
    }
}
