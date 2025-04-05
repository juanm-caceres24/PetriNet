package petrinet.src.threads;

import petrinet.src.Setup;
import petrinet.src.models.PetriNet;
import petrinet.src.models.Policy;
import petrinet.src.models.Segment;
import petrinet.src.models.Transition;
import petrinet.src.monitor.Monitor;
import petrinet.src.utils.Logger;

public class SegmentThread implements Runnable {

    /*
     * VARIABLES
     */

    private Segment segment;

    /*
     * CONSTRUCTORS
     */

    public SegmentThread(Segment segment) {
        this.segment = segment;
    }
    
    /*
     * METHODS
     */

    @Override
    public void run() {
        // Set the state of the segment to running
        Monitor.setThreadState(segment.getSegmentId(), 1);
        // Iterate all the transitions while the segment isn't stopped
        while (Monitor.getThreadsState().get(segment.getSegmentId()) != 0) {
            // Check if the segment is empty and the state of the segment is stopping
            if (Monitor.getThreadsState().get(segment.getSegmentId()) == 2 && this.isSegmentEmpty()) {
                this.stopSegment();
                break;
            }
            // Iterate all the transitions of the segment and fire them if possible
            segment.getTransitions().forEach(transition -> {
                // Check if the transition is the start of the segment and if it should take a new token
                if (transition == segment.getTransitionLimits()[0] && !this.shouldTakeNewToken()) {
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
        return segment.getPlaces().stream()
                .filter(place -> place != segment.getPlaceLimits()[0] && place != segment.getPlaceLimits()[1] && place.getIsTracked())
                .mapToInt(place -> place.getTokens().size())
                .sum() == 0;
    }

    private void stopSegment() {
        Monitor.setThreadState(segment.getSegmentId(), 0);
        Monitor.acquireLogger();
        Logger.logThreadsState();
        Monitor.releaseLogger();
    }

    private void processTransition(Transition transition) {
        Monitor.acquirePlace(transition.getInputPlaces());
        if (transition.getDelayTime() <= System.currentTimeMillis() && transition.canFire()) {
            Monitor.acquirePlace(transition.getOutputPlaces());
            Integer trackedTokenId = transition.fireTransition();
            this.logTransition(
                    transition,
                    trackedTokenId);
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

    private void logTransition(
            Transition transition,
            Integer trackedTokenId) {
        Monitor.acquireLogger();
        Logger.logTransitionFiring(
                segment,
                transition,
                trackedTokenId,
                true,
                true);
        Monitor.releaseLogger();
    }

    private void checkSegmentStoppingCondition(Transition transition) {
        if (transition == segment.getTransitionLimits()[0] && getTotalFireCounter() >= Setup.getMaxSegmentCompletionCounter()) {
            Monitor.setThreadState(segment.getSegmentId(), 2);
            PetriNet.getSegments().stream()
                    .filter(s -> s.getSegmentId() != segment.getSegmentId() && s.getPlaceLimits()[0] == segment.getPlaceLimits()[0])
                    .forEach(s -> Monitor.setThreadState(s.getSegmentId(), 2));
            Monitor.acquireLogger();
            Logger.logThreadsState();
            Monitor.releaseLogger();
        }
    }

    private Integer getTotalFireCounter() {
        return segment.getConflictedTransitions().stream()
                .mapToInt(t -> Logger.getTransitionFireCounters().get(t.getTransitionId()))
                .sum() + Logger.getTransitionFireCounters().get(segment.getTransitionLimits()[0].getTransitionId());
    }

    private Boolean shouldTakeNewToken() {
        // If the segment is empty or the segment is not running, return true
        if (segment.getConflictedTransitions().isEmpty() || Monitor.getThreadsState().get(segment.getSegmentId()) != 1) {
            return true;
        }
        // Count the total number of fires of the conflicted transitions and the starting transition of the segment
        Integer totalFires = segment.getConflictedTransitions().stream()
                .mapToInt(t -> Logger.getTransitionFireCounters().get(t.getTransitionId()))
                .sum() + Logger.getTransitionFireCounters().get(segment.getTransitionLimits()[0].getTransitionId());
        // If there are no fires, return true
        if (totalFires == 0) {
            return true;
        }
        // Calculate the probability of the transition and compare it with the policy
        Float transitionProbability = (float) Logger.getTransitionFireCounters().get(segment.getTransitionLimits()[0].getTransitionId()) / totalFires;
        return transitionProbability <= Policy.getProbabilites().get(segment.getSegmentId());
    }
}
