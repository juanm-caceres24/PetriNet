package v2.petrinet.src.models;

import v2.petrinet.src.Setup;

import java.util.ArrayList;

public class PetriNet {

    /*
     * VARIABLES
     */

    private static ArrayList<Token> tokens;
    private static ArrayList<Place> places;
    private static ArrayList<Transition> transitions;

    /*
     * CONSTRUCTORS
     */

    private PetriNet() {
        PetriNet.tokens = new ArrayList<>();
        PetriNet.places = new ArrayList<>();
        PetriNet.transitions = new ArrayList<>();
        PetriNet.createTokens(
                Setup.getMainPlaces(),
                Setup.getInitialMarking());
        PetriNet.createPlaces(
                Setup.getMainPlaces(),
                Setup.getInitialMarking());
        PetriNet.createTransitions(
                Setup.getIncidenceMatrix(),
                Setup.getDelayTimeLimitsMatrix());
    }

    /*
     * METHODS
     */

    private static final void createTokens(
            Integer[] mainPlaces,
            Integer[] initialMarking) {
        
        // Create tokens based on initial marking
        for (int i = 0; i < initialMarking.length; i++) {
            for (int j = 0; j < initialMarking[i]; j++) {
                Token token = new Token(j + 100 * i, mainPlaces[i] == 1);
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

    /*
     * GETTERS AND SETTERS
     */

    public static final ArrayList<Token> getTokens() { return tokens; }

    public static final ArrayList<Place> getPlaces() { return places; }

    public static final ArrayList<Transition> getTransitions() { return transitions; }
}
