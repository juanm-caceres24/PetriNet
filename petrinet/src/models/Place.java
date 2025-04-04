package petrinet.src.models;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Place {

    /*
     * VARIABLES
     */

    private Integer placeId;
    private Boolean isTracked;
    private ArrayList<Token> tokens;
    // Semaphore to control access to the place
    private Semaphore semaphore;

    /*
     * CONSTRUCTORS
     */

    public Place(Integer placeId, Boolean isTracked, ArrayList<Token> tokens) {
        this.placeId = placeId;
        this.isTracked = isTracked;
        this.tokens = tokens;
        this.semaphore = new Semaphore(1);
    }

    /*
     * METHODS
     */

    public Token consume() {
        if (!tokens.isEmpty()) {
            Token tmpToken = tokens.get(0);
            tokens.remove(0);
            return tmpToken;
        }
        return null;
    }

    public void produce(Token token) {
        if (token != null) {
            if (isTracked) {
                tokens.add(token);
            } else {
                Integer tmpTokenId;
                for (int i = 0; true; i++) {
                    tmpTokenId = i + 100 * placeId;
                    Boolean isFound = false;
                    for (Token t : tokens) {
                        if (t.getTokenId().equals(tmpTokenId)) {
                            isFound = true;
                            break;
                        }
                    }
                    if (!isFound) {
                        tokens.add(new Token(tmpTokenId, isTracked));
                        break;
                    }
                }
            }
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getPlaceId() { return placeId; }

    public Boolean getIsTracked() { return isTracked; }

    public ArrayList<Token> getTokens() { return tokens; }

    public Semaphore getSemaphore() { return semaphore; }
}
