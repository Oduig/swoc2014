package com.gjos.scala.swoc.protocol;

public class Score {
    /**
     * Returns the current board's utility from a given player's perspective.
     * Rates the board by the difference between us and them,
     * where score is determined by how close we are to having a type of stones eliminated
     */
    public static int score(FastBoard board, int us) {
        if (ScoreCache.hasKey(board)) {
            return ScoreCache.get(board);
        } else {
            int them = Player.opponent(us);

            int myScore = utility(board, us);
            int theirScore = utility(board, them);
            //println(s"Me: $myScore, them: $theirScore")

            int score;
            if (theirScore <= 0) {
                // If we can win this turn, the rest doesn't matter.
                score = Integer.MAX_VALUE;
            } else if (myScore <= 0) {
                // If we made a losing move, it's bad.
                score = Integer.MIN_VALUE;
            } else {
//                int ourValidMoves = Player.allValidMoves(us, board, true).length;
//                int theirValidMoves = Player.allValidMoves(them, board, true).length;
//                score = (ourValidMoves - theirValidMoves) * 10 + myScore - theirScore;
                score = myScore - theirScore;
            }
            ScoreCache.add(board, score);
            return score;
        }
    }

    /**
     * Best primary metric seems to be the minimal type of stone,
     * aka how close we are to death.
     * Still, it's good to factor in other stones slightly,
     * because we cannot always take their most valuable stone.
     */
    public static int utility(FastBoard b, int p) {
        int location = 0;
        int pebbleValue = 0;
        int rockValue = 0;
        int boulderValue = 0;
        while (location < 81) {
            int field = b.getField(location);
            if (Field.player(field) == p) {
                int stone = Field.stone(field);
                int value = (int) Math.pow(Field.height(field) * 10, 1.4);
                if (stone == Stone.Pebble()) {
                    pebbleValue += value;
                } else if (stone == Stone.Rock()) {
                    rockValue += value;
                } else {
                    boulderValue += value;
                }
            }
            location += 1;
        }

        //println(s"Pebble: $pebbleValue, Rock: $rockValue, Boulder: $boulderValue")
        int minScore = Math.min(pebbleValue, Math.min(rockValue, boulderValue));
        int totalScore = pebbleValue + rockValue + boulderValue;
        return minScore <= 0 ? Integer.MIN_VALUE : minScore * 10 + totalScore;
    }
}
