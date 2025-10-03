package v2.petrinet.src.threads;

import java.util.ArrayList;

import v2.petrinet.src.models.Transition;
import v2.petrinet.src.monitor.Monitor;

public class SimulationThread extends Thread {

    /*
     * VARIABLES
     */

    private Integer threadId;
    private Integer threadState; // '0'=stopped, '1'=running, '2'=stopping
    private ArrayList<Transition> transitions;
    private Transition[] transitionLimits;

    /*
     * CONSTRUCTORS
     */

    public SimulationThread(
            Integer threadId,
            ArrayList<Transition> transitions,
            Transition[] transitionLimits) {

        this.threadId = threadId;
        this.threadState = 0;
        this.transitions = transitions;
        this.transitionLimits = transitionLimits;
    }
    
    /*
     * METHODS
     */
    
    @Override
    public final void run() {
        this.threadState = 1; // Set state to 'running'
        while (this.threadState.equals(1) || this.threadState.equals(2)) {
            for (Transition transition : transitions) {
                Monitor.getMonitorInstance().fireTransition(transition.getTransitionId());
                if (transition.equals(transitionLimits[1]) && this.threadState.equals(2)) {
                    break;
                }
            }
        }
        this.threadState = 0; // Set state to 'stopping'
    }

    /*
     * GETTERS AND SETTERS
     */

    public final Integer getThreadId() { return threadId; }

    public final Integer getThreadState() { return threadState; }

    public final void setThreadState(Integer threadState) { this.threadState = threadState; }

    public final ArrayList<Transition> getTransitions() { return transitions; }

    public final Transition[] getTransitionLimits() { return transitionLimits; }
}
