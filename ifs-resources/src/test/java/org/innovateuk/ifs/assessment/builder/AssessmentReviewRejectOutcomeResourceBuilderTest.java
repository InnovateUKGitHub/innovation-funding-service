package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.NOT_AREA_OF_EXPERTISE;
import static org.junit.Assert.assertEquals;

public class AssessmentReviewRejectOutcomeResourceBuilderTest {

    @Test
    public void buildOne() {
        AssessmentRejectOutcomeValue expectedRejectReason = NOT_AREA_OF_EXPERTISE;
        String expectedRejectComment = "Comment";

        AssessmentReviewRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentReviewRejectOutcomeResource()
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

        List<AssessmentReviewRejectOutcomeResource> assessmentReviewRejectOutcomeResources = newAssessmentReviewRejectOutcomeResource()
                .withRejectReason(expectedRejectReasons)
                .withRejectComment(expectedRejectComments)
                .build(2);

        AssessmentReviewRejectOutcomeResource first = assessmentReviewRejectOutcomeResources.get(0);

        assertEquals(expectedRejectReasons[0], first.getRejectReason());
        assertEquals(expectedRejectComments[0], first.getRejectComment());

        AssessmentReviewRejectOutcomeResource second = assessmentReviewRejectOutcomeResources.get(1);

        assertEquals(expectedRejectReasons[1], second.getRejectReason());
        assertEquals(expectedRejectComments[1], second.getRejectComment());
    }

}
