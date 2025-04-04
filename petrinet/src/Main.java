package petrinet.src;

import petrinet.src.models.PetriNet;
import petrinet.src.models.Policy;
import petrinet.src.monitor.Monitor;
import petrinet.src.userinterface.ConsoleUserInterface;
import petrinet.src.userinterface.UserInterface;
import petrinet.src.utils.Logger;

public class Main {

    /*
     * VARIABLES
     */

    private static UserInterface userInterface;
    
    /*
     * MAIN METHOD
     */
    
    public static final void main(String args[]) {
        // Initialize the User Interface by default, PetriNet, Policy, Logger and Monitor
        userInterface = new ConsoleUserInterface();
        userInterface = userInterface.requestUserInterface();
        PetriNet.initializePetriNet();
        Policy.initializePolicy();
        Logger.initializeLogger();
        Monitor.initializeMonitor();
        // Selection of mode
        switch (userInterface.requestModeSelection()) {
            case "0":
                Monitor.startSimulationMode();
                break;
            case "1":
                Monitor.startManualMode();
                break;
        }
        // End of the program
        return;
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final UserInterface getUserInterface() { return userInterface; }

    public static final void setUserInterface(UserInterface userInterface) { Main.userInterface = userInterface; }
}
