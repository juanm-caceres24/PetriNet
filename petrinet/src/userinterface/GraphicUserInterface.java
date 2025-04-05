package petrinet.src.userinterface;

import petrinet.src.models.Transition;

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
    public final void showSegments() { }

    @Override
    public final void showPaths() { }

    @Override
    public final void showPolicy() { }

    @Override
    public final void showThreadsState() { }

    @Override
    public final void showTransitionFiring(
            Transition transition,
            Boolean showMinimal,
            Boolean showSegmentsCompletionCounters) { }
    
    @Override
    public final void showStartSimulation(Boolean showMinimal) { }

    @Override
    public final void showEndSimulation(Boolean showMinimal) { }

    @Override
    public final void showElapsedTime() { }

    @Override
    public final void showTransitionsByToken() { }

    @Override
    public final void showTransitionFireCounters() { }

    @Override
    public final void showSegmentCompletionCounters() { }
}
