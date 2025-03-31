import java.util.Scanner;

public class Main {
    
    /*
     * MAIN METHOD
     */
    
    public static void main(String args[]) {

        // Initialize the PetriNet, policy, logger, and monitor
        PetriNet.initializePetriNet();
        Policy.initializePolicy();
        Logger.initializeLogger();
        Monitor.initializeMonitorr();
        
        // Selection and launching of simulation mode or manual mode
        System.out.println("========================|");
        System.out.println(" MODE SELECTION         |");
        System.out.println("========================|");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("                    >>> | Select mode ('0'=Simulation, '1'=Manual mode): ");
            String input = scanner.nextLine();
            if (input.equals("0")) {
                Monitor.startSimulationMode();
                break;
            } else if (input.equals("1")) {
                Monitor.startManualMode();
                break;
            } else {
                System.out.println("                    >>> | ERROR: Invalid input.");
            }
        }
        scanner.close();

        // Show end of the program
        System.out.println("                    >>> | Program successfully finished!");
        return;
    }
}
