package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.junit.Assert.assertEquals;

public class ApplicationAssessmentResourceBuilderTest {

    @Test
    public void testOne() throws Exception {
        ApplicationAssessmentResource applicationAssessmentResource = newApplicationAssessmentResource()
                .withApplicationId(1L)
                .withAssessmentId(2L)
                .withCompetitionName("Test Competition")
                .build();

        assertEquals(1L, applicationAssessmentResource.getApplicationId());
        assertEquals(2L, applicationAssessmentResource.getAssessmentId());
        assertEquals("Test Competition", applicationAssessmentResource.getCompetitionName());
    }

    @Test
    public void testMany() throws Exception {
        List<ApplicationAssessmentResource> applicationAssessmentResources = newApplicationAssessmentResource()
                .withApplicationId(1L, 2L)
                .withAssessmentId(80L, 81L)
                .withCompetitionName("Test Competition")
                .build(2);

        assertEquals(1L, applicationAssessmentResources.get(0).getApplicationId());
        assertEquals(2L, applicationAssessmentResources.get(1).getApplicationId());
        assertEquals(80L, applicationAssessmentResources.get(0).getAssessmentId());
        assertEquals(81L, applicationAssessmentResources.get(1).getAssessmentId());
        assertEquals("Test Competition", applicationAssessmentResources.get(0).getCompetitionName());
        assertEquals("Test Competition", applicationAssessmentResources.get(1).getCompetitionName());
    }
}