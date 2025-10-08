package v2.petrinet.src;

public class Setup {

    /*
     * VARIABLES
     */

    private static final Integer[][] INCIDENCE_MATRIX = {
        // T0  T1  T2  T3  T4  T5  T6  T7  T8  T9  T10 T11
        { -1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1  }, // P0
        { -1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0  }, // P1
        {  1, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0  }, // P2
        {  0,  1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  0  }, // P3
        { -1,  0,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0  }, // P4
        {  0,  0,  1,  0,  0, -1,  0,  0,  0,  0,  0,  0  }, // P5
        {  0,  0, -1,  0,  0,  1,  0,  0,  0,  0,  0,  0  }, // P6
        {  0,  0,  0, -1,  1,  0,  0,  0,  0,  0,  0,  0  }, // P7
        {  0,  0,  0,  1, -1,  0,  0,  0,  0,  0,  0,  0  }, // P8
        {  0,  0,  0,  0,  1,  1, -1, -1,  0,  0,  0,  0  }, // P9
        {  0,  0,  0,  0,  0,  0, -1, -1,  1,  0,  1,  0  }, // P10
        {  0,  0,  0,  0,  0,  0,  1,  0,  0, -1,  0,  0  }, // P11
        {  0,  0,  0,  0,  0,  0,  0,  1, -1,  0,  0,  0  }, // P12
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, -1,  0  }, // P13
        {  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  1, -1  }  // P14 
    };
    private static final Integer[] MAIN_PLACES = {
        1, // P0
        0, // P1
        1, // P2
        1, // P3
        0, // P4
        1, // P5
        0, // P6
        0, // P7
        1, // P8
        1, // P9
        0, // P10
        1, // P11
        1, // P12
        1, // P13
        1  // P14
    };
    private static final Integer[] INITIAL_MARKING = {
        5, // P0
        1, // P1
        0, // P2
        0, // P3
        5, // P4
        0, // P5
        1, // P6
        1, // P7
        0, // P8
        0, // P9
        1, // P10
        0, // P11
        0, // P12
        0, // P13
        0  // P14
    };
    private static final Integer[] PETRINET_PLACE_LIMITS = {
        0,  // P_i
        14, // P_o
    };
    private static final Integer[][] THREADS_TRANSITIONS_MATRIX = {
        // T0  T1  T2  T3  T4  T5  T6  T7  T8  T9  T10 T11
        {  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0  }, // Thread 0
        {  0,  0,  1,  0,  0,  1,  0,  0,  0,  0,  0,  0  }, // Thread 1
        {  0,  0,  0,  1,  1,  0,  0,  0,  0,  0,  0,  0  }, // Thread 2
        {  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0  }, // Thread 3
        {  0,  0,  0,  0,  0,  0,  0,  1,  1,  0,  0,  0  }, // Thread 4
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1  }, // Thread 5
    };
    private static final Integer[][] THREADS_TRANSITION_LIMITS_MATRIX = {
        // T_i  T_o
        {  0,   1   }, // Thread 0
        {  2,   5   }, // Thread 1
        {  3,   4   }, // Thread 2
        {  6,   10  }, // Thread 3
        {  7,   8   }, // Thread 4
        {  11,  11  }  // Thread 5
    };
    private static final Integer[][] DELAY_TIME_LIMITS_MATRIX = {
        // Min  Max
        {  0,   0   }, // T0
        {  5,   40  }, // T1
        {  0,   0   }, // T2
        {  0,   0   }, // T3
        {  5,   40  }, // T4
        {  5,   40  }, // T5
        {  0,   1   }, // T6
        {  0,   0   }, // T7
        {  5,   40  }, // T8
        {  5,   40  }, // T9
        {  5,   40  }, // T10
        {  0,   0   }  // T11
    };
    /*
    private static final Integer[][] DELAY_TIME_LIMITS_MATRIX = {
        // Min  Max
        {  0,   0  }, // T0
        {  0,   0  }, // T1
        {  0,   0  }, // T2
        {  0,   0  }, // T3
        {  0,   0  }, // T4
        {  0,   0  }, // T5
        {  0,   0  }, // T6
        {  0,   0  }, // T7
        {  0,   0  }, // T8
        {  0,   0  }, // T9
        {  0,   0  }, // T10
        {  0,   0  }  // T11
    };
    */
    private static final Float[] PROBABILITIES = {
        1.00f, // Thread 0
        0.50f, // Thread 1
        0.50f, // Thread 2
        0.50f, // Thread 3
        0.50f, // Thread 4
        1.00f  // Thread 5
    };
    /*
    private static final Float[] PROBABILITIES = {
        1.00f, // Thread 0
        0.75f, // Thread 1
        0.25f, // Thread 2
        0.80f, // Thread 3
        0.20f, // Thread 4
        1.00f  // Thread 5
    };
    */
    private static final Integer MAX_THREAD_COMPLETION_COUNTER = 186;
    private static final Integer PLACES_QUANTITY = INCIDENCE_MATRIX.length;
    private static final Integer TRANSITIONS_QUANTITY = INCIDENCE_MATRIX[0].length;
    private static final Integer THREADS_QUANTITY = THREADS_TRANSITIONS_MATRIX.length;
    private static final Integer PLACE_INVARIANT = INITIAL_MARKING[0];

    /*
     * GETTERS AND SETTERS
     */

    public static final Integer[][] getIncidenceMatrix() { return INCIDENCE_MATRIX; }

    public static final Integer[] getMainPlaces() { return MAIN_PLACES; }

    public static final Integer[] getInitialMarking() { return INITIAL_MARKING; }

    public static final Integer[] getPetrinetPlaceLimits() { return PETRINET_PLACE_LIMITS; }

    public static final Integer[][] getThreadsTransitionsMatrix() { return THREADS_TRANSITIONS_MATRIX; }

    public static final Integer[][] getThreadsTransitionLimitsMatrix() { return THREADS_TRANSITION_LIMITS_MATRIX; }

    public static final Integer[][] getDelayTimeLimitsMatrix() { return DELAY_TIME_LIMITS_MATRIX; }

    public static final Float[] getProbabilities() { return PROBABILITIES; }

    public static final Integer getMaxThreadCompletionCounter() { return MAX_THREAD_COMPLETION_COUNTER; }

    public static final Integer getPlacesQuantity() { return PLACES_QUANTITY; }

    public static final Integer getTransitionsQuantity() { return TRANSITIONS_QUANTITY; }

    public static final Integer getThreadsQuantity() { return THREADS_QUANTITY; }

    public static final Integer getPlaceInvariant() { return PLACE_INVARIANT; }
}
