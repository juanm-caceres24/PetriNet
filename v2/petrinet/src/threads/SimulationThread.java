package v2.petrinet.src.threads;

import java.util.ArrayList;

import v2.petrinet.src.models.Transition;
import v2.petrinet.src.monitor.Monitor;

public class SimulationThread extends Thread {

    /*
     * VARIABLES
     */

    private Integer threadId;
    // State of the thread: '0'=stopped, '1'=running, '2'=sleeping
    private Integer threadState;
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
        // Set state to 'running'
        this.threadState = 1;
        // Main loop
        while (true) {
            // Iterate over all transitions
            for (Transition transition : transitions) {
                // If the thread is stopped, exit the method
                if (this.threadState == 0) {
                    return;
                } else {
                    // Fires the transition
                    Monitor.getMonitorInstance().fireTransition(transition.getTransitionId());
                    // If the thread is in 'sleeping' state, sleep for the transition delay time and then set the thread state to 'running'
                    if (this.threadState == 2) {
                        try {
                            Thread.sleep(transition.getDelayTime());
                            this.threadState = 1;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // After waking up, try to fire the transition again
                        Monitor.getMonitorInstance().fireTransition(transition.getTransitionId());
                    }
                }
            }
        }
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
