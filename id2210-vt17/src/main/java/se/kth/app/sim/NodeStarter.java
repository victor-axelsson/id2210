package se.kth.app.sim;

import se.kth.app.sim.behaviour.Behaviour;
import se.kth.system.HostMngrComp;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by victoraxelsson on 2017-05-21.
 */
public class NodeStarter implements Operation1<StartNodeEvent, Integer>{

    private Behaviour behaviour;

    public NodeStarter(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public StartNodeEvent generate(final Integer nodeId) {
        return new StartNodeEvent() {
            KAddress selfAdr;

            {
                String nodeIp = "193.0.0." + nodeId;
                selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeId);
            }

            @Override
            public Address getNodeAddress() {
                return selfAdr;
            }

            @Override
            public Class getComponentDefinition() {
                return HostMngrComp.class;
            }

            @Override
            public HostMngrComp.Init getComponentInit() {
                return new HostMngrComp.Init(selfAdr, ScenarioSetup.bootstrapServer, ScenarioSetup.croupierOId, behaviour);
            }

            @Override
            public Map<String, Object> initConfigUpdate() {
                Map<String, Object> nodeConfig = new HashMap<>();
                nodeConfig.put("system.id", nodeId);
                nodeConfig.put("system.seed", ScenarioSetup.getNodeSeed(nodeId));
                nodeConfig.put("system.port", ScenarioSetup.appPort);
                return nodeConfig;
            }
        };
    }
}
