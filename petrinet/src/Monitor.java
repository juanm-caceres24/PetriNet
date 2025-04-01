import java.util.ArrayList;
import java.util.Scanner;

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
            threads.add(new Thread(segment));
        }
        threadsState = new ArrayList<>();
        for (int i = 0; i < threads.size(); i++) {
            threadsState.add(0);
        }
    }

    public static final void startSimulationMode() {

        // Show start of simulation mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.showThreadsState();
        Logger.showStartSimulation(true);

        // Start simulation mode
        for (Thread thread : threads) {
            thread.start();
            Monitor.acquireLogger();
            Logger.showThreadsState();
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

        // Show end of simulation mode
        Logger.showEndSimulation(true);
        Logger.showThreadsState();
    }

    public static final void startManualMode() {
    
        // Show start of manual mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.showStartSimulation(true);

        // Start manual mode
        Boolean isRunning = true;
        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            System.out.print("                                   >>> | Enter transition ID to fire ('exit'=quit): ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                isRunning = false;
                break;
            } else {
                try {
                    Integer transitionId = Integer.parseInt(input);
                    if (PetriNet.getTransitions().get(transitionId).canFire()) {
                        PetriNet.getTransitions().get(transitionId).fireTransition();
                        Logger.incrementTransitionFireCounter(transitionId);
                        Logger.showTransitionFiring(
                                PetriNet.getTransitions().get(transitionId),
                                true,
                                false);
                    } else {
                        System.out.println("                                   >>> | ERROR: Transition cannot be fired.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("                                   >>> | ERROR: Invalid input.");
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("                                   >>> | ERROR: Invalid input.");
                }
            }
        }
        scanner.close();

        // Show end of manual mode
        Logger.showEndSimulation(true);
    }

    @Override
    public final synchronized Boolean fireTransition(Integer transitionId) {
        if (PetriNet.getTransitions().get(transitionId).canFire()) {
            PetriNet.getTransitions().get(transitionId).fireTransition();
            Logger.incrementTransitionFireCounter(transitionId);
            Logger.showTransitionFiring(
                    PetriNet.getTransitions().get(transitionId),
                    true,
                    false);
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

    public static final void setThreadState(
            Integer threadId,
            Integer state) {
        
        threadsState.set(threadId, state);
        
    }
}
