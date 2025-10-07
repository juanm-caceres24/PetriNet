package v2.petrinet.src.monitor;

import v2.petrinet.src.Main;
import v2.petrinet.src.Setup;
import v2.petrinet.src.models.PetriNet;
import v2.petrinet.src.utils.Logger;

public class Monitor implements MonitorInterface {

    /*
     * VARIABLES
     */

    private static Monitor monitorInstance;

    /*
     * CONSTRUCTORS
     */

    private Monitor() { }

    /*
     * METHODS
     */

    @Override
    public final synchronized Boolean fireTransition(Integer transitionId) {
        // Check if the transition is sensibilized (have the required tokens in the input places)
        if (PetriNet.getTransitions().get(transitionId).isSensibilized()) {
            // Fire the transition and save the ID of the tracked token, if any
            Integer trackedTokenId = PetriNet.getTransitions().get(transitionId).fireTransition();
            // Log the transition firing
            Logger.logTransitionFiring(
                    PetriNet.getTransitions().get(transitionId),
                    trackedTokenId);
            // If some path complete the maximum thread completion counter, set all the threads to stopped state
            if (Logger.getPathsCounters().contains(Setup.getMaxThreadCompletionCounter())) {
                Main.stopSimulationThreads();
            }
            // Check place invariants and stop the simulation if any invariant is violated
            if (PetriNet.getPlaceInvariant() == -1) {
                Main.stopSimulationThreads();
                Main.getUserInterface().showErrorMessage(3);
                Main.getUserInterface().showEndOfSimulation();
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
