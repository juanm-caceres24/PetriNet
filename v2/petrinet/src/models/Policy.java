package v2.petrinet.src.models;

import v2.petrinet.src.Setup;

import java.util.ArrayList;

public class Policy {

    /*
     * VARIABLES
     */
    
    private static ArrayList<Float> probabilites;
    private static Policy policyInstance;

    /*
     * CONSTRUCTORS
     */

    private Policy() {
        probabilites = new ArrayList<>();
        for (int i = 0; i < Setup.getProbabilities().length; i++) {
            probabilites.add(Setup.getProbabilities()[i]);
        }
    }

    /*
     * METHODS
     */

    // (none)

    /*
     * GETTERS AND SETTERS
     */

    public static final ArrayList<Float> getProbabilites() { return probabilites; }

    public static final Policy getPolicyInstance() {
        if (policyInstance == null) {
            policyInstance = new Policy();
        }
        return policyInstance;
    }
}
