package v2.petrinet.src.monitor;

import v2.petrinet.src.Main;
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
    private static ArrayList<Thread> threads;

    /*
     * CONSTRUCTORS
     */

    public Monitor() {
        threads = new ArrayList<>();
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
            threads.add(new Thread(new SimulationThread(
                    i,
                    transitions,
                    transitionLimits,
                    this)));
        }
    }

    /*
     * METHODS
     */

    @Override
    public final synchronized Boolean fireTransition(Integer transitionId) {
        if (PetriNet.getTransitions().get(transitionId).isSensibilized()) {
            Integer trackedTokenId = PetriNet.getTransitions().get(transitionId).fireTransition();
            Segment segment = PetriNet.getSegments().stream()
                    .filter(s -> s.getTransitions().contains(PetriNet.getTransitions().get(transitionId)))
                    .findFirst()
                    .orElse(null);
            Logger.logTransitionFiring(
                    segment,
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

    // (none)
}
