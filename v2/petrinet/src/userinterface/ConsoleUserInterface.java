package v2.petrinet.src.userinterface;

import v2.petrinet.src.models.PetriNet;
import v2.petrinet.src.models.Place;
import v2.petrinet.src.models.Policy;
import v2.petrinet.src.models.Token;
import v2.petrinet.src.models.Transition;
import v2.petrinet.src.utils.Logger;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {

    /* 
     * VARIABLES
     */

    private final Scanner scanner;

    /* 
     * CONSTRUCTORS
     */

    public ConsoleUserInterface() {
        this.scanner = new Scanner(System.in);
    }

    /* 
     * METHODS
     */

    @Override
    public final String requestUserInterface() {
        System.out.println("=======================================|");
        System.out.println(" USER INTERFACE SELECTION              |");
        System.out.println("=======================================|");
        System.out.print("                                   >>> | Select user interface ('0'=Console, '1'=GUI, default=Console): ");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                return "0";
            case "1":
                return "1";
            default:
                this.showErrorMessage(1);
                return "0";
        }
    }

    @Override
    public final String requestModeSelection() {
        System.out.println("=======================================|");
        System.out.println(" MODE SELECTION                        |");
        System.out.println("=======================================|");
        System.out.print("                                   >>> | Select mode ('0'=Simulation mode, '1'=Manual mode, default=Simualtion mode): ");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                return "0";
            case "1":
                return "1";
            default:
                this.showErrorMessage(1);
                return "0";
        }
    }

    @Override
    public final String requestTransitionToFire() {
        System.out.print("                                   >>> | Enter transition ID to fire ('exit'=quit): ");
        return scanner.nextLine();
    }

    @Override
    public final void showErrorMessage(Integer code) {
        switch (code) {
            case 0:
                System.out.println("                                   >>> | ERROR: Invalid input. Requesting again.");
                break;
            case 1:
                System.out.println("                                   >>> | ERROR: Invalid input. Setting default.");
                break;
            case 2:
                System.out.println("                                   >>> | ERROR: Transition cannot fire.");
                break;
            default:
                System.out.println("                                   >>> | ERROR: Unknown error.");
                break;
        }
    }

    @Override
    public final void showTokens() {
        System.out.println("=======================================|");
        System.out.println(" TOKENS                                |");
        System.out.println("=======================================|");
        Integer totalTrackedTokens = 0;
        for (Token token : PetriNet.getTokens()) {
            System.out.println("Token ID ----------------------------- | " + token.getTokenId());
            System.out.println(" |------------------------> Is tracked | " + token.getIsTracked());
            if (token.getIsTracked()) {
                totalTrackedTokens++;
            }
        }
        System.out.println("Total tokens ------------------------- | " + totalTrackedTokens);
    }

    @Override
    public final void showPlaces(
            Boolean showMinimal,
            Boolean showTitle,
            Boolean showIsTracked) {

        if (!showMinimal) {
            if (showTitle) {
                System.out.println("=======================================|");
                System.out.println(" PLACES                                |");
                System.out.println("=======================================|");
            }
            for (Place place : PetriNet.getPlaces()) {
                System.out.println("Place ID ----------------------------- | " + place.getPlaceId());
                if (showIsTracked) {
                    System.out.println(" |------------------------> Is tracked | " + place.getIsTracked());
                }
                System.out.print(" |-------------------------> Tokens ID | ");
                if (place.getTokens().isEmpty()) {
                    System.out.print("None ");
                }
                Integer tokensCounter = 0;
                for (Token token : place.getTokens()) {
                    System.out.print(token.getTokenId() + " ");
                    tokensCounter++;
                }
                System.out.println("(" + tokensCounter + ")");
            }
        } else {
            System.out.println("Actual marking ----------------------- | P0  | P1  | P2  | P3  | P4  | P5  | P6  | P7  | P8  | P9  | P10 | P11 | P12 | P13 | P14 | TOTAL |");
            System.out.print("                                       | ");
            Integer totalTrackedTokens = 0;
            for (Place place : PetriNet.getPlaces()) {
                System.out.printf(
                        "%-4d| ",
                        place.getTokens().size());
                if (place.getIsTracked()) {
                    totalTrackedTokens += place.getTokens().size();
                }
            }
            System.out.printf(
                    "%-4d  |",
                    totalTrackedTokens);
            System.out.println();
        }
    }

    @Override
    public final void showTransitions() {
        System.out.println("=======================================|");
        System.out.println(" TRANSITIONS                           |");
        System.out.println("=======================================|");
        for (Transition transition : PetriNet.getTransitions()) {
            System.out.println("Transition ID ------------------------ | " + transition.getTransitionId());
            System.out.print(" |-------------------> Input places ID | ");
            for (Place inputPlace : transition.getInputPlaces()) {
                System.out.print(inputPlace.getPlaceId() + " ");
            }
            System.out.print("\n |------------------> Output places ID | ");
            for (Place outputPlace : transition.getOutputPlaces()) {
                System.out.print(outputPlace.getPlaceId() + " ");
            }
            System.out.println("\n |--------------------> Min delay time | " + transition.getDelayTimeLimits()[0] + " [ms]");
            System.out.println(" |--------------------> Max delay time | " + transition.getDelayTimeLimits()[1] + " [ms]");
        }
    }

    @Override
    public final void showPolicy() {
        System.out.println("=======================================|");
        System.out.println(" POLICY                                |");
        System.out.println("=======================================|");
        for (int i = 0; i < Policy.getProbabilites().size(); i++) {
            System.out.println("Probability " + i + " ------------------------ | " + Policy.getProbabilites().get(i));
        }
    }

    @Override
    public final void showThreadsState(ArrayList<Integer> threadsState) {
        System.out.println("=======================================|");
        System.out.println(" THREADS STATE                         |");
        System.out.println("=======================================|");
        for (int i = 0; i < threadsState.size(); i++) {
            System.out.println("Thread " + i + " ----------------------------- | " + threadsState.get(i));
        }
    }

    @Override
    public final void showTransitionFiring(
            Transition transition,
            Boolean showMinimal) {
                
        System.out.println("=======================================|");
        System.out.println(" TRANSITION FIRED                      |");
        System.out.println("=======================================|");
        this.showElapsedTime();
        System.out.println("Transition fired --------------------- | " + transition.getTransitionId());
        this.showTransitionsFireCounters();
        this.showThreadsCompletionCounters();
        this.showPlaces(
                showMinimal,
                false,
                false);
        this.showPaths(false);
    }

    @Override
    public final void showStartOfSimulation() {
        System.out.println("=======================================|");
        System.out.println(" START OF SIMULATION                   |");
        System.out.println("=======================================|");
        this.showElapsedTime();
        this.showTransitionsFireCounters();
        this.showThreadsCompletionCounters();
        this.showPlaces(
                true,
                false,
                false);
        this.showTransitionsByToken();
        this.showPaths(true);
    }

    @Override
    public final void showEndOfSimulation() {
        System.out.println("=======================================|");
        System.out.println(" END OF SIMULATION                     |");
        System.out.println("=======================================|");
        this.showElapsedTime();
        this.showTransitionsFireCounters();
        this.showThreadsCompletionCounters();
        this.showPlaces(
                true,
                false,
                false);
        this.showTransitionsByToken();
        this.showPaths(true);
    }

    @Override
    public final void showTransitionsByToken() {
        System.out.println("=======================================|");
        System.out.println(" TRANSITIONS TAKEN BY TOKEN            |");
        System.out.println("=======================================|");
        for (int i = 0; i < Logger.getTransitionsByTokenLogs().size(); i++) {
            System.out.println("Token ID ----------------------------- | " + i);
            System.out.print(" |--------------------> Transitions ID | ");
            if (Logger.getTransitionsByTokenLogs().get(i).isEmpty()) {
                System.out.print("None ");
            }
            Integer transitionsCounter = 0;
            for (Integer transitionId : Logger.getTransitionsByTokenLogs().get(i)) {
                
                // Print lines of 50 elements and then insert a new row
                if (transitionsCounter % 50 == 0 && transitionsCounter != 0) {
                    System.out.println();
                    System.out.print("                                     + | ");
                }
                System.out.print(transitionId + " ");
                transitionsCounter++;
            }
            System.out.println();
        }
    }

    @Override
    public final void showElapsedTime() {
        System.out.println("Elapsed time ------------------------- | " + (System.currentTimeMillis() - Logger.getStartTime()) + " [ms]");
    }

    @Override
    public final void showPaths(Boolean showTitle) {
        if (showTitle) {
            System.out.println("=======================================|");
            System.out.println(" PATHS                                 |");
            System.out.println("=======================================|");
        }
        for (int i = 0; i < Logger.getPaths().size(); i++) {
            System.out.print("Path " + i + " ------------------------------- | ");
            for (Integer threadId : Logger.getPaths().get(i)) {
                System.out.print(threadId + " ");
            }
            System.out.println("\n |---------------------------> Counter | " + Logger.getPathsCounters().get(i));
        }
    }

    @Override
    public final void showTransitionsFireCounters() {
        System.out.println("Transition counters ------------------ | T0  | T1  | T2  | T3  | T4  | T5  | T6  | T7  | T8  | T9  | T10 | T11 |");
        System.out.print("                                       | ");
        for (int i = 0; i < Logger.getTransitionsFireCounters().size(); i++) {
            System.out.printf(
                    "%-4d| ",
                    Logger.getTransitionsFireCounters().get(i));
        }
        System.out.println();
    }

    @Override
    public final void showThreadsCompletionCounters() {
        System.out.println("Thread counters --------------------- | T0  | T1  | T2  | T3  | T4  | T5  |");
        System.out.print("                                       | ");
        for (int i = 0; i < Logger.getThreadsCompletionCounters().size(); i++) {
            System.out.printf(
                    "%-4d| ",
                    Logger.getThreadsCompletionCounters().get(i));
        }
        System.out.println();
    }

    /*
     * GETTERS AND SETTERS
     */

    public Scanner getScanner() { return scanner; }
}
