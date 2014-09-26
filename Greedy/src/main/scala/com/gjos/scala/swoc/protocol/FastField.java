package com.gjos.scala.swoc.protocol;

// player 0|1|2 stone 0|1|2|3 height 00-12
// eg 2307
public class FastField {
    // Encode player -> stone -> height, where 0 <= height < 20
    public static int[][][] encode = new int[3][][];

    // Decode int -> player
    public static int[] player = new int[2400];

    // Decode int -> stone
    public static int[] stone = new int[2400];

    // Deocde int -> height
    public static int[] height = new int[2400];

    public static void init() {
        for (int p = 1; p < 3; p++) {
            encode[p] = new int[4][];
            for (int s = 0; s < 4; s++) {
                encode[p][s] = new int[20];
                for (int h = 0; h < 20; h++) {
                    encode[p][s][h] = p * 1000 + s * 100 + h;
                }
            }
        }

        for (int f = 0; f < 2400; f++) {
            player[f] = f / 1000;
            stone[f] = f % 1000 / 100;
            height[f] = f % 100;
        }
    }
}
