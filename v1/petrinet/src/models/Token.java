package v1.petrinet.src.models;

public class Token {

    /*
     * VARIABLES
     */
    
    private Integer tokenId;
    private Boolean isTracked;
    
    /*
     * CONSTRUCTORS
     */

    public Token(
            Integer tokenId,
            Boolean isTracked) {
        this.tokenId = tokenId;
        this.isTracked = isTracked;
    }

    /*
     * GETTERS AND SETTERS
     */

    public Integer getTokenId() { return tokenId; }

    public Boolean getIsTracked() { return isTracked; }
}
