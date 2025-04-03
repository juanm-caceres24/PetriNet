package petrinet.src.models;

import petrinet.src.monitor.Monitor;
import petrinet.src.utils.Logger;
import petrinet.src.Setup;

import java.util.ArrayList;

public class Segment implements Runnable {

    /*
     * VARIABLES
     */

    private Integer segmentId;
    private ArrayList<Place> places;
    private ArrayList<Transition> transitions;
    private Place[] placeLimits;

    // Variables to analize conflicts and apply Policy
    private Transition[] transitionLimits;
    private ArrayList<Transition> conflictedTransitions;

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
        
        // Search the transitions that are the limits of the segment
        transitionLimits = new Transition[2];
        for (Transition transition : transitions) {
            if (transition.getInputPlaces().contains(placeLimits[0])) {
                transitionLimits[0] = transition;
            }
            if (transition.getOutputPlaces().contains(placeLimits[1])) {
                transitionLimits[1] = transition;
            }
        }

        // Search all the others transitions of the others segments that have the same input place
        conflictedTransitions = new ArrayList<>();
        for (Transition transition : PetriNet.getTransitions()) {
            if (transition.getInputPlaces().contains(placeLimits[0]) && transition != transitionLimits[0]) {
                conflictedTransitions.add(transition);
            }
        }
    }

    /*
     * METHODS
     */

    @Override
    public void run() {

        // Set the state of the segment to running
        Monitor.setThreadState(segmentId, 1);

        // Iterate all the transitions while the segment isn't stopped
        while (Monitor.getThreadsState().get(segmentId) != 0) {

            // Check if the segment is empty and the state of the segment is stopping
            if (Monitor.getThreadsState().get(segmentId) == 2 && this.isSegmentEmpty()) {
                this.stopSegment();
                break;
            }

            // Iterate all the transitions of the segment and fire them if possible
            transitions.forEach(transition -> {

                // Check if the transition is the start of the segment and if it should take a new token
                if (transition == transitionLimits[0] && !this.shouldTakeNewToken()) {
                    transition.setIsWaiting(false);
                }

                // If transition is waiting, process it, else prepare it
                if (transition.getIsWaiting()) {
                    this.processTransition(transition);
                } else {
                    this.prepareTransition(transition);
                }
            });
        }
    }

    private Boolean isSegmentEmpty() {
        return places.stream()
                .filter(place -> place != placeLimits[0] && place != placeLimits[1] && place.getIsTracked())
                .mapToInt(place -> place.getTokens().size())
                .sum() == 0;
    }

    private void stopSegment() {
        Monitor.setThreadState(segmentId, 0);
        Monitor.acquireLogger();
        Logger.logThreadsState();
        Monitor.releaseLogger();
    }

    private void processTransition(Transition transition) {
        Monitor.acquirePlace(transition.getInputPlaces());
        if (transition.getDelayTime() <= System.currentTimeMillis() && transition.canFire()) {
            Monitor.acquirePlace(transition.getOutputPlaces());
            Logger.addTransitionByTokenLog(
                    transition.fireTransition(),
                    transition.getTransitionId());
            this.logTransition(transition);
            this.checkSegmentStoppingCondition(transition);
            Monitor.releasePlace(transition.getOutputPlaces());
        }
        Monitor.releasePlace(transition.getInputPlaces());
    }

    private void prepareTransition(Transition transition) {
        Monitor.acquirePlace(transition.getInputPlaces());
        if (transition.canFire()) {
            transition.randomizeDelayTime();
        }
        Monitor.releasePlace(transition.getInputPlaces());
    }

    private void logTransition(Transition transition) {
        Logger.incrementTransitionFireCounter(transition.getTransitionId());
        if (transition == transitionLimits[1]) {
            Logger.incrementSegmentCompletionCounter(segmentId);
        }
        Monitor.acquireLogger();
        Logger.logTransitionFiring(
                transition,
                true,
                true);
        Monitor.releaseLogger();
    }

    private void checkSegmentStoppingCondition(Transition transition) {
        if (transition == transitionLimits[0] && getTotalFireCounter() >= Setup.getMaxSegmentCompletionCounter()) {
            Monitor.setThreadState(segmentId, 2);
            PetriNet.getSegments().stream()
                    .filter(segment -> segment.getSegmentId() != segmentId && segment.getPlaceLimits()[0] == placeLimits[0])
                    .forEach(segment -> Monitor.setThreadState(segment.getSegmentId(), 2));
            Monitor.acquireLogger();
            Logger.logThreadsState();
            Monitor.releaseLogger();
        }
    }

    private Integer getTotalFireCounter() {
        return conflictedTransitions.stream()
                .mapToInt(t -> Logger.getTransitionFireCounters().get(t.getTransitionId()))
                .sum() + Logger.getTransitionFireCounters().get(transitionLimits[0].getTransitionId());
    }

    private Boolean shouldTakeNewToken() {

        // If the segment is empty or the segment is not running, return true
        if (conflictedTransitions.isEmpty() || Monitor.getThreadsState().get(segmentId) != 1) {
            return true;
        }

        // Count the total number of fires of the conflicted transitions and the starting transition of the segment
        Integer totalFires = conflictedTransitions.stream()
                .mapToInt(t -> Logger.getTransitionFireCounters().get(t.getTransitionId()))
                .sum() + Logger.getTransitionFireCounters().get(transitionLimits[0].getTransitionId());

        // If there are no fires, return true
        if (totalFires == 0) {
            return true;
        }

        // Calculate the probability of the transition and compare it with the policy
        Float transitionProbability = (float) Logger.getTransitionFireCounters().get(transitionLimits[0].getTransitionId()) / totalFires;
        return transitionProbability <= Policy.getProbabilites().get(segmentId);
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getSegmentId() { return segmentId; }

    public ArrayList<Place> getPlaces() { return places; }

    public ArrayList<Transition> getTransitions() { return transitions; }

    public Place[] getPlaceLimits() { return placeLimits; }

    public Transition[] getTransitionsLimits() { return transitionLimits; }

    public ArrayList<Transition> getConflictedTransitions() { return conflictedTransitions; }
}
