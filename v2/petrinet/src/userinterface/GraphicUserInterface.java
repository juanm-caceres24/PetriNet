package v2.petrinet.src.userinterface;

import v2.petrinet.src.models.Transition;

public class GraphicUserInterface implements UserInterface {

    /*
     * METHODS
     */

    public GraphicUserInterface() { }

    @Override
    public final String requestUserInterface() { return null; }

    @Override
    public final String requestModeSelection() { return null; }

    @Override
    public final String requestTransitionToFire() { return null; }

    @Override
    public final void showErrorMessage(Integer code) { }

    @Override
    public final void showTokens() { }

    @Override
    public final void showPlaces(
            Boolean showMinimal,
            Boolean showTitle,
            Boolean showIsTracked) { }
    
    @Override
    public final void showTransitions() { }

    @Override
    public final void showPolicy() { }

    @Override
    public final void showThreadsState() { }

    @Override
    public final void showTransitionFiring(
            Transition transition,
            Boolean showMinimal) { }
    
    @Override
    public final void showStartOfSimulation() { }

    @Override
    public final void showEndOfSimulation() { }

    @Override
    public final void showTransitionsByToken() { }

    @Override
    public final void showElapsedTime() { }

    @Override
    public final void showPaths(Boolean showTitle) { }

    @Override
    public final void showTransitionsFireCounters() { }

    @Override
    public final void showThreadsCompletionCounters() { }
}
