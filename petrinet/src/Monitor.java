import java.util.ArrayList;
import java.util.Scanner;

public class Monitor implements MonitorInterface {

    /*
     * VARIABLES
     */

    // Indicates the state of the simulation: 'false'=stopped, 'true'=running
    private static Boolean simulationIsRunning;

    // List of threads that are running the simulation
    private static ArrayList<Thread> threads;

    /*
     * CONSTRUCTORS
     */

    private Monitor() { }

    /*
     * METHODS
     */

    public static final void initializeMonitorr() {
        simulationIsRunning = false;
        threads = new ArrayList<>();
    }

    public static final void startSimulationMode() {

        // Show start of simulation mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.showStartSimulation(true);

        // Create threads for each segment
        for (Segment segment : PetriNet.getSegments()) {
            threads.add(new Thread(segment));
        }

        // Start simulation mode
        simulationIsRunning = true;
        for (Thread thread : threads) {
            thread.start();
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
    }

    public static final void startManualMode() {
    
        // Show start of manual mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.showStartSimulation(true);

        // Start manual mode
        simulationIsRunning = true;
        Scanner scanner = new Scanner(System.in);
        while (simulationIsRunning) {
            System.out.print("                    >>> | Enter transition ID to fire ('exit'=quit): ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                simulationIsRunning = false;
                break;
            } else {
                try {
                    Integer transitionId = Integer.parseInt(input);
                    if (PetriNet.getTransitions().get(transitionId).canFire()) {
                        PetriNet.getTransitions().get(transitionId).fireTransition();
                        Logger.incrementTransitionFireCounter(transitionId);
                        Logger.showTransitionFiring(PetriNet.getTransitions().get(transitionId), true);
                    } else {
                        System.out.println("                    >>> | ERROR: Transition cannot be fired.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("                    >>> | ERROR: Invalid input.");
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("                    >>> | ERROR: Invalid input.");
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
            Logger.showTransitionFiring(PetriNet.getTransitions().get(transitionId), true);
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

    public static final Boolean getSimulationIsRunning() { return simulationIsRunning; }

    public static final void setSimulationIsRunning(Boolean simulationIsRunning) { Monitor.simulationIsRunning = simulationIsRunning; }

    public static final ArrayList<Thread> getThreads() { return threads; }
}
