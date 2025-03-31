import java.util.ArrayList;

public class Policy {

    /*
     * VARIABLES
     */
    
    private static ArrayList<Float> probabilites;

    /*
     * CONSTRUCTORS
     */

    private Policy() { }

    /*
     * METHODS
     */

    public static final void initializePolicy() {
        probabilites = new ArrayList<>();
        for (int i = 0; i < Setup.getProbabilities().length; i++) {
            probabilites.add(Setup.getProbabilities()[i]);
        }
        Logger.showPolicy();
    }

    /*
     * GETTERS AND SETTERS
     */

    public static final ArrayList<Float> getProbabilites() { return probabilites; }

    public static final void setProbabilites(Float[] probabilites) {
        Policy.probabilites = new ArrayList<>();
        for (int i = 0; i < probabilites.length; i++) {
            Policy.probabilites.add(probabilites[i]);
        }
    }
}
