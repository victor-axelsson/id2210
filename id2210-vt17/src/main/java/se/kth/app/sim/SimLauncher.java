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

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class SimLauncher {
    public static void main(String[] args) {
        SimulationScenario.setSeed(ScenarioSetup.scenarioSeed);
        //SimulationScenario simpleBootScenario = ScenarioGen.simpleBoot();

        //SIM 1
        //SimulationScenario sc = ScenarioGen.multipleRegisterSimulation();

        //SIM 2
        //SimulationScenario sc = ScenarioGen.modifyNestedMapSimulation();

        //SIM 3
        //SimulationScenario sc = ScenarioGen.editSameListSimulation();

        //SIM 4
        //SimulationScenario sc = ScenarioGen.editConcurrentListSimulation();

        //SIM 5
        //SimulationScenario sc = ScenarioGen.assigningDifferentTypesSimulation();

        //SIM 6
        //SimulationScenario sc = ScenarioGen.concurrentMapEdit();

        //Nested maps
        SimulationScenario sc = ScenarioGen.nestedMapSimulation();

        sc.simulate(LauncherComp.class);
        //simpleBootScenario.simulate(LauncherComp.class);
    }
}
