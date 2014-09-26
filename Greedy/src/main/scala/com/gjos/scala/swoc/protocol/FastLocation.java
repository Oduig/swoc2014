package com.gjos.scala.swoc.protocol;

public class FastLocation {
    public static boolean[] valid = new boolean[] {
            true, true, true, true, true, false, false, false, false,
            true, true, true, true, true, true, false, false, false,
            true, true, true, true, true, true, true, false, false,
            true, true, true, true, true, true, true, true, false,
            true, true, true, true, false, true, true, true, true,
            false, true, true, true, true, true, true, true, true,
            false, false, true, true, true, true, true, true, true,
            false, false, false, true, true, true, true, true, true,
            false, false, false, false, true, true, true, true, true,
            false, false, false, false, false, false, false, false, false
    };

    public static boolean isValid(int location) {
        return valid[location];
    }

    public static boolean isValid(int x, int y) {
        return valid[y * 9 + x];
    }
}
