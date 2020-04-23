package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.OVERVIEW;
import static org.junit.Assert.*;

public class CompetitionAssessmentConfigResourceBuilderTest {

    @Test
    public void buildOne() {

        Integer expectedAssessorCount = 3;
        BigDecimal expectedAssessorPay = BigDecimal.valueOf(100);

        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = newCompetitionAssessmentConfigResource()
                .withAssessorCount(expectedAssessorCount)
                .withAssessorFinanceView(OVERVIEW)
                .withAssessorPay(expectedAssessorPay)
                .withHasAssessmentPanel(false)
                .withHasInterviewStage(false)
                .withIncludeAverageAssessorScoreInNotifications(true)
                .build();

        assertEquals(expectedAssessorCount, competitionAssessmentConfigResource.getAssessorCount());
        assertEquals(expectedAssessorPay, competitionAssessmentConfigResource.getAssessorPay());
        assertEquals(OVERVIEW, competitionAssessmentConfigResource.getAssessorFinanceView());
        assertFalse(competitionAssessmentConfigResource.getHasAssessmentPanel());
        assertFalse(competitionAssessmentConfigResource.getHasInterviewStage());
        assertTrue(competitionAssessmentConfigResource.getIncludeAverageAssessorScoreInNotifications());
    }

    @Test
    public void buildMany() {

        Integer[] expectedAssessorCount = {3, 5};
        BigDecimal[] expectedAssessorPay = {BigDecimal.valueOf(100), BigDecimal.valueOf(90)};

        List<CompetitionAssessmentConfigResource> competitionAssessmentConfigResources = newCompetitionAssessmentConfigResource()
                .withAssessorCount(expectedAssessorCount)
                .withAssessorFinanceView(OVERVIEW)
                .withAssessorPay(expectedAssessorPay)
                .withHasAssessmentPanel(false)
                .withHasInterviewStage(false)
                .withIncludeAverageAssessorScoreInNotifications(true)
                .build(2);

        for(int i=0; i<competitionAssessmentConfigResources.size(); i++) {
            assertEquals(expectedAssessorCount[i], competitionAssessmentConfigResources.get(i).getAssessorCount());
            assertEquals(expectedAssessorPay[i], competitionAssessmentConfigResources.get(i).getAssessorPay());
            assertEquals(OVERVIEW, competitionAssessmentConfigResources.get(i).getAssessorFinanceView());
            assertFalse(competitionAssessmentConfigResources.get(i).getHasAssessmentPanel());
            assertFalse(competitionAssessmentConfigResources.get(i).getHasInterviewStage());
            assertTrue(competitionAssessmentConfigResources.get(i).getIncludeAverageAssessorScoreInNotifications());
        }
    }
}