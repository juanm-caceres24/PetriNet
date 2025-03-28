public class Policy {

    /*
     * VARIABLES
     */
    
    private static Float[] probabilites;

    /*
     * CONSTRUCTORS
     */

    private Policy() {

    }

    /*
     * METHODS
     */

    public static final void initializePolicy() {
        Policy.probabilites = Setup.getProbabilities();
        Logger.showPolicy();
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final Float[] getProbabilites() { return probabilites; }

    public static final void setProbabilites(Float[] probabilites) { Policy.probabilites = probabilites; }
}
