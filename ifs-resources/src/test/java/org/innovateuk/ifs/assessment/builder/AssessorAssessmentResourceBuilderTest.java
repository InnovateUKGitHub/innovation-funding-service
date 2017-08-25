package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.NOT_AREA_OF_EXPERTISE;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED;
import static org.junit.Assert.assertEquals;

public class AssessorAssessmentResourceBuilderTest {

    @Test
    public void testBuildOne() {
        long expectedApplicationId = 1L;
        String expectedApplicationName = "Test Application";
        String expectedLeadOrganisation = "Test Lead Organisation";
        int expectedTotalAssessors = 10;
        AssessmentState expectedState = REJECTED;
        AssessmentRejectOutcomeValue expectedAssessmentRejectOutcomeValue = CONFLICT_OF_INTEREST;
        String expectedRejectionComment = "rejection comment";
        long expectedAssessmentId = 2L;

        AssessorAssessmentResource resource = newAssessorAssessmentResource()
                .withApplicationId(expectedApplicationId)
                .withApplicationName(expectedApplicationName)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withTotalAssessors(expectedTotalAssessors)
                .withState(expectedState)
                .withRejectionReason(expectedAssessmentRejectOutcomeValue)
                .withRejectionComment(expectedRejectionComment)
                .withAssessmentId(expectedAssessmentId)
                .build();

        assertEquals(expectedApplicationId, resource.getApplicationId());
        assertEquals(expectedApplicationName, resource.getApplicationName());
        assertEquals(expectedLeadOrganisation, resource.getLeadOrganisation());
        assertEquals(expectedTotalAssessors, resource.getTotalAssessors());
        assertEquals(expectedState, resource.getState());
        assertEquals(expectedAssessmentRejectOutcomeValue, resource.getRejectReason());
        assertEquals(expectedRejectionComment, resource.getRejectComment());
        assertEquals(expectedAssessmentId, resource.getAssessmentId());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedApplicationIds = {1L, 2L};
        String[] expectedApplicationNames = {"Test Application 1", "Test Application 2"};
        String[] expectedLeadOrganisations = {"Test Lead Organisation 1", "Test Lead Organisation 2"};
        Integer[] expectedTotalAssessors = {10, 20};
        AssessmentState[] expectedStates = {REJECTED, REJECTED};
        AssessmentRejectOutcomeValue[] expectedAssessmentRejectOutcomeValues = {CONFLICT_OF_INTEREST, NOT_AREA_OF_EXPERTISE};
        String[] expectedRejectionComments = {"rejection comment 1", "rejection comment 2"};
        Long[] expectedAssessmentIds = {3L, 4L};

        List<AssessorAssessmentResource> resources = newAssessorAssessmentResource()
                .withApplicationId(expectedApplicationIds)
                .withApplicationName(expectedApplicationNames)
                .withLeadOrganisation(expectedLeadOrganisations)
                .withTotalAssessors(expectedTotalAssessors)
                .withState(expectedStates)
                .withRejectionReason(expectedAssessmentRejectOutcomeValues)
                .withRejectionComment(expectedRejectionComments)
                .withAssessmentId(expectedAssessmentIds)
                .build(2);

        assertEquals(expectedApplicationIds[0].longValue(), resources.get(0).getApplicationId());
        assertEquals(expectedApplicationNames[0], resources.get(0).getApplicationName());
        assertEquals(expectedLeadOrganisations[0], resources.get(0).getLeadOrganisation());
        assertEquals(expectedTotalAssessors[0].intValue(), resources.get(0).getTotalAssessors());
        assertEquals(expectedStates[0], resources.get(0).getState());
        assertEquals(expectedAssessmentRejectOutcomeValues[0], resources.get(0).getRejectReason());
        assertEquals(expectedRejectionComments[0], resources.get(0).getRejectComment());
        assertEquals(expectedAssessmentIds[0].longValue(), resources.get(0).getAssessmentId());

        assertEquals(expectedApplicationIds[1].longValue(), resources.get(1).getApplicationId());
        assertEquals(expectedApplicationNames[1], resources.get(1).getApplicationName());
        assertEquals(expectedLeadOrganisations[1], resources.get(1).getLeadOrganisation());
        assertEquals(expectedStates[1], resources.get(1).getState());
        assertEquals(expectedTotalAssessors[1].intValue(), resources.get(1).getTotalAssessors());
        assertEquals(expectedAssessmentRejectOutcomeValues[1], resources.get(1).getRejectReason());
        assertEquals(expectedRejectionComments[1], resources.get(1).getRejectComment());
        assertEquals(expectedAssessmentIds[1].longValue(), resources.get(1).getAssessmentId());
    }
}
