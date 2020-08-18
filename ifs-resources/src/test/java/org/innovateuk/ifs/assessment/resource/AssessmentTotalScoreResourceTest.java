package org.innovateuk.ifs.assessment.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssessmentTotalScoreResourceTest {

    @Test
    public void getTotalScorePercentage() throws Exception {
        assertEquals("Expected 50% since a score of 100 was given out of a possible 200", 50, getTotalScorePercentage(100, 200));
    }

    @Test
    public void getTotalScorePercentage_roundDown() throws Exception {
        assertEquals("Expected 50.4% to be rounded down to 50%", 50, getTotalScorePercentage(126, 250));
    }

    @Test
    public void getTotalScorePercentage_halfWayRoundUp() throws Exception {
        assertEquals("Expected 50.5% to be rounded up to 51%", 51, getTotalScorePercentage(101, 200));
    }

    @Test
    public void getTotalScorePercentage_roundUp() throws Exception {
        assertEquals("Expected 50.6% to be rounded up to 51%", 51, getTotalScorePercentage(253, 500));
    }

    @Test
    public void getTotalScorePercentage_noScore() throws Exception {
        assertEquals("Expected 0% since no score was given", 0, getTotalScorePercentage(0, 100));
    }

    @Test
    public void getTotalScorePercentage_noScorePossible() throws Exception {
        assertEquals("Expected 0% since no score is possible", 0, getTotalScorePercentage(50, 0));
    }

    @Test
    public void getTotalScorePercentage_maxScorePossible() throws Exception {
        assertEquals("Expected 100% since the score given equals the maximum score possible", 100, getTotalScorePercentage(50, 50));
    }

    @Test
    public void getTotalScorePercentage_zeroIfNoScoreGiven() {
        assertEquals(0, getTotalScorePercentage(0,0));
    }

    private int getTotalScorePercentage(int totalScoreGiven, int totalScorePossible) {
        return new AssessmentTotalScoreResource(totalScoreGiven, totalScorePossible).getTotalScorePercentage();
    }
}
