package v2.petrinet.src.monitor;

import v2.petrinet.src.Main;
import v2.petrinet.src.Setup;
import v2.petrinet.src.models.PetriNet;
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
        simulationThreads = Main.getSimulationThreads();
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
            // If some path complete the maximum thread completion counter, set all the threads to stopping state
            if (Logger.getPathsCounters().contains(Setup.getMaxThreadCompletionCounter())) {
                for (SimulationThread t : simulationThreads) {
                    t.setThreadState(2);
                }
            }
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
