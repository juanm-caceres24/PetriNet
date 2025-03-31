import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Logger {

    /*
     * VARIABLES
     */

    private static Long startTime;
    private static ArrayList<Integer> transitionFireCounters;

    // Semaphore to control access to the logger
    private static Semaphore semaphore;

    /*
     * CONSTRUCTORS
     */

    private Logger() { }

    /*
     * METHODS
     */

    public static final void initializeLogger() {
        startTime = null;
        transitionFireCounters = new ArrayList<>();
        for (int i = 0; i < PetriNet.getTransitions().size(); i++) {
            transitionFireCounters.add(0);
        }
        semaphore = new Semaphore(1);
    }

    public static final void incrementTransitionFireCounter(Integer transitionId) {
        transitionFireCounters.set(transitionId, transitionFireCounters.get(transitionId) + 1);
    }

    public static final void showTokens() {
        System.out.println("========================|");
        System.out.println(" TOKENS                 |");
        System.out.println("========================|");
        Integer totalTrackedTokens = 0;
        for (Token token : PetriNet.getTokens()) {
            System.out.println("Token ID -------------- | " + token.getTokenId());
            System.out.println(" |------------> Tracked | " + token.getIsTracked());
            if (token.getIsTracked()) {
                totalTrackedTokens++;
            }
        }
        System.out.println("Total tokens ---------- | " + totalTrackedTokens);
    }

    public static final void showPlaces() {
        System.out.println("========================|");
        System.out.println(" PLACES                 |");
        System.out.println("========================|");
        for (Place place : PetriNet.getPlaces()) {
            System.out.println("Place ID -------------- | " + place.getPlaceId());
            System.out.println(" |------------> Tracked | " + place.getIsTracked());
            System.out.print(" |-------------> Tokens | ");
            if (place.getTokens().isEmpty()) {
                System.out.print("None");
            }
            for (Token token : place.getTokens()) {
                System.out.print(token.getTokenId() + " ");
            }
            System.out.println();
        }
    }

    public static final void showTransitions() {
        System.out.println("========================|");
        System.out.println(" TRANSITIONS            |");
        System.out.println("========================|");
        for (Transition transition : PetriNet.getTransitions()) {
            System.out.println("Transition ID --------- | " + transition.getTransitionId());
            System.out.print(" |-------> Input places | ");
            for (Place inputPlace : transition.getInputPlaces()) {
                System.out.print(inputPlace.getPlaceId() + " ");
            }
            System.out.print("\n |------> Output places | ");
            for (Place outputPlace : transition.getOutputPlaces()) {
                System.out.print(outputPlace.getPlaceId() + " ");
            }
            System.out.println("\n |-----> Min delay time | " + transition.getDelayTimeLimits()[0] + " [ms]");
            System.out.println(" |-----> Max delay time | " + transition.getDelayTimeLimits()[1] + " [ms]");
        }
    }

    public static final void showSegments() {
        System.out.println("========================|");
        System.out.println(" SEGMENTS               |");
        System.out.println("========================|");
        for (Segment segment : PetriNet.getSegments()) {
            System.out.println("Segment ID ------------ | " + segment.getSegmentId());
            System.out.print(" |-------------> Places | ");
            for (Place place : segment.getPlaces()) {
                System.out.print(place.getPlaceId() + " ");
            }
            System.out.print("\n |--------> Transitions | ");
            for (Transition transition : segment.getTransitions()) {
                System.out.print(transition.getTransitionId() + " ");
            }
            System.out.println("\n |-----> Starting place | " + segment.getPlaceLimits()[0].getPlaceId());
            System.out.println(" |-------> Ending place | " + segment.getPlaceLimits()[1].getPlaceId());
            System.out.println(" |--> Internal conflict | " + segment.getConflictedInternalTransition().getTransitionId());
            System.out.print(" |--> External conflict | "); 
            if (segment.getConflictedExternalTransitions().isEmpty()) {
                System.out.print("None");
            }
            for (Transition transition : segment.getConflictedExternalTransitions()) {
                System.out.print(transition.getTransitionId() + " ");
            }
            System.out.println();
        }
    }

    public static final void showPolicy() {
        System.out.println("========================|");
        System.out.println(" POLICY                 |");
        System.out.println("========================|");
        for (int i = 0; i < Policy.getProbabilites().size(); i++) {
            System.out.println("Probability " + i + " --------- | " + Policy.getProbabilites().get(i));
        }
    }

    public static final void showTransitionFiring(Transition transition, Boolean isMinimal) {
        System.out.println("========================|");
        System.out.println(" TRANSITION FIRED       |");
        System.out.println("========================|");
        System.out.println("Elapsed time ---------- | " + (System.currentTimeMillis() - startTime) + " [ms]");
        System.out.println("Transition fired ------ | " + transition.getTransitionId());
        System.out.println("Transition counters --- | T0  T1  T2  T3  T4  T5  T6  T7  T8  T9  T10 T11");
        System.out.print("                        | ");
        for (int i = 0; i < transitionFireCounters.size(); i++) {
            System.out.printf("%-4d", transitionFireCounters.get(i));
        }
        System.out.println();
        if (isMinimal) {
            System.out.println("Actual marking -------- | P0  P1  P2  P3  P4  P5  P6  P7  P8  P9  P10 P11 P12 P13 P14 | TOTAL");
            System.out.print("                        | ");
            Integer totalTrackedTokens = 0;
            for (Place place : PetriNet.getPlaces()) {
                System.out.printf("%-4d", place.getTokens().size());
                if (place.getIsTracked()) {
                    totalTrackedTokens += place.getTokens().size();
                }
            }
            System.out.printf("| %-4d", totalTrackedTokens);
            System.out.println();
        } else {
            for (Place place : PetriNet.getPlaces()) {
                System.out.println("Place ID -------------- | " + place.getPlaceId());
                System.out.print(" |-------------> Tokens | ");
                if (place.getTokens().isEmpty()) {
                    System.out.print("None");
                }
                for (int i = 0; i < place.getTokens().size(); i++) {
                    System.out.print(place.getTokens().get(i).getTokenId() + " ");
                }
                System.out.println();
            }
        }
    }

    public static final void showStartSimulation(Boolean isMinimal) {
        System.out.println("========================|");
        System.out.println(" START OF SIMULATION    |");
        System.out.println("========================|");
        System.out.println("Elapsed time ---------- | " + (System.currentTimeMillis() - startTime) + " [ms]");
        System.out.println("Transition counters --- | T0  T1  T2  T3  T4  T5  T6  T7  T8  T9  T10 T11");
        System.out.print("                        | ");
        for (int i = 0; i < transitionFireCounters.size(); i++) {
            System.out.printf("%-4d", transitionFireCounters.get(i));
        }
        System.out.println();
        if (isMinimal) {
            System.out.println("Actual marking -------- | P0  P1  P2  P3  P4  P5  P6  P7  P8  P9  P10 P11 P12 P13 P14 | TOTAL");
            System.out.print("                        | ");
            Integer totalTrackedTokens = 0;
            for (Place place : PetriNet.getPlaces()) {
                System.out.printf("%-4d", place.getTokens().size());
                if (place.getIsTracked()) {
                    totalTrackedTokens += place.getTokens().size();
                }
            }
            System.out.printf("| %-4d", totalTrackedTokens);
            System.out.println();
        } else {
            for (Place place : PetriNet.getPlaces()) {
                System.out.println("Place ID -------------- | " + place.getPlaceId());
                System.out.print(" |-------------> Tokens | ");
                if (place.getTokens().isEmpty()) {
                    System.out.print("None");
                }
                for (int i = 0; i < place.getTokens().size(); i++) {
                    System.out.print(place.getTokens().get(i).getTokenId() + " ");
                }
                System.out.println();
            }
        }
    }

    public static final void showEndSimulation(Boolean isMinimal) {
        System.out.println("========================|");
        System.out.println(" END OF SIMULATION      |");
        System.out.println("========================|");
        System.out.println("Elapsed time ---------- | " + (System.currentTimeMillis() - startTime) + " [ms]");
        System.out.println("Transition counters --- | T0  T1  T2  T3  T4  T5  T6  T7  T8  T9  T10 T11");
        System.out.print("                        | ");
        for (int i = 0; i < transitionFireCounters.size(); i++) {
            System.out.printf("%-4d", transitionFireCounters.get(i));
        }
        System.out.println();
        if (isMinimal) {
            System.out.println("Actual marking -------- | P0  P1  P2  P3  P4  P5  P6  P7  P8  P9  P10 P11 P12 P13 P14 | TOTAL");
            System.out.print("                        | ");
            Integer totalTrackedTokens = 0;
            for (Place place : PetriNet.getPlaces()) {
                System.out.printf("%-4d", place.getTokens().size());
                if (place.getIsTracked()) {
                    totalTrackedTokens += place.getTokens().size();
                }
            }
            System.out.printf("| %-4d", totalTrackedTokens);
            System.out.println();
        } else {
            for (Place place : PetriNet.getPlaces()) {
                System.out.println("Place ID -------------- | " + place.getPlaceId());
                System.out.print(" |-------------> Tokens | ");
                if (place.getTokens().isEmpty()) {
                    System.out.print("None");
                }
                for (int i = 0; i < place.getTokens().size(); i++) {
                    System.out.print(place.getTokens().get(i).getTokenId() + " ");
                }
                System.out.println();
            }
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final Long getStartTime() { return startTime; }

    public static final void setStartTime(Long startTime) { Logger.startTime = startTime; }

    public static final ArrayList<Integer> getTransitionFireCounters() { return transitionFireCounters; }

    public static final Semaphore getSemaphore() { return semaphore; }
}
