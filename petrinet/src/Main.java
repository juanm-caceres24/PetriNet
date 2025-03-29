import java.util.Scanner;

public class Main {
    
    /*
     * MAIN METHOD
     */
    
    public static void main(String args[]) {

        // Initialize the PetriNet, policy and logger
        PetriNet.initializePetriNet();
        Policy.initializePolicy();
        Logger.initializeLogger();
        
        // Selection of simulation or manual mode and run
        System.out.println("=======================|");
        System.out.println(" MODE SELECTION        |");
        System.out.println("=======================|");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("                   >>> | Select mode ('0'=Simulation, '1'=Manual mode): ");
            String input = scanner.nextLine();
            if (input.equals("0")) {
                Monitor.startSimulation();
                break;
            } else if (input.equals("1")) {
                Monitor.startManualMode();
                break;
            } else {
                System.out.println("                   >>> | ERROR: Invalid input.");
            }
        }
        scanner.close();

        // Show end of the program
        System.out.println("                   >>> | Program successfully finished!");
        return;
    }
}
