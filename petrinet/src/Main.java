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
        userInterface.askForUserUserInterface();
        PetriNet.initializePetriNet();
        Policy.initializePolicy();
        Logger.initializeLogger();
        Monitor.initializeMonitor();
        
        // Selection of mode
        userInterface.askForModeSelection();

        // Show end of the program
        System.out.println("                                   >>> | Program successfully finished!");
        return;
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final UserInterface getUserInterface() { return userInterface; }

    public static final void setUserInterface(UserInterface userInterface) { Main.userInterface = userInterface; }
}
