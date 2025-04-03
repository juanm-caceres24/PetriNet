package petrinet.src.userinterface;

import petrinet.src.Main;
import petrinet.src.models.PetriNet;
import petrinet.src.models.Place;
import petrinet.src.models.Policy;
import petrinet.src.models.Segment;
import petrinet.src.models.Token;
import petrinet.src.models.Transition;
import petrinet.src.monitor.Monitor;
import petrinet.src.utils.Logger;

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
        super();
        this.scanner = new Scanner(System.in);
    }

    /* 
     * METHODS
     */

    @Override
    public final void askForUserUserInterface() {
        System.out.println("=======================================|");
        System.out.println(" USER INTERFACE SELECTION              |");
        System.out.println("=======================================|");
        while (true) {
            System.out.print("                                   >>> | Select user interface ('0'=Console, '1'=GUI): ");
            String input = scanner.nextLine();
            if (input.equals("0")) {
                Main.setUserInterface(new ConsoleUserInterface());
                break;
            } else if (input.equals("1")) {
                Main.setUserInterface(new GraphicUserInterface());
                break;
            } else {
                System.out.println("                                   >>> | ERROR: Invalid input.");
            }
        }
    }

    @Override
    public final void askForModeSelection() {
        System.out.println("=======================================|");
        System.out.println(" MODE SELECTION                        |");
        System.out.println("=======================================|");
        while (true) {
            System.out.print("                                   >>> | Select mode ('0'=Simulation mode, '1'=Manual mode): ");
            String input = scanner.nextLine();
            if (input.equals("0")) {
                Monitor.startSimulationMode();
                break;
            } else if (input.equals("1")) {
                Monitor.startManualMode();
                break;
            } else {
                System.out.println("                                   >>> | ERROR: Invalid input.");
            }
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
                System.out.printf("%-4d| ", place.getTokens().size());
                if (place.getIsTracked()) {
                    totalTrackedTokens += place.getTokens().size();
                }
            }
            System.out.printf("%-4d  |", totalTrackedTokens);
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
    public final void showSegments() {
        System.out.println("=======================================|");
        System.out.println(" SEGMENTS                              |");
        System.out.println("=======================================|");
        for (Segment segment : PetriNet.getSegments()) {
            System.out.println("Segment ID --------------------------- | " + segment.getSegmentId());
            System.out.print(" |-------------------------> Places ID | ");
            for (Place place : segment.getPlaces()) {
                System.out.print(place.getPlaceId() + " ");
            }
            System.out.print("\n |--------------------> Transitions ID | ");
            for (Transition transition : segment.getTransitions()) {
                System.out.print(transition.getTransitionId() + " ");
            }
            System.out.println("\n |-----------------> Starting place ID | " + segment.getPlaceLimits()[0].getPlaceId());
            System.out.println(" |-------------------> Ending place ID | " + segment.getPlaceLimits()[1].getPlaceId());
            System.out.println(" |------------> Starting transition ID | " + segment.getTransitionLimits()[0].getTransitionId());
            System.out.println(" |--------------> Ending transition ID | " + segment.getTransitionLimits()[1].getTransitionId());
            System.out.print(" |---------> Conflicted transitions ID | "); 
            if (segment.getConflictedTransitions().isEmpty()) {
                System.out.print("None");
            }
            for (Transition transition : segment.getConflictedTransitions()) {
                System.out.print(transition.getTransitionId() + " ");
            }
            System.out.println();
        }
    }

    @Override
    public final void showPaths() {
        System.out.println("=======================================|");
        System.out.println(" PATHS                                 |");
        System.out.println("=======================================|");
        for (int i = 0; i < Logger.getPaths().size(); i++) {
            System.out.print("Path " + i + " ------------------------------- | ");
            for (Integer segmentId : Logger.getPaths().get(i)) {
                System.out.print(segmentId + " ");
            }
            System.out.println("\n |---------------------------> Counter | " + Logger.getPathsCounters().get(i));
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
    public final void showThreadsState() {
        System.out.println("=======================================|");
        System.out.println(" THREADS STATE                         |");
        System.out.println("=======================================|");
        for (int i = 0; i < Monitor.getThreadsState().size(); i++) {
            System.out.println("Thread " + i + " ----------------------------- | " + Monitor.getThreadsState().get(i));
        }
    }

    @Override
    public final void showTransitionFiring(
            Transition transition,
            Boolean showMinimal,
            Boolean showSegmentsCompletionCounters) {

        System.out.println("=======================================|");
        System.out.println(" TRANSITION FIRED                      |");
        System.out.println("=======================================|");
        this.showElapsedTime();
        System.out.println("Transition fired --------------------- | " + transition.getTransitionId());
        this.showTransitionFireCounters();
        if (showSegmentsCompletionCounters) {
            this.showSegmentCompletionCounters();
        }
        this.showPlaces(
                showMinimal,
                false,
                false);
    }

    @Override
    public final void showStartSimulation(Boolean showMinimal) {
        System.out.println("=======================================|");
        System.out.println(" START OF SIMULATION                   |");
        System.out.println("=======================================|");
        this.showElapsedTime();
        this.showTransitionFireCounters();
        this.showSegmentCompletionCounters();
        this.showPlaces(
                showMinimal,
                false,
                false);
        this.showThreadsState();
        this.showTransitionsByToken();
    }

    @Override
    public final void showEndSimulation(Boolean showMinimal) {
        System.out.println("=======================================|");
        System.out.println(" END OF SIMULATION                     |");
        System.out.println("=======================================|");
        this.showElapsedTime();
        this.showTransitionFireCounters();
        this.showSegmentCompletionCounters();
        this.showPlaces(
                showMinimal,
                false,
                false);
        this.showThreadsState();
        this.showTransitionsByToken();
    }

    @Override
    public final void showElapsedTime() {
        System.out.println("Elapsed time ------------------------- | " + (System.currentTimeMillis() - Logger.getStartTime()) + " [ms]");
    }

    @Override
    public final void showTransitionsByToken() {
        System.out.println("=======================================|");
        System.out.println(" TRANSITIONS BY TOKEN                  |");
        System.out.println("=======================================|");
        for (int i = 0; i < Logger.getTransitionsByTokenLogs().size(); i++) {
            System.out.println("Token ID ----------------------------- | " + i);
            System.out.print(" |--------------------> Transitions ID | ");
            if (Logger.getTransitionsByTokenLogs().get(i).isEmpty()) {
                System.out.print("None ");
            }
            for (Integer transitionId : Logger.getTransitionsByTokenLogs().get(i)) {
                System.out.print(transitionId + " ");
            }
            System.out.println();
        }
    }

    @Override
    public final void showTransitionFireCounters() {
        System.out.println("Transition counters ------------------ | T0  | T1  | T2  | T3  | T4  | T5  | T6  | T7  | T8  | T9  | T10 | T11 |");
        System.out.print("                                       | ");
        for (int i = 0; i < Logger.getTransitionFireCounters().size(); i++) {
            System.out.printf("%-4d| ", Logger.getTransitionFireCounters().get(i));
        }
        System.out.println();
    }

    @Override
    public final void showSegmentCompletionCounters() {
        System.out.println("Segment counters --------------------- | S0  | S1  | S2  | S3  | S4  | S5  |");
        System.out.print("                                       | ");
        for (int i = 0; i < Logger.getSegmentCompletionCounters().size(); i++) {
            System.out.printf("%-4d| ", Logger.getSegmentCompletionCounters().get(i));
        }
        System.out.println();
    }

    /*
     * GETTERS AND SETTERS
     */

    public Scanner getScanner() { return scanner; }
}
