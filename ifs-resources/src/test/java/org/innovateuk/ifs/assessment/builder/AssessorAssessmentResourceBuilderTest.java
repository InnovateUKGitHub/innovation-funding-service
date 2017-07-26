package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.junit.Assert.assertEquals;

public class AssessorAssessmentResourceBuilderTest {

    @Test
    public void testBuildOne() {
        long expectedApplicationId = 1L;
        String expectedApplicationName = "Test Application";
        String expectedLeadOrganisation = "Test Lead Organisation";
        int expectedTotalAssessors = 10;

        AssessorAssessmentResource resource = newAssessorAssessmentResource()
                .withApplicationId(expectedApplicationId)
                .withApplicationName(expectedApplicationName)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withTotalAssessors(expectedTotalAssessors)
                .build();

        assertEquals(expectedApplicationId, resource.getApplicationId());
        assertEquals(expectedApplicationName, resource.getApplicationName());
        assertEquals(expectedLeadOrganisation, resource.getLeadOrganisation());
        assertEquals(expectedTotalAssessors, resource.getTotalAssessors());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedApplicationId = {1L, 2L};
        String[] expectedApplicationName = {"Test Application 1", "Test Application 2"};
        String[] expectedLeadOrganisation = {"Test Lead Organisation 1", "Test Lead Organisation 2"};
        Integer[] expectedTotalAssessors = {10, 20};

        List<AssessorAssessmentResource> resources = newAssessorAssessmentResource()
                .withApplicationId(expectedApplicationId)
                .withApplicationName(expectedApplicationName)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withTotalAssessors(expectedTotalAssessors)
                .build(2);

        assertEquals(expectedApplicationId[0].longValue(), resources.get(0).getApplicationId());
        assertEquals(expectedApplicationName[0], resources.get(0).getApplicationName());
        assertEquals(expectedLeadOrganisation[0], resources.get(0).getLeadOrganisation());
        assertEquals(expectedTotalAssessors[0].intValue(), resources.get(0).getTotalAssessors());

        assertEquals(expectedApplicationId[1].longValue(), resources.get(1).getApplicationId());
        assertEquals(expectedApplicationName[1], resources.get(1).getApplicationName());
        assertEquals(expectedLeadOrganisation[1], resources.get(1).getLeadOrganisation());
        assertEquals(expectedTotalAssessors[1].intValue(), resources.get(1).getTotalAssessors());
    }
}
