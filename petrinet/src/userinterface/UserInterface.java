package petrinet.src.userinterface;

import petrinet.src.models.Transition;

public interface UserInterface {

    /*
     * METHODS
     */

    public abstract void askForUserUserInterface();
    public abstract void askForModeSelection();
    public abstract void showTokens();
    public abstract void showPlaces(
            Boolean showMinimal,
            Boolean showTitle,
            Boolean showIsTracked);
    public abstract void showTransitions();
    public abstract void showSegments();
    public abstract void showPaths();
    public abstract void showPolicy();
    public abstract void showThreadsState();
    public abstract void showTransitionFiring(
            Transition transition,
            Boolean showMinimal,
            Boolean showSegmentsCompletionCounters);
    public abstract void showStartSimulation(Boolean showMinimal);
    public abstract void showEndSimulation(Boolean showMinimal);
    public abstract void showElapsedTime();
    public abstract void showTransitionsByToken();
    public abstract void showTransitionFireCounters();
    public abstract void showSegmentCompletionCounters();
}
