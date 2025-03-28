import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Monitor implements MonitorInterface {

    /*
     * VARIABLES
     */

    // Indicates the state of the simulation: 'false'=stopped, 'true'=running
    private static Boolean simulationIsRunning;

    // List of threads that are running the simulation
    private static ArrayList<Thread> threads = new ArrayList<>();

    /*
     * CONSTRUCTORS
     */

    private Monitor() {

    }

    /*
     * METHODS
     */

    public static final void startSimulation() {

        // Show start of simulation
        Logger.setStartTime(System.currentTimeMillis());
        Logger.showStartSimulation(true);

        // Create threads for each segment
        for (Segment segment : PetriNet.getSegments()) {
            threads.add(new Thread(segment));
        }

        // Start simulation
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

        // Show end of simulation
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
            System.out.print("                   >>> | Enter transition ID to fire ('exit'=quit): ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                simulationIsRunning = false;
                break;
            } else {
                try {
                    Integer transitionId = Integer.parseInt(input);
                    if (PetriNet.getTransitions().get(transitionId).canFire()) {
                        PetriNet.getTransitions().get(transitionId).fireTransition();
                        Logger.incrementTransitionFireCounter(PetriNet.getTransitions().get(transitionId));
                        Logger.showTransitionFiring(PetriNet.getTransitions().get(transitionId), true);
                    } else {
                        System.out.println("                   >>> | ERROR: Transition cannot be fired.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("                   >>> | ERROR: Invalid input.");
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("                   >>> | ERROR: Invalid input.");
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
            return true;
        }
        return false;
    }

    public static final void getPlaceSemaphore(ArrayList<Place> places) {
        Boolean areAcquired = false;
        while (!areAcquired) {
            Integer acquired = 0;
            for (Place place : places) {
                try {
                    if (!place.getSemaphore().tryAcquire(5, TimeUnit.MILLISECONDS)) {
                        releasePlaceSemaphore(places, acquired);
                        Thread.sleep((long) Math.random() * 10);
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                acquired++;
                areAcquired = acquired == places.size();
            }
        }
    }

    public static final void getLoggerSemaphore() {
        Boolean isAcquired = false;
        while (!isAcquired) {
            try {
                if (!Logger.getSemaphore().tryAcquire(5, TimeUnit.MILLISECONDS)) {
                    Thread.sleep((long) Math.random() * 10);
                } else {
                    isAcquired = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static final void releasePlaceSemaphore(ArrayList<Place> places) {
        for (Place place : places) {
            place.getSemaphore().release();
        }
    }

    public static final void releasePlaceSemaphore(ArrayList<Place> places, int acquired) {
        for (int i = 0; i < acquired; i++) {
            places.get(i).getSemaphore().release();
        }
    }

    public static final void releaseLoggerSemaphore() {
        Logger.getSemaphore().release();
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final Boolean getSimulationIsRunning() { return simulationIsRunning; }

    public static final void setSimulationIsRunning(Boolean simulationIsRunning) { Monitor.simulationIsRunning = simulationIsRunning; }
}
