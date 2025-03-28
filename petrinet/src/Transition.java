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
    private Boolean isWaiting;

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
        isWaiting = false;
    }

    /*
     * METHODS
     */

    public void fireTransition() {

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

        // Set the waiting flag to false
        isWaiting = false;
    }

    public Boolean canFire() {

        // Checks if there are enough tokens in input places to fire the transition
        for (int i = 0; i < inputPlaces.size(); i++) {
            if (inputPlaces.get(i).getTokens().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void randomizeDelayTime() {

        // Randomizes the delay time and sets the waiting flag to true
        delayTime = System.currentTimeMillis() + (long) (Math.random() * (delayTimeLimits[1] - delayTimeLimits[0] + 1) + delayTimeLimits[0]);
        isWaiting = true;
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getTransitionId() { return transitionId; }

    public ArrayList<Place> getInputPlaces() { return inputPlaces; }

    public ArrayList<Place> getOutputPlaces() { return outputPlaces; }
    
    public Integer[] getDelayTimeLimits() { return delayTimeLimits; }

    public Long getDelayTime() { return delayTime; }

    public Boolean getIsWaiting() { return isWaiting; }

    public void setIsWaiting(Boolean isWaiting) { this.isWaiting = isWaiting; }
}
