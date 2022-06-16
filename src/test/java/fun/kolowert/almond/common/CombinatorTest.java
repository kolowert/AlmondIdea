package fun.kolowert.almond.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombinatorTest {

    @Test
    void ShouldMake3Iterations() {
        int subSet = 2;
        int superSet = 3;
        Combinator combinator = new Combinator(subSet, superSet);
        int counter = 0;
        while (!combinator.isFinished()) {
            ++counter;
            combinator.makeNext();
        }
        int expected = 3;
        assertEquals(expected, counter);
    }

    @Test
    void ShoultCalculateCombinationsQuantity() {
        int subSet = 5;
        int superSet = 45;
        long combinations = Combinator.calculateCombinations(subSet, superSet);
        long expected = 1_221_759;
        assertEquals(expected, combinations);
    }
}