package com.worth.ifs.competitionsetup.utils;

import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Test;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.resource.CompetitionStatus.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;


public class CompetitionUtilsTest {

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

    @Test
    public void testIsSendToDashboard() {
        CompetitionResource competitionResource = newCompetitionResource().withCompetitionStatus(COMPETITION_SETUP).build();
        assertEquals(FALSE, CompetitionUtils.isSendToDashboard(competitionResource));
        competitionResource = newCompetitionResource().withCompetitionStatus(READY_TO_OPEN).build();
        assertEquals(FALSE, CompetitionUtils.isSendToDashboard(competitionResource));

        competitionResource = null;
        assertEquals(TRUE, CompetitionUtils.isSendToDashboard(competitionResource));
        competitionResource = newCompetitionResource().withCompetitionStatus(OPEN).build();
        assertEquals(TRUE, CompetitionUtils.isSendToDashboard(competitionResource));
        competitionResource = newCompetitionResource().withCompetitionStatus(CLOSED).build();
        assertEquals(TRUE, CompetitionUtils.isSendToDashboard(competitionResource));
        competitionResource = newCompetitionResource().with((integer, competitionResource1) -> competitionResource1.setCompetitionStatus(null)).build();
        assertEquals(TRUE, CompetitionUtils.isSendToDashboard(competitionResource));
    }
}
