package org.innovateuk.ifs.competitionsetup.utils;

import org.junit.Test;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CompetitionUtilsTest {

    @Test
    public void testAllInnovationAreas() {
        assertTrue(CompetitionUtils.ALL_INNOVATION_AREAS.equals(-1L));
    }

    @Test
    public void testTextToBoolean() {
        assertEquals(TRUE, CompetitionUtils.textToBoolean("yes"));
        assertEquals(TRUE, CompetitionUtils.textToBoolean("YeS"));
        assertEquals(FALSE, CompetitionUtils.textToBoolean("no"));
        assertEquals(FALSE, CompetitionUtils.textToBoolean("NO"));
        assertEquals(FALSE, CompetitionUtils.textToBoolean("8y7af87af"));
        assertEquals(FALSE, CompetitionUtils.textToBoolean(null));
    }

    @Test
    public void testBooleanToText() {
        assertEquals("yes", CompetitionUtils.booleanToText(TRUE));
        assertEquals("yes", CompetitionUtils.booleanToText(true));
        assertEquals("no", CompetitionUtils.booleanToText(FALSE));
        assertEquals("no", CompetitionUtils.booleanToText(false));
        assertEquals("", CompetitionUtils.booleanToText(null));
    }
}
