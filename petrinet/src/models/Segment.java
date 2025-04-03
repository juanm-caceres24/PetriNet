package petrinet.src.models;

import java.util.ArrayList;

public class Segment {

    /*
     * VARIABLES
     */

    private Integer segmentId;
    private ArrayList<Place> places;
    private ArrayList<Transition> transitions;
    private Place[] placeLimits;

    // Variables to analize conflicts and apply Policy
    private Transition[] transitionLimits;
    private ArrayList<Transition> conflictedTransitions;

    /*
     * CONSTRUCTORS
     */

    public Segment(
            Integer segmentId,
            ArrayList<Place> places,
            ArrayList<Transition> transitions,
            Place[] placeLimits) {

        this.segmentId = segmentId;
        this.places = places;
        this.transitions = transitions;
        this.placeLimits = placeLimits;
        
        // Search the transitions that are the limits of the segment
        transitionLimits = new Transition[2];
        for (Transition transition : transitions) {
            if (transition.getInputPlaces().contains(placeLimits[0])) {
                transitionLimits[0] = transition;
            }
            if (transition.getOutputPlaces().contains(placeLimits[1])) {
                transitionLimits[1] = transition;
            }
        }

        // Search all the others transitions of the others segments that have the same input place
        conflictedTransitions = new ArrayList<>();
        for (Transition transition : PetriNet.getTransitions()) {
            if (transition.getInputPlaces().contains(placeLimits[0]) && transition != transitionLimits[0]) {
                conflictedTransitions.add(transition);
            }
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getSegmentId() { return segmentId; }

    public ArrayList<Place> getPlaces() { return places; }

    public ArrayList<Transition> getTransitions() { return transitions; }

    public Place[] getPlaceLimits() { return placeLimits; }

    public Transition[] getTransitionLimits() { return transitionLimits; }

    public ArrayList<Transition> getConflictedTransitions() { return conflictedTransitions; }
}
