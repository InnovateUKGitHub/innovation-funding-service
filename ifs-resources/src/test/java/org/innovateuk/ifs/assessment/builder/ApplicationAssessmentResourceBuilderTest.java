package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.junit.Assert.assertEquals;

public class ApplicationAssessmentResourceBuilderTest {

    @Test
    public void buildOne() {
        ApplicationAssessmentResource applicationAssessmentResource = newApplicationAssessmentResource()
                .withApplicationId(1L)
                .withAssessmentId(2L)
                .withApplicationName("Test Application")
                .build();

        assertEquals(1L, applicationAssessmentResource.getApplicationId());
        assertEquals(2L, applicationAssessmentResource.getAssessmentId());
        assertEquals("Test Application", applicationAssessmentResource.getApplicationName());
    }

    @Test
    public void buildMany() {
        List<ApplicationAssessmentResource> applicationAssessmentResources = newApplicationAssessmentResource()
                .withApplicationId(1L, 2L)
                .withAssessmentId(80L, 81L)
                .withApplicationName("Test Application 1", "Test Application 2")
                .build(2);

        assertEquals(1L, applicationAssessmentResources.get(0).getApplicationId());
        assertEquals(2L, applicationAssessmentResources.get(1).getApplicationId());
        assertEquals(80L, applicationAssessmentResources.get(0).getAssessmentId());
        assertEquals(81L, applicationAssessmentResources.get(1).getAssessmentId());
        assertEquals("Test Application 1", applicationAssessmentResources.get(0).getApplicationName());
        assertEquals("Test Application 2", applicationAssessmentResources.get(1).getApplicationName());
    }
}