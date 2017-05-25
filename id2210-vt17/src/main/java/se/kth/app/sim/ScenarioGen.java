/*
 * 2016 Royal Institute of Technology (KTH)
 *
 * LSelector is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.app.sim;

import se.kth.app.sim.behaviour.sim1.PBehaviour;
import se.kth.app.sim.behaviour.sim1.QBehaviour;
import se.kth.sim.compatibility.SimNodeIdExtractor;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class ScenarioGen {

    static Operation<SetupEvent> systemSetupOp = new Operation<SetupEvent>() {
        @Override
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public IdentifierExtractor getIdentifierExtractor() {
                    return new SimNodeIdExtractor();
                }
            };
        }
    };

    static Operation<StartNodeEvent> startBootstrapServerOp = new Operation<StartNodeEvent>() {

        @Override
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = ScenarioSetup.bootstrapServer;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return BootstrapServerComp.class;
                }

                @Override
                public BootstrapServerComp.Init getComponentInit() {
                    return new BootstrapServerComp.Init(selfAdr);
                }
            };
        }
    };

    static Operation1<StartNodeEvent, Integer> startNodeOp = new NodeStarter(null);
    static Operation1<StartNodeEvent, Integer> startSim1PNode = new NodeStarter(new se.kth.app.sim.behaviour.sim1.PBehaviour());
    static Operation1<StartNodeEvent, Integer> startSim1QNode = new NodeStarter(new se.kth.app.sim.behaviour.sim1.QBehaviour());

    static Operation1<StartNodeEvent, Integer> startSim2PNode = new NodeStarter(new se.kth.app.sim.behaviour.sim2.PBehaviour());
    static Operation1<StartNodeEvent, Integer> startSim2QNode = new NodeStarter(new se.kth.app.sim.behaviour.sim2.QBehaviour());

    static Operation1<StartNodeEvent, Integer> startSim3PNode = new NodeStarter(new se.kth.app.sim.behaviour.sim3.PBehaviour());
    static Operation1<StartNodeEvent, Integer> startSim3QNode = new NodeStarter(new se.kth.app.sim.behaviour.sim3.QBehaviour());

    static Operation1<StartNodeEvent, Integer> startSim4PNode = new NodeStarter(new se.kth.app.sim.behaviour.sim4.PBehaviour());
    static Operation1<StartNodeEvent, Integer> startSim4QNode = new NodeStarter(new se.kth.app.sim.behaviour.sim4.QBehaviour());


    static Operation1<StartNodeEvent, Integer> startSim6PNode = new NodeStarter(new se.kth.app.sim.behaviour.sim6.PBehaviour());
    static Operation1<StartNodeEvent, Integer> startSim6QNode = new NodeStarter(new se.kth.app.sim.behaviour.sim6.QBehaviour());

    static Operation1<StartNodeEvent, Integer> startSim5PNode = new NodeStarter(new se.kth.app.sim.behaviour.sim5.PBehaviour());
    static Operation1<StartNodeEvent, Integer> startSim5QNode = new NodeStarter(new se.kth.app.sim.behaviour.sim5.QBehaviour());


    public static SimulationScenario simpleBoot() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPeers = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(100, startNodeOp, new BasicIntSequentialDistribution(1));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPeers.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000 * 1000, startPeers);
            }
        };

        return scen;
    }

    public static SimulationScenario multipleRegisterSimulation() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startAnAdder = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim1PNode, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startAnAssigner = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim1QNode, new BasicIntSequentialDistribution(2));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startAnAdder.startAfterTerminationOf(1000, startBootstrapServer);
                startAnAssigner.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000 * 1000, startAnAssigner);
            }
        };

        return scen;
    }

    public static SimulationScenario modifyNestedMapSimulation() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim2PNode, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startQNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim2QNode, new BasicIntSequentialDistribution(2));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPNode.startAfterTerminationOf(1000, startBootstrapServer);
                startQNode.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000 * 1000, startQNode);
            }
        };

        return scen;
    }

    public static SimulationScenario editSameListSimulation() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim3PNode, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startQNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim3QNode, new BasicIntSequentialDistribution(2));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPNode.startAfterTerminationOf(1000, startBootstrapServer);
                startQNode.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000 * 1000, startQNode);
            }
        };

        return scen;
    }

    public static SimulationScenario editConcurrentListSimulation() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim4PNode, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startQNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim4QNode, new BasicIntSequentialDistribution(2));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPNode.startAfterTerminationOf(1000, startBootstrapServer);
                startQNode.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000 * 1000, startQNode);
            }
        };

        return scen;
    }

    public static SimulationScenario concurrentMapEdit()  {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim6PNode, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startQNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim6QNode, new BasicIntSequentialDistribution(2));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPNode.startAfterTerminationOf(1000, startBootstrapServer);
                startQNode.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000 * 1000, startQNode);
            }
        };

        return scen;
    }

    public static SimulationScenario assigningDifferentTypesSimulation() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess systemSetup = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, systemSetupOp);
                    }
                };
                StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                StochasticProcess startPNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim5PNode, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startQNode = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startSim5QNode, new BasicIntSequentialDistribution(2));
                    }
                };

                systemSetup.start();
                startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
                startPNode.startAfterTerminationOf(1000, startBootstrapServer);
                startQNode.startAfterTerminationOf(1000, startBootstrapServer);
                terminateAfterTerminationOf(1000 * 1000, startQNode);
            }
        };

        return scen;
    }
}
