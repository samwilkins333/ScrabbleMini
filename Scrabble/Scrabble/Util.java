package Scrabble;

import java.util.Arrays;
import static Scrabble.PlayerNumber.*;
import static Scrabble.PlayerType.*;

class Util {

    static boolean areValidIndices(int[] numbers) {
        return Arrays.stream(numbers).allMatch(n -> n >= 0 && n <= 14);
    }

    static PlayerNumber invert(PlayerNumber player) {
        return (player == One) ? Two : One;
    }

    static PlayerType invert(PlayerType type) {
        return (type == Human) ? AI : Human;
    }

}
