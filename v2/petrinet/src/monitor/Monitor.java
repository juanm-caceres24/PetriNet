package v2.petrinet.src.monitor;

import v2.petrinet.src.Main;
import v2.petrinet.src.Setup;
import v2.petrinet.src.models.PetriNet;
import v2.petrinet.src.models.Transition;
import v2.petrinet.src.utils.Logger;

public class Monitor implements MonitorInterface {

    /*
     * VARIABLES
     */

    private static Monitor monitorInstance;

    /*
     * CONSTRUCTORS
     */

    private Monitor() {
    }

    /*
     * METHODS
     */

    @Override
    public final synchronized Boolean fireTransition(Integer transitionId) {
        // Check if the transition is sensibilized (have the required tokens in the input places)
        Transition transition = PetriNet.getTransitions().get(transitionId);
        if (transition.isSensibilized()) {
            // If the transition can fire (delay time is set to null), fire it
            if (transition.canFire()) {
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
            } else {
                // If the transition can't fire, randomize the transition delay time and put the thread in 'sleeping' state
                transition.randomizeDelayTime();
                for (int i = 0; i < Setup.getThreadsQuantity(); i++) {
                    if (Setup.getThreadsTransitionsMatrix()[i][transitionId] == 1) {
                        Main.getSimulationThreads().get(i).setThreadState(2);
                        break;
                    }
                }
            }
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
