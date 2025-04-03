package petrinet.src;

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
        8, // P0
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
    private static final Integer[][] PLACES_SEGMENTS_MATRIX = {
        // P0  P1  P2  P3  P4  P5  P6  P7  P8  P9  P10 P11 P12 P13 P14
        {  1,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0  }, // S0
        {  0,  0,  0,  1,  0,  1,  1,  0,  0,  1,  0,  0,  0,  0,  0  }, // S1
        {  0,  0,  0,  1,  0,  0,  0,  1,  1,  1,  0,  0,  0,  0,  0  }, // S2
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  0,  1,  1  }, // S3
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  0,  1,  0,  1  }, // S4
        {  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1  }  // S5
    };
    private static final Integer[][] TRANSITIONS_SEGMENTS_MATRIX = {
        // T0  T1  T2  T3  T4  T5  T6  T7  T8  T9  T10 T11
        {  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0  }, // S0
        {  0,  0,  1,  0,  0,  1,  0,  0,  0,  0,  0,  0  }, // S1
        {  0,  0,  0,  1,  1,  0,  0,  0,  0,  0,  0,  0  }, // S2
        {  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  1,  0  }, // S3
        {  0,  0,  0,  0,  0,  0,  0,  1,  1,  0,  0,  0  }, // S4
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1  }, // S5
    };
    private static final Integer[][] SEGMENTS_PLACE_LIMITS_MATRIX = {
        // P_i  P_o
        {  0,   3   }, // S0
        {  3,   9   }, // S1
        {  3,   9   }, // S2
        {  9,   14  }, // S3
        {  9,   14  }, // S4
        {  14,  0   }  // S5
    };

    private static final Integer[][] DELAY_TIME_LIMITS_MATRIX = {
        // Min  Max
        {  0,   0   }, // T0
        {  10,  50  }, // T1
        {  0,   0   }, // T2
        {  0,   0   }, // T3
        {  10,  50  }, // T4
        {  10,  50  }, // T5
        {  0,   1   }, // T6
        {  0,   0   }, // T7
        {  10,  50  }, // T8
        {  10,  50  }, // T9
        {  10,  50  }, // T10
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
        1.00f, // S0
        0.50f, // S1
        0.50f, // S2
        0.50f, // S3
        0.50f, // S4
        1.00f  // S5
    };
    /*
    private static final Float[] PROBABILITIES = {
        1.00f, // S0
        0.75f, // S1
        0.25f, // S2
        0.80f, // S3
        0.20f, // S4
        1.00f  // S5
    };
    */
    private static final Integer MAX_SEGMENTS_COMPLETION_COUNTER = 186;

    /*
     * GETTERS AND SETTERS
     */

    public static final Integer[][] getIncidenceMatrix() { return INCIDENCE_MATRIX; }

    public static final Integer[] getMainPlaces() { return MAIN_PLACES; }

    public static final Integer[] getInitialMarking() { return INITIAL_MARKING; }

    public static final Integer[][] getPlacesSegmentsMatrix() { return PLACES_SEGMENTS_MATRIX; }

    public static final Integer[][] getTransitionsSegmentsMatrix() { return TRANSITIONS_SEGMENTS_MATRIX; }

    public static final Integer[][] getSegmentsPlaceLimitsMatrix() { return SEGMENTS_PLACE_LIMITS_MATRIX; }

    public static final Integer[][] getDelayTimeLimitsMatrix() { return DELAY_TIME_LIMITS_MATRIX; }

    public static final Float[] getProbabilities() { return PROBABILITIES; }

    public static final Integer getMaxSegmentCompletionCounter() { return MAX_SEGMENTS_COMPLETION_COUNTER; }
}
