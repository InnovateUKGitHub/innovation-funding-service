package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.junit.Assert.assertEquals;

public class AssessorCompetitionSummaryResourceBuilderTest {

    @Test
    public void testBuildOne() {
        long expectedCompetitionId = 1L;
        String expectedCompetitionName = "Test Competition";
        AssessorProfileResource expectedAssessor = new AssessorProfileResource();
        long expectedTotalApplications = 10L;
        List<AssessorAssessmentResource> expectedAssignedAssessments = newAssessorAssessmentResource()
                .withApplicationId(1L)
                .withApplicationName("Test Application")
                .build(1);

        AssessorCompetitionSummaryResource resource = newAssessorCompetitionSummaryResource()
                .withCompetitionId(expectedCompetitionId)
                .withCompetitionName(expectedCompetitionName)
                .withAssessor(expectedAssessor)
                .withTotalApplications(expectedTotalApplications)
                .withAssignedAssessments(expectedAssignedAssessments)
                .build();

        assertEquals(expectedCompetitionId, resource.getCompetitionId());
        assertEquals(expectedCompetitionName, resource.getCompetitionName());
        assertEquals(expectedAssessor, resource.getAssessor());
        assertEquals(expectedTotalApplications, resource.getTotalApplications());
        assertEquals(expectedAssignedAssessments, resource.getAssignedAssessments());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedCompetitionIds = {1L, 2L};
        String[] expectedCompetitionNames = {"Test Competition 1", "Test Competition 2"};
        AssessorProfileResource[] expectedAssessors = newAssessorProfileResource().buildArray(2, AssessorProfileResource.class);
        Long[] expectedTotalApplications = {10L, 20L};
        List<AssessorAssessmentResource> expectedAssignedAssessments1 = newAssessorAssessmentResource().build(1);
        List<AssessorAssessmentResource> expectedAssignedAssessments2 = newAssessorAssessmentResource().build(1);

        List<AssessorCompetitionSummaryResource> resources = newAssessorCompetitionSummaryResource()
                .withCompetitionId(expectedCompetitionIds)
                .withCompetitionName(expectedCompetitionNames)
                .withAssessor(expectedAssessors)
                .withTotalApplications(expectedTotalApplications)
                .withAssignedAssessments(expectedAssignedAssessments1, expectedAssignedAssessments2)
                .build(2);

        assertEquals(expectedCompetitionIds[0].longValue(), resources.get(0).getCompetitionId());
        assertEquals(expectedCompetitionNames[0], resources.get(0).getCompetitionName());
        assertEquals(expectedAssessors[0], resources.get(0).getAssessor());
        assertEquals(expectedTotalApplications[0].intValue(), resources.get(0).getTotalApplications());
        assertEquals(expectedAssignedAssessments1, resources.get(0).getAssignedAssessments());

        assertEquals(expectedCompetitionIds[1].longValue(), resources.get(1).getCompetitionId());
        assertEquals(expectedCompetitionNames[1], resources.get(1).getCompetitionName());
        assertEquals(expectedAssessors[1], resources.get(1).getAssessor());
        assertEquals(expectedTotalApplications[1].intValue(), resources.get(1).getTotalApplications());
        assertEquals(expectedAssignedAssessments1, resources.get(1).getAssignedAssessments());
    }
}
