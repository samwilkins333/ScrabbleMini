package Scrabble;

import java.util.Arrays;

class Util {

    static boolean areValidIndices(int[] numbers) {
        boolean res = Arrays.stream(numbers).allMatch(n -> n >= 0 && n <= 14);
        return res;
    }

}
