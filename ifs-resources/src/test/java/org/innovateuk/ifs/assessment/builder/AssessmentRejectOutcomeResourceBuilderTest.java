package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.NOT_AREA_OF_EXPERTISE;
import static org.junit.Assert.assertEquals;

public class AssessmentRejectOutcomeResourceBuilderTest {

    @Test
    public void buildOne() {
        AssessmentRejectOutcomeValue expectedRejectReason = NOT_AREA_OF_EXPERTISE;
        String expectedRejectComment = "Comment";

        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(expectedRejectReason)
                .withRejectComment(expectedRejectComment)
                .build();

        assertEquals(expectedRejectReason, assessmentRejectOutcomeResource.getRejectReason());
        assertEquals(expectedRejectComment, assessmentRejectOutcomeResource.getRejectComment());
    }

    @Test
    public void buildMany() {
        AssessmentRejectOutcomeValue[] expectedRejectReasons = {NOT_AREA_OF_EXPERTISE, CONFLICT_OF_INTEREST};
        String[] expectedRejectComments = {"Comment 1", "Comment 2"};

        List<AssessmentRejectOutcomeResource> assessmentRejectOutcomeResources = newAssessmentRejectOutcomeResource()
                .withRejectReason(expectedRejectReasons)
                .withRejectComment(expectedRejectComments)
                .build(2);

        AssessmentRejectOutcomeResource first = assessmentRejectOutcomeResources.get(0);

        assertEquals(expectedRejectReasons[0], first.getRejectReason());
        assertEquals(expectedRejectComments[0], first.getRejectComment());

        AssessmentRejectOutcomeResource second = assessmentRejectOutcomeResources.get(1);

        assertEquals(expectedRejectReasons[1], second.getRejectReason());
        assertEquals(expectedRejectComments[1], second.getRejectComment());
    }

}
