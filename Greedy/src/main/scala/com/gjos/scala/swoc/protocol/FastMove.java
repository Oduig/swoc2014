package com.gjos.scala.swoc.protocol;

// Encoding:
// 0 == Pass
// movetype from to
// 1|2 00-80 00-80
// e.g. 24105
public class FastMove {
    // Encode moveType -> from -> to, where 0 <= from < 81 && 0 <= to < 81 && moveType > 0
    public static int[][][] encode = new int[3][][];

    // Decode int -> moveType
    public static int[] moveType = new int[28181];

    // Decode int -> from
    public static int[] from = new int[28181];

    // Deocde int -> to
    public static int[] to = new int[28181];

    public static void init() {
        for (int mt = 1; mt < 3; mt++) {
            encode[mt] = new int[81][];
            for (int f = 0; f < 81; f++) {
                encode[mt][f] = new int[81];
                for (int t = 0; t < 81; t++) {
                    encode[mt][f][t] = mt * 10000 + f * 100 + t;
                }
            }
        }

        for (int m = 0; m < 28181; m++) {
            int mt = m / 10000;
            moveType[m] = mt;
            from[m] = (mt > 0) ? (m % 10000) / 100 : -1;
            to[m] = (mt > 0) ? m % 100 : -1;
        }
    }
}
