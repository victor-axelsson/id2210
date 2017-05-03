package se.kth.app.link;

import se.sics.kompics.PortType;

/**
 * Created by victoraxelsson on 2017-04-26.
 */
public class PerfectLink extends PortType {
    {
        request(PL_Send.class);
        indication(PL_Deliver.class);
    }

}

