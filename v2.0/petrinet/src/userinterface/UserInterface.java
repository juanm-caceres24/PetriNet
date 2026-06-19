package v2.petrinet.src.userinterface;

import v2.petrinet.src.models.Transition;

public interface UserInterface {

    /*
     * METHODS
     */

    public abstract String requestUserInterface();
    public abstract String requestModeSelection();
    public abstract String requestTransitionToFire();
    public abstract void showErrorMessage(Integer code);
    public abstract void showTokens();
    public abstract void showPlaces(
            Boolean showMinimal,
            Boolean showTitle,
            Boolean showIsTracked);
    public abstract void showTransitions();
    public abstract void showPolicy();
    public abstract void showThreadsState();
    public abstract void showTransitionFiring(
            Transition transition,
            Boolean showMinimal);
    public abstract void showStartOfSimulation();
    public abstract void showEndOfSimulation();
    public abstract void showTransitionsByToken();
    public abstract void showElapsedTime();
    public abstract void showPaths(Boolean showTitle);
    public abstract void showTransitionsFireCounters();
    public abstract void showThreadsCompletionCounters();
}
