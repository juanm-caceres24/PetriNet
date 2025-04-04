package petrinet.src.monitor;

import petrinet.src.Main;
import petrinet.src.models.PetriNet;
import petrinet.src.models.Place;
import petrinet.src.models.Segment;
import petrinet.src.threads.SegmentThread;
import petrinet.src.utils.Logger;

import java.util.ArrayList;

public class Monitor implements MonitorInterface {

    /*
     * VARIABLES
     */

    // List of threads that are running the simulation and their states ('0'=stopped, '1'=running, '2'=stopping)
    private static ArrayList<Thread> threads;
    private static ArrayList<Integer> threadsState;

    /*
     * CONSTRUCTORS
     */

    private Monitor() { }

    /*
     * METHODS
     */

    public static final void initializeMonitor() {
        threads = new ArrayList<>();
        for (Segment segment : PetriNet.getSegments()) {
            threads.add(new Thread(new SegmentThread(segment)));
        }
        threadsState = new ArrayList<>();
        for (int i = 0; i < threads.size(); i++) {
            threadsState.add(0);
        }
    }

    public static final void startSimulationMode() {
        // Log start of simulation mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.logStartSimulation(true);
        // Start simulation mode
        for (Thread thread : threads) {
            thread.start();
            Monitor.acquireLogger();
            Logger.logThreadsState();
            Monitor.releaseLogger();
        }
        // Wait for all segments to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Log end of simulation mode
        Logger.logEndSimulation(true);
    }

    public static final void startManualMode() {
        // Log start of manual mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.logStartSimulation(true);
        // Start manual mode
        while (true) {
            String input = Main.getUserInterface().requestTransitionToFire();
            if (input.equals("exit")) {
                break;
            } else {
                try {
                    Integer transitionId = Integer.parseInt(input);
                    if (PetriNet.getTransitions().get(transitionId).canFire()) {
                        PetriNet.getTransitions().get(transitionId).fireTransition();
                        Logger.incrementTransitionFireCounter(transitionId);
                        Logger.logTransitionFiring(PetriNet.getTransitions().get(transitionId), true, false);
                    } else {
                        Main.getUserInterface().showErrorMessage(1);
                    }
                } catch (NumberFormatException e) {
                    Main.getUserInterface().showErrorMessage(0);
                } catch (IndexOutOfBoundsException e) {
                    Main.getUserInterface().showErrorMessage(0);
                }
            }
        }
        // Log end of manual mode
        Logger.logEndSimulation(true);
    }

    @Override
    public final synchronized Boolean fireTransition(Integer transitionId) {
        if (PetriNet.getTransitions().get(transitionId).canFire()) {
            PetriNet.getTransitions().get(transitionId).fireTransition();
            Logger.incrementTransitionFireCounter(transitionId);
            Logger.logTransitionFiring(PetriNet.getTransitions().get(transitionId), true, false);
            return true;
        }
        return false;
    }

    public static final void acquirePlace(ArrayList<Place> places) {
        for (Place place : places) {
            try {
                place.getSemaphore().acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static final void acquireLogger() {
        try {
            Logger.getSemaphore().acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final void releasePlace(ArrayList<Place> places) {
        for (Place place : places) {
            place.getSemaphore().release();
        }
    }

    public static final void releaseLogger() {
        Logger.getSemaphore().release();
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final ArrayList<Thread> getThreads() { return threads; }

    public static final ArrayList<Integer> getThreadsState() { return threadsState; }

    public static final void setThreadState(Integer threadId, Integer state) { threadsState.set(threadId, state); }
}
