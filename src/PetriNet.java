public class PetriNet {

    //                                         P0   P1   P2   P3   P4   P5   P6   P7   P8   P9
    private final int[] marking = new int[] {   3,   0,   0,   0,   0,   0,   0,   1,   1,   0 };

    //                                                      T0  T1  T2  T3  T4  T5  T6  T7  T8  T9
    private final int[][] incidenceMatrix = new int[][] { { -1,  0,  0,  0,  0,  0,  0,  0,  0,  1 },   // P0
                                                          {  1, -1,  0,  0, -1,  0, -1,  0,  0,  0 },   // P1
                                                          {  0,  1, -1,  0,  0,  0,  0,  0,  0,  0 },   // P2
                                                          {  0,  0,  1, -1,  0,  0,  0,  0,  0,  0 },   // P3
                                                          {  0,  0,  0,  0,  1, -1,  0,  0,  0,  0 },   // P4
                                                          {  0,  0,  0,  0,  0,  0,  1, -1,  0,  0 },   // P5
                                                          {  0,  0,  0,  0,  0,  0,  0,  1, -1,  0 },   // P6
                                                          {  0, -1,  0,  1, -1,  1,  0,  0,  0,  0 },   // P7
                                                          {  0,  0,  0,  0, -1,  1, -1,  0,  1,  0 },   // P8
                                                          {  0,  0,  0,  1,  0,  1,  0,  0,  1, -1 } }; // P9;

    //                                          T0   T1   T2   T3   T4   T5   T6   T7   T8   T9
    private final long[] alphas = new long[] {   0,   0, 100,  80,   0, 400,   0, 200, 160,   0 };

    //                                             Total    PlacesInvariants
    private final int[][][] placeInvariants = { { {  3  }, { 0, 1, 2, 3, 4, 5, 6, 9 } },   // Pi0
                                                { {  1  }, { 2, 3, 4, 7             } },   // Pi1
                                                { {  1  }, { 4, 5, 6, 8             } } }; // Pi2

    private long[] timeStamps;
    private int maxInvariants;
    private int[] transitionCounters;
    private Logger logger;

    public PetriNet(int maxInvariants, Logger logger) {
        this.maxInvariants = maxInvariants;
        this.logger = logger;
        this.transitionCounters = new int[incidenceMatrix[0].length];
        this.timeStamps = new long[incidenceMatrix[0].length];
        boolean[] initialSensitized = getSensitizedTransitionsByMarking();
        long now = System.currentTimeMillis();
        for (int i = 0; i < initialSensitized.length; i++) {
            if (initialSensitized[i]) {
                timeStamps[i] = now;
            }
        }
    }

    public int fireTransition(int transition) {

        // If he transition has already reached the maximum number of firings, we return -1 to indicate that it cannot be fired anymore.
        if (transitionCounters[transition] >= maxInvariants) {
            return -1;
        }
        
        // If the transition is not enabled y tokens, we return -1 to indicate that it cannot be fired.
        if (!getSensitizedTransitionsByMarking()[transition]) {
            return -1; 
        }

        // Calculate the time to wait for the transition to be fired based on its alpha value and the last time it was fired.
        long currentTime = System.currentTimeMillis();
        long timeToWait = (timeStamps[transition] + alphas[transition]) - currentTime;

        // If the transition is enabled but the required time has not yet passed, we return the remaining time to wait.
        if (timeToWait > 0) {
            return (int) timeToWait;
        }

        // If the transition is enabled and the required time has passed, we proceed to fire the transition by updating the marking.
        boolean[] sensitizedBefore = getSensitizedTransitionsByMarking();
        for (int i = 0; i < marking.length; i++) {
            marking[i] += incidenceMatrix[i][transition];
        }

        // Check if the place invariants are still satisfied after firing the transition. If not, throw an exception.
        verifyPlaceInvariants();

        // Update timeStamps for transitions that are now sensitized after firing the transition. Only update timeStamps for transitions that are now sensitized and were not sensitized before.
        boolean[] sensitizedAfter = getSensitizedTransitionsByMarking();
        long now = System.currentTimeMillis();
        for (int j = 0; j < sensitizedAfter.length; j++) {
            if (sensitizedAfter[j] && (!sensitizedBefore[j] || j == transition)) {
                timeStamps[j] = now;
            }
        }

        // Increment the counter for the fired transition and log the firing of the transition.
        transitionCounters[transition]++;
        logger.logTransitionFiring(transition, marking, transitionCounters);
        
        // Return 0 to indicate that the transition was fired successfully.
        return 0;
    }

    private void verifyPlaceInvariants() {
        for (int k = 0; k < placeInvariants.length; k++) {
            int sum = 0;
            for (int idx : placeInvariants[k][1]) {
                sum += marking[idx];
            }
            if (sum != placeInvariants[k][0][0]) {
                throw new IllegalStateException("Place invariant violated");
            }
        }
    }

    public boolean[] getSensitizedTransitionsByMarking(){
        boolean[] output = new boolean[incidenceMatrix[0].length];
        for (int j = 0; j < incidenceMatrix[0].length; j++) {
            if (transitionCounters[j] >= maxInvariants) {
                output[j] = false;
                continue;
            }
            boolean isSensitized = true;
            for (int i = 0; i < marking.length; i++) {
                if (marking[i] < -incidenceMatrix[i][j]) {
                    isSensitized = false;
                    break;
                }
            }
            output[j] = isSensitized;
        }
        return output;
    }
    
    public int[][] getIncidenceMatrix() { return incidenceMatrix; }

    public int[] getTransitionCounters() { return transitionCounters; }
}
