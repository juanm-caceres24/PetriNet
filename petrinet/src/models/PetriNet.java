package petrinet.src.models;

import petrinet.src.utils.Logger;
import petrinet.src.Setup;

import java.util.ArrayList;

public class PetriNet {

    /*
     * VARIABLES
     */

    private static ArrayList<Token> tokens;
    private static ArrayList<Place> places;
    private static ArrayList<Transition> transitions;
    private static ArrayList<Segment> segments;

    /*
     * CONSTRUCTORS
     */

    private PetriNet() { }

    /*
     * METHODS
     */

    public static final void initializePetriNet() {

        PetriNet.tokens = new ArrayList<>();
        PetriNet.places = new ArrayList<>();
        PetriNet.transitions = new ArrayList<>();
        PetriNet.segments = new ArrayList<>();
        createTokens(
                Setup.getMainPlaces(),
                Setup.getInitialMarking());
        createPlaces(
                Setup.getMainPlaces(),
                Setup.getInitialMarking());
        createTransitions(
                Setup.getIncidenceMatrix(),
                Setup.getDelayTimeLimitsMatrix());
        createSegments(
                Setup.getPlacesSegmentsMatrix(),
                Setup.getTransitionsSegmentsMatrix(),
                Setup.getSegmentsPlaceLimitsMatrix());

        // Log creation of tokens, places, transitions and segments
        Logger.logTokens();
        Logger.logPlaces(
                false,
                true,
                true);
        Logger.logTransitions();
        Logger.logSegments();
    }

    private static final void createTokens(
            Integer[] mainPlaces,
            Integer[] initialMarking) {

        // Create tokens based on initial marking
        for (int i = 0; i < initialMarking.length; i++) {
            for (int j = 0; j < initialMarking[i]; j++) {
                Token token = new Token(
                        j + 100 * i,
                        mainPlaces[i] == 1);
                PetriNet.tokens.add(token);
            }
        }
    }

    private static final void createPlaces(
            Integer[] mainPlaces,
            Integer[] initialMarking) {

        // Create places based on initial marking
        int tokenIndex = 0;
        for (int j = 0; j < initialMarking.length; j++) {
            ArrayList<Token> tmp = new ArrayList<>();
            for (int k = 0; k < initialMarking[j]; k++) {
                tmp.add(tokens.get(tokenIndex));
                tokenIndex++;
            }
            PetriNet.places.add(new Place(
                    j,
                    mainPlaces[j] == 1,
                    tmp));
        }
    }

    private static final void createTransitions(
            Integer[][] incidenceMatrix,
            Integer[][] delayTimeLimits) {

        // Create transitions based on incidence matrix columns
        for (int i = 0; i < incidenceMatrix[0].length; i++) {
            ArrayList<Integer> quantitiesToConsume = new ArrayList<>();
            ArrayList<Integer> quantitiesToProduce = new ArrayList<>();
            ArrayList<Place> inputPlaces = new ArrayList<>();
            ArrayList<Place> outputPlaces = new ArrayList<>();

            // Load consumed & produced quantities and input & output places for each transition based on incidence matrix rows
            for (int j = 0; j < incidenceMatrix.length; j++) {
                if (incidenceMatrix[j][i] < 0) {
                    quantitiesToConsume.add(-incidenceMatrix[j][i]);
                    inputPlaces.add(places.get(j));
                } else if (incidenceMatrix[j][i] > 0) {
                    quantitiesToProduce.add(incidenceMatrix[j][i]);
                    outputPlaces.add(places.get(j));
                }
            }
            Transition transition = new Transition(
                    i,
                    inputPlaces,
                    outputPlaces,
                    delayTimeLimits[i]);
            PetriNet.transitions.add(transition);
        }
    }
    
    private static final void createSegments(
            Integer[][] placesSegmentsMatrix,
            Integer[][] transitionsSegmentsMatrix,
            Integer[][] segmentsPlaceLimitsMatrix) {

        // Create transitions and places for each segment based on places segments matrix rows
        for (int i = 0; i < placesSegmentsMatrix.length; i++) {
            ArrayList<Place> places = new ArrayList<>();
            ArrayList<Transition> transitions = new ArrayList<>();

            // Load all places of the actual segment based on the places list
            for (int j = 0; j < PetriNet.places.size(); j++) {
                if (placesSegmentsMatrix[i][j] == 1) {
                    places.add(PetriNet.places.get(j));
                }
            }

            // Load all transitions of the actual segment based on the transitions list
            for (int j = 0; j < PetriNet.transitions.size(); j++) {
                if (transitionsSegmentsMatrix[i][j] == 1) {
                    transitions.add(PetriNet.transitions.get(j));
                }
            }
            Segment segment = new Segment(
                    i,
                    places,
                    transitions,
                    new Place[] {
                            PetriNet.places.get(segmentsPlaceLimitsMatrix[i][0]),
                            PetriNet.places.get(segmentsPlaceLimitsMatrix[i][1])
                    });
            segments.add(segment);
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final ArrayList<Token> getTokens() { return tokens; }

    public static final ArrayList<Place> getPlaces() { return places; }

    public static final ArrayList<Transition> getTransitions() { return transitions; }

    public static final ArrayList<Segment> getSegments() { return segments; }
}
