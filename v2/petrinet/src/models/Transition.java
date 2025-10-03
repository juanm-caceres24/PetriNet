package v2.petrinet.src.models;

import java.util.ArrayList;

public class Transition {

    /*
     * VARIABLES
     */

    private Integer transitionId;
    // Connected input and output places
    private ArrayList<Place> inputPlaces;
    private ArrayList<Place> outputPlaces;
    // Delay time
    private Integer[] delayTimeLimits;
    private Long delayTime;

    /*
     * CONSTRUCTORS
     */

    public Transition(
            Integer transitionId,
            ArrayList<Place> inputPlaces,
            ArrayList<Place> outputPlaces,
            Integer[] delayTimeLimits) {

        this.transitionId = transitionId;
        this.inputPlaces = inputPlaces;
        this.outputPlaces = outputPlaces;
        this.delayTimeLimits = delayTimeLimits;
        delayTime = System.currentTimeMillis();
    }

    /*
     * METHODS
     */

    public Integer fireTransition() {
        // Token to be rescued from input places
        Token trackedToken = null;
        Token tmpToken;
        // Consumes tokens from input places
        for (int i = 0; i < inputPlaces.size(); i++) {
            tmpToken = inputPlaces.get(i).consume();
            if (tmpToken.getIsTracked()) {
                trackedToken = tmpToken;
            }
        }
        // Produces tokens in output places
        for (int i = 0; i < outputPlaces.size(); i++) {
            outputPlaces.get(i).produce(trackedToken);
        }
        // Resets the delay time
        delayTime = null;
        // Returns the ID of the tracked token, if any
        if (trackedToken != null) {
            return trackedToken.getTokenId();
        } else {
            return null;
        }
    }

    public Boolean isSensibilized() {
        // Checks if there are enough tokens in input places to fire the transition
        for (int i = 0; i < inputPlaces.size(); i++) {
            if (inputPlaces.get(i).getTokens().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Boolean canFire() {
        // Checks if the transition can fire (is sensibilized and delay time has passed)
        if (isSensibilized() && (System.currentTimeMillis() >= delayTime)) {
            return true;
        }
        return false;
    }

    public void randomizeDelayTime() {
        // Randomizes the delay time and sets the waiting flag to true
        delayTime = System.currentTimeMillis() + (long) (Math.random() * (delayTimeLimits[1] - delayTimeLimits[0] + 1) + delayTimeLimits[0]);
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getTransitionId() { return transitionId; }

    public ArrayList<Place> getInputPlaces() { return inputPlaces; }

    public ArrayList<Place> getOutputPlaces() { return outputPlaces; }
    
    public Integer[] getDelayTimeLimits() { return delayTimeLimits; }

    public Long getDelayTime() { return delayTime; }
}
