import java.util.ArrayList;

public class Segment implements Runnable {

    /*
     * VARIABLES
     */

    private Integer segmentId;
    private ArrayList<Place> places;
    private ArrayList<Transition> transitions;
    private Place[] placeLimits;

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
    }

    /*
     * METHODS
     */
    
    @Override
    public void run() {

        // Fires possible transitions all the time while simulation is running
        while (Monitor.getSimulationIsRunning()) {
            for (Transition transition : transitions) {
                if (transition.getIsWaiting()) {

                    // Acquires semaphores from input places
                    Monitor.getPlaceSemaphore(transition.getInputPlaces());

                    // Check if transition delay time has passed and if it can fire
                    if (transition.getDelayTime() <= System.currentTimeMillis() && transition.canFire()) {

                        // Acquires semaphores from output places
                        Monitor.getPlaceSemaphore(transition.getOutputPlaces());

                        // Fires transition and logs the firing
                        transition.fireTransition();
                        Monitor.getLoggerSemaphore();
                        Logger.incrementTransitionFireCounter(transition);
                        Logger.showTransitionFiring(transition, true);
                        Monitor.releaseLoggerSemaphore();

                        // Check if the transition has fired enough times to stop the simulation
                        if (Logger.getTransitionFireCounters().get(transition.getTransitionId()) >= Setup.getMaxTransitionFireCounter()) {
                            Monitor.setSimulationIsRunning(false);
                        }

                        // Releases semaphores from output places
                        Monitor.releasePlaceSemaphore(transition.getOutputPlaces());
                    }

                    // Releases semaphores from input places
                    Monitor.releasePlaceSemaphore(transition.getInputPlaces());
                } else {

                    // Acquires semaphores from input places
                    Monitor.getPlaceSemaphore(transition.getInputPlaces());

                    // Randomizes delay time if transition can fire and set the flag isWaiting to true
                    if (transition.canFire()) {
                        transition.randomizeDelayTime();
                    }

                    // Releases semaphores from input places
                    Monitor.releasePlaceSemaphore(transition.getInputPlaces());
                }
            }
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getSegmentId() { return segmentId; }

    public ArrayList<Place> getPlaces() { return places; }

    public ArrayList<Transition> getTransitions() { return transitions; }

    public Place[] getPlaceLimits() { return placeLimits; }
}
