package v1.petrinet.src.utils;

import v1.petrinet.src.Main;
import v1.petrinet.src.Setup;
import v1.petrinet.src.models.PetriNet;
import v1.petrinet.src.models.Place;
import v1.petrinet.src.models.Segment;
import v1.petrinet.src.models.Token;
import v1.petrinet.src.models.Transition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Logger {

    /*
     * VARIABLES
     */

    // File paths
    private static String TRANSITIONS_FILE_PATH = "v1/petrinet/logs/transitions.txt";
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
        // If the transitions.txt file exists, delete it
        File transitionsFile = new File(TRANSITIONS_FILE_PATH);
        if (transitionsFile.exists()) {
            transitionsFile.delete();
        }
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

    public static final void logPlaces(
            Boolean logMinimal,
            Boolean logTitle,
            Boolean logIsTracked) {
        Main.getUserInterface().showPlaces(
                logMinimal,
                logTitle,
                logIsTracked);
    }

    public static final void logTransitions() {
        Main.getUserInterface().showTransitions();
    }

    public static final void logSegments() {
        Main.getUserInterface().showSegments();
    }

    public static final void logPaths() {
        Main.getUserInterface().showPaths(true);
    }

    public static final void logPolicy() {
        Main.getUserInterface().showPolicy();
    }

    public static final void logThreadsState() {
        Main.getUserInterface().showThreadsState();
    }

    public static final void logTransitionFiring(
            Segment segment,
            Transition transition,
            Integer trackedTokenId,
            Boolean logMinimal) {
        // Add the transition to the list of transitions fired by the tracked token
        transitionsByTokenLogs.get(trackedTokenId).add(transition.getTransitionId());
        // Increment the counter of the fired transition
        transitionFireCounters.set(
                transition.getTransitionId(),
                transitionFireCounters.get(transition.getTransitionId()) + 1);
        // Increment the counter of the segment when fires the last transition of the segment
        if (transition == segment.getTransitionLimits()[1]) {
            segmentCompletionCounters.set(
                    segment.getSegmentId(),
                    segmentCompletionCounters.get(segment.getSegmentId()) + 1);
        }
        // Check if this transition completes any path for the tracked token
        ArrayList<Integer> transitionsOfToken = transitionsByTokenLogs.get(trackedTokenId);
        for (int i = 0; i < paths.size(); i++) {
            ArrayList<Integer> path = paths.get(i);
            // Check if the sequence of transitions of the token matches this path
            if (transitionsOfToken.size() >= path.size()) {
                boolean completedPath = true;
                // Check if the last 'path.size()' transitions match the path
                for (int j = 0; j < path.size(); j++) {
                    if (!transitionsOfToken.get(transitionsOfToken.size() - path.size() + j).equals(path.get(j))) {
                        completedPath = false;
                        break;
                    }
                }
                if (completedPath) {
                    // Increment the counter for this path
                    pathsCounters.set(i, pathsCounters.get(i) + 1);
                }
            }
        }
        // Add the transition to the transitions.txt file
        Logger.dumpIntoFile(
                "T" + transition.getTransitionId(),
                TRANSITIONS_FILE_PATH);

        // Show the transition firing
        Main.getUserInterface().showTransitionFiring(
                transition,
                logMinimal);
    }

    public static final void logStartSimulation() {
        Main.getUserInterface().showStartSimulation();
    }

    public static final void logEndSimulation() {
        Main.getUserInterface().showEndSimulation();
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
        Logger.findPaths(
                PetriNet.getPlaces().get(Setup.getPetrinetPlaceLimits()[0]),
                PetriNet.getPlaces().get(Setup.getPetrinetPlaceLimits()[0]),
                new ArrayList<>(),
                new ArrayList<>());
    }

    private static final void findPaths(
            Place startPlace,
            Place currentPlace,
            ArrayList<Integer> currentPath,
            ArrayList<Integer> visitedTransition) {
        for (Transition transition : PetriNet.getTransitions()) {
            if (transition.getInputPlaces().contains(currentPlace) && !visitedTransition.contains(transition.getTransitionId())) {
                visitedTransition.add(transition.getTransitionId());
                currentPath.add(transition.getTransitionId());
                for (Place nextPlace : transition.getOutputPlaces()) {
                    if (nextPlace.getIsTracked()) {
                        if (nextPlace.equals(startPlace)) {
                            paths.add(new ArrayList<>(currentPath));
                        } else {
                            Logger.findPaths(
                                    startPlace,
                                    nextPlace,
                                    currentPath,
                                    visitedTransition);
                        }
                    }
                }
                visitedTransition.remove(transition.getTransitionId());
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    private static final Integer dumpIntoFile(
            String data,
            String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                filePath,
                true))) {
            writer.write(data);
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final String getTransitionsFilePath() { return TRANSITIONS_FILE_PATH; }

    public static final Long getStartTime() { return startTime; }

    public static final void setStartTime(Long startTime) { Logger.startTime = startTime; }

    public static final ArrayList<ArrayList<Integer>> getTransitionsByTokenLogs() { return transitionsByTokenLogs; }

    public static final ArrayList<Integer> getTransitionFireCounters() { return transitionFireCounters; }

    public static final ArrayList<Integer> getSegmentCompletionCounters() { return segmentCompletionCounters; }

    public static final ArrayList<ArrayList<Integer>> getPaths() { return paths; }

    public static final ArrayList<Integer> getPathsCounters() { return pathsCounters; }

    public static final Semaphore getSemaphore() { return semaphore; }
}
