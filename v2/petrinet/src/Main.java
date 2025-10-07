package v2.petrinet.src;

import java.util.ArrayList;

import v2.petrinet.src.models.PetriNet;
import v2.petrinet.src.models.Policy;
import v2.petrinet.src.models.Transition;
import v2.petrinet.src.monitor.Monitor;
import v2.petrinet.src.threads.SimulationThread;
import v2.petrinet.src.userinterface.ConsoleUserInterface;
import v2.petrinet.src.userinterface.GraphicUserInterface;
import v2.petrinet.src.userinterface.UserInterface;
import v2.petrinet.src.utils.Logger;

public class Main {

    /*
     * VARIABLES
     */

    private static UserInterface userInterface;
    private static ArrayList<SimulationThread> simulationThreads;
    
    /*
     * MAIN METHOD
     */
    
    public static final void main(String args[]) {
        // Initialize the User Interface
        userInterface = new ConsoleUserInterface();
        switch (userInterface.requestUserInterface()) {
            case "0":
                userInterface = new ConsoleUserInterface();
                break;
            case "1":
                userInterface = new GraphicUserInterface();
        }
        // Initialize the petrinet, threads, monitor, logger and policy singletons
        PetriNet.getPetriNetInstance();
        Main.createSimulationThreads();
        Policy.getPolicyInstance();
        Monitor.getMonitorInstance();
        Logger.getLoggerInstance();
        // Selection of mode
        switch (userInterface.requestModeSelection()) {
            case "0":
                Main.startSimulationMode();
                break;
            case "1":
                Main.startManualMode();
                break;
        }
        // End of the program
        return;
    }

    /*
     * METHODS
     */

    private static final void startSimulationMode() {
        // Log start of simulation mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.logStartOfSimulation();
        // Start simulation mode
        for (SimulationThread thread : simulationThreads) {
            thread.start();
        }
        // Wait for all segments to finish
        for (SimulationThread thread : simulationThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Log end of simulation mode
        Logger.logEndOfSimulation();
    }

    private static final void startManualMode() {
        // Log start of manual mode
        Logger.setStartTime(System.currentTimeMillis());
        Logger.logStartOfSimulation();
        // Start manual mode
        while (true) {
            String input = Main.getUserInterface().requestTransitionToFire();
            if (input.equals("exit")) {
                break;
            } else {
                try {
                    Integer transitionId = Integer.parseInt(input);
                    if (PetriNet.getTransitions().get(transitionId).isSensibilized()) {
                        // Fire the transition and save the ID of the tracked token, if any
                        Integer trackedTokenId = PetriNet.getTransitions().get(transitionId).fireTransition();
                        // Get the thread that contains the fired transitions
                        SimulationThread thread = null;
                        for (int i = 0; i < Setup.getThreadsQuantity(); i++) {
                            if (Setup.getThreadsTransitionsMatrix()[i][transitionId] == 1) {
                                thread = simulationThreads.get(i);
                                break;
                            }
                        }
                        // Log the transition firing
                        Logger.logTransitionFiring(
                                thread,
                                PetriNet.getTransitions().get(transitionId),
                                trackedTokenId,
                                true);
                    } else {
                        Main.getUserInterface().showErrorMessage(2);
                    }
                } catch (NumberFormatException e) {
                    Main.getUserInterface().showErrorMessage(0);
                } catch (IndexOutOfBoundsException e) {
                    Main.getUserInterface().showErrorMessage(0);
                }
            }
        }
        // Log end of manual mode
        Logger.logEndOfSimulation();
    }

    private static final void createSimulationThreads() {
        simulationThreads = new ArrayList<>();
        for (int i = 0; i < Setup.getThreadsQuantity(); i++) {
            ArrayList<Transition> threadTransitions = new ArrayList<>();
            for (int j = 0; j < Setup.getThreadsTransitionsMatrix()[i].length; j++) {
                if (Setup.getThreadsTransitionsMatrix()[i][j] == 1) {
                    threadTransitions.add(PetriNet.getTransitions().get(j));
                }
            }
            Transition[] transitionLimits = new Transition[] {
                    PetriNet.getTransitions().get(Setup.getThreadsTransitionLimitsMatrix()[i][0]),
                    PetriNet.getTransitions().get(Setup.getThreadsTransitionLimitsMatrix()[i][1])
            };
            simulationThreads.add(new SimulationThread(
                    i,
                    threadTransitions,
                    transitionLimits));
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final UserInterface getUserInterface() { return userInterface; }

    public static final ArrayList<SimulationThread> getSimulationThreads() { return simulationThreads; }
}
