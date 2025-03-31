import java.util.ArrayList;

public class Segment implements Runnable {

    /*
     * VARIABLES
     */

    private Integer segmentId;
    private ArrayList<Place> places;
    private ArrayList<Transition> transitions;
    private Place[] placeLimits;

    // Variables to compare conflicts and apply Policy
    Transition conflictedInternalTransition;
    ArrayList<Transition> conflictedExternalTransitions;

    /*
     * CONSTRUCTORS
     */

    public Segment(
            Integer segmentId,
            ArrayList<Place> places,
            ArrayList<Transition> transitions,
            Place[] placeLimits) {

        this.segmentId = segmentId;
        this.places = places;
        this.transitions = transitions;
        this.placeLimits = placeLimits;
        this.conflictedInternalTransition = null;
        this.conflictedExternalTransitions = new ArrayList<>();
        
        // Search the transition that is in the segment and has the same input place as the segment
        for (Transition transition : transitions) {
            if (transition.getInputPlaces().contains(placeLimits[0])) {
                conflictedInternalTransition = transition;
            }
        }

        // Search all the others transitions of the others segments that have the same input place
        for (Transition transition : PetriNet.getTransitions()) {
            if (transition.getInputPlaces().contains(placeLimits[0]) && transition != conflictedInternalTransition) {
                conflictedExternalTransitions.add(transition);
            }
        }
    }

    /*
     * METHODS
     */
    
    @Override
    public void run() {

        // Fires possible transitions all the time while simulation is running
        while (Monitor.getSimulationIsRunning()) {
            for (Transition transition : transitions) {

                // If is the first transition of the segment compare with the fire counters of the others conflicted transitions
                if (transition == conflictedInternalTransition) {
                    if (!shouldTakeNewToken()) {
                        // If the transition is not allowed to fire, set the flag isWaiting to true
                        transition.setIsWaiting(false);
                    } else {
                        // If the transition is allowed to fire, set the flag isWaiting to false
                        transition.setIsWaiting(true);
                    }
                }

                // Continue with the rest of the transitions
                if (transition.getIsWaiting()) {

                    // Acquires semaphores from input places
                    Monitor.acquirePlace(transition.getInputPlaces());

                    // Check if transition delay time has passed and if it can fire
                    if (transition.getDelayTime() <= System.currentTimeMillis() && transition.canFire()) {

                        // Acquires semaphores from output places
                        Monitor.acquirePlace(transition.getOutputPlaces());

                        // Fires transition and logs the firing
                        transition.fireTransition();
                        Monitor.acquireLogger();
                        Logger.incrementTransitionFireCounter(transition.getTransitionId());
                        Logger.showTransitionFiring(transition, true);
                        Monitor.releaseLogger();

                        // Check if the transition has fired enough times to stop the simulation
                        if (Logger.getTransitionFireCounters().get(transition.getTransitionId()) >= Setup.getMaxTransitionFireCounter()) {
                            Monitor.setSimulationIsRunning(false);
                        }

                        // Releases semaphores from output places
                        Monitor.releasePlace(transition.getOutputPlaces());
                    }

                    // Releases semaphores from input places
                    Monitor.releasePlace(transition.getInputPlaces());
                } else {

                    // Acquires semaphores from input places
                    Monitor.acquirePlace(transition.getInputPlaces());

                    // Randomizes delay time if transition can fire and set the flag isWaiting to true
                    if (transition.canFire()) {
                        transition.randomizeDelayTime();
                    }

                    // Releases semaphores from input places
                    Monitor.releasePlace(transition.getInputPlaces());
                }
            }
        }
    }

    public Boolean shouldTakeNewToken() {
        if (!conflictedExternalTransitions.isEmpty()) {

            // Count the total fires of all the conflicted transitions
            Integer totalFires = Logger.getTransitionFireCounters().get(conflictedInternalTransition.getTransitionId());
            for (Transition transition : conflictedExternalTransitions) {
                totalFires += Logger.getTransitionFireCounters().get(transition.getTransitionId());
            }
            if (totalFires != 0) {

                // Check if the total fires of the conflicted transitions is less than the maximum allowed
                if ((float) Logger.getTransitionFireCounters().get(conflictedInternalTransition.getTransitionId()) / totalFires > Policy.getProbabilites().get(segmentId)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getSegmentId() { return segmentId; }

    public ArrayList<Place> getPlaces() { return places; }

    public ArrayList<Transition> getTransitions() { return transitions; }

    public Place[] getPlaceLimits() { return placeLimits; }

    public Transition getConflictedInternalTransition() { return conflictedInternalTransition; }

    public ArrayList<Transition> getConflictedExternalTransitions() { return conflictedExternalTransitions; }
}
