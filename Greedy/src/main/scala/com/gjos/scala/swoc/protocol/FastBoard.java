package com.gjos.scala.swoc.protocol;

import java.util.Arrays;

public class FastBoard {
    int[] _state;

    public FastBoard(int[] state) {
        _state = state;
    }

    public int getField(int location) {
        return _state[location];
    }

    public void setField(int location, int field) {
      _state[location] = field;
    }

    public FastBoard copy() {
        return new FastBoard(_state.clone());
    }

    public int score(Player us) {
        return Score.score(this, us);
    }

    public int myHashCode() {
        return Arrays.hashCode(_state);
    }

    public static int diameter = 9;
}
