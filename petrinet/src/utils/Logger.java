package petrinet.src.utils;

import petrinet.src.Main;
import petrinet.src.Setup;
import petrinet.src.models.PetriNet;
import petrinet.src.models.Place;
import petrinet.src.models.Segment;
import petrinet.src.models.Token;
import petrinet.src.models.Transition;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Logger {

    /*
     * VARIABLES
     */

    // Variables to track simulation states, counters and statistics
    private static Long startTime;
    private static ArrayList<ArrayList<Integer>> transitionsByTokenLogs;
    private static ArrayList<Integer> transitionFireCounters;
    private static ArrayList<Integer> segmentCompletionCounters;
    private static ArrayList<ArrayList<Integer>> paths;
    private static ArrayList<Integer> pathsCounters;
    // Semaphore to control access to the logger
    private static Semaphore semaphore;

    /*
     * CONSTRUCTORS
     */

    private Logger() { }

    /*
     * METHODS
     */

    public static final void initializeLogger() {
        startTime = null;
        transitionsByTokenLogs = new ArrayList<>();
        for (Token token : PetriNet.getTokens()) {
            if (token.getIsTracked()) {
                transitionsByTokenLogs.add(new ArrayList<>());
            }
        }
        transitionFireCounters = new ArrayList<>();
        for (int i = 0; i < PetriNet.getTransitions().size(); i++) {
            transitionFireCounters.add(0);
        }
        segmentCompletionCounters = new ArrayList<>();
        for (int i = 0; i < PetriNet.getSegments().size(); i++) {
            segmentCompletionCounters.add(0);
        }
        semaphore = new Semaphore(1);
        paths = new ArrayList<>();
        Logger.initializePaths();
        pathsCounters = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            pathsCounters.add(0);
        }
        Logger.logPaths();
    }

    public static final void logTokens() {
        Main.getUserInterface().showTokens();
    }

    public static final void logPlaces(Boolean logMinimal, Boolean logTitle, Boolean logIsTracked) {
        Main.getUserInterface().showPlaces(logMinimal, logTitle, logIsTracked);
    }

    public static final void logTransitions() {
        Main.getUserInterface().showTransitions();
    }

    public static final void logSegments() {
        Main.getUserInterface().showSegments();
    }

    public static final void logPaths() {
        Main.getUserInterface().showPaths();
    }

    public static final void logPolicy() {
        Main.getUserInterface().showPolicy();
    }

    public static final void logThreadsState() {
        Main.getUserInterface().showThreadsState();
    }

    public static final void logTransitionFiring(Segment segment, Transition transition, Integer trackedTokenId, Boolean logMinimal, Boolean logSegmentsCompletionCounters) {
        transitionsByTokenLogs.get(trackedTokenId).add(transition.getTransitionId());
        transitionFireCounters.set(transition.getTransitionId(), transitionFireCounters.get(transition.getTransitionId()) + 1);
        if (transition == segment.getTransitionLimits()[1]) {
            segmentCompletionCounters.set(segment.getSegmentId(), segmentCompletionCounters.get(segment.getSegmentId()) + 1);
        }
        Main.getUserInterface().showTransitionFiring(transition, logMinimal, logSegmentsCompletionCounters);
    }

    public static final void logStartSimulation(Boolean logMinimal) {
        Main.getUserInterface().showStartSimulation(logMinimal);
    }

    public static final void logEndSimulation(Boolean logMinimal) {
        Main.getUserInterface().showEndSimulation(logMinimal);
    }

    public static final void logElapsedTime() {
        Main.getUserInterface().showElapsedTime();
    }

    public static final void logTransitionsByToken() {
        Main.getUserInterface().showTransitionsByToken();
    }

    public static final void logTransitionFireCounters() {
        Main.getUserInterface().showTransitionFireCounters();
    }

    public static final void logSegmentCompletionCounters() {
        Main.getUserInterface().showSegmentCompletionCounters();
    }

    private static final void initializePaths() {
        Logger.findPaths(PetriNet.getPlaces().get(Setup.getPetrinetPlaceLimits()[0]), PetriNet.getPlaces().get(Setup.getPetrinetPlaceLimits()[0]), new ArrayList<>(), new ArrayList<>());
    }

    private static final void findPaths(Place startPlace, Place currentPlace, ArrayList<Integer> currentPath, ArrayList<Integer> visitedTransition) {
        for (Transition transition : PetriNet.getTransitions()) {
            if (transition.getInputPlaces().contains(currentPlace) && !visitedTransition.contains(transition.getTransitionId())) {
                visitedTransition.add(transition.getTransitionId());
                currentPath.add(transition.getTransitionId());
                for (Place nextPlace : transition.getOutputPlaces()) {
                    if (nextPlace.getIsTracked()) {
                        if (nextPlace.equals(startPlace)) {
                            paths.add(new ArrayList<>(currentPath));
                        } else {
                            Logger.findPaths(startPlace, nextPlace, currentPath, visitedTransition);
                        }
                    }
                }
                visitedTransition.remove(transition.getTransitionId());
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final Long getStartTime() { return startTime; }

    public static final void setStartTime(Long startTime) { Logger.startTime = startTime; }

    public static final ArrayList<ArrayList<Integer>> getTransitionsByTokenLogs() { return transitionsByTokenLogs; }

    public static final ArrayList<Integer> getTransitionFireCounters() { return transitionFireCounters; }

    public static final ArrayList<Integer> getSegmentCompletionCounters() { return segmentCompletionCounters; }

    public static final ArrayList<ArrayList<Integer>> getPaths() { return paths; }

    public static final ArrayList<Integer> getPathsCounters() { return pathsCounters; }

    public static final Semaphore getSemaphore() { return semaphore; }
}
