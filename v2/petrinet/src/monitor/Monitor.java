package v2.petrinet.src.monitor;

import v2.petrinet.src.Setup;
import v2.petrinet.src.models.PetriNet;
import v2.petrinet.src.models.Transition;
import v2.petrinet.src.threads.SimulationThread;
import v2.petrinet.src.utils.Logger;

import java.util.ArrayList;

public class Monitor implements MonitorInterface {

    /*
     * VARIABLES
     */

    // List of threads that are running the simulation and their states ('0'=stopped, '1'=running, '2'=stopping)
    private static ArrayList<SimulationThread> simulationThreads;
    private static Monitor monitorInstance;

    /*
     * CONSTRUCTORS
     */

    private Monitor() {
        simulationThreads = new ArrayList<>();
        for (Integer i = 0; i < Setup.getThreadsQuantity(); i++) {
            ArrayList<Transition> transitions = new ArrayList<>();
            for (Integer j = 0; j < Setup.getTransitionsQuantity(); j++) {
                if (Setup.getThreadsTransitionsMatrix()[i][j] == 1) {
                    transitions.add(PetriNet.getTransitions().get(j));
                }
            }
            Transition[] transitionLimits = new Transition[2];
            transitionLimits[0] = PetriNet.getTransitions().get(Setup.getThreadsTransitionLimitsMatrix()[i][0]);
            transitionLimits[1] = PetriNet.getTransitions().get(Setup.getThreadsTransitionLimitsMatrix()[i][1]);
            simulationThreads.add(new SimulationThread(
                    i,
                    transitions,
                    transitionLimits));
        }
    }

    /*
     * METHODS
     */

    @Override
    public final synchronized Boolean fireTransition(Integer transitionId) {
        // Check if the transition is sensibilized (have the required tokens in the input places)
        if (PetriNet.getTransitions().get(transitionId).isSensibilized()) {
            Integer trackedTokenId = PetriNet.getTransitions().get(transitionId).fireTransition();
            // Get the thread that contains the fired transitions
            SimulationThread thread = simulationThreads.stream()
                    .filter(t -> t.getTransitions().contains(PetriNet.getTransitions().get(transitionId)))
                    .findFirst()
                    .orElse(null);
            // Log the transition firing
            Logger.logTransitionFiring(
                    thread,
                    PetriNet.getTransitions().get(transitionId),
                    trackedTokenId,
                    true);
            return true;
        }
        return false;
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final Monitor getMonitorInstance() {
        if (monitorInstance == null) {
            monitorInstance = new Monitor();
        }
        return monitorInstance;
    }
}
