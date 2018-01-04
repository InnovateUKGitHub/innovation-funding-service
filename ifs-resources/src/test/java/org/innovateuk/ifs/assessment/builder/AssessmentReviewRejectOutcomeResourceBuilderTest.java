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
        String expectedRejectComment = "Comment";

        AssessmentReviewRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentReviewRejectOutcomeResource()
                .withRejectComment(expectedRejectComment)
                .build();

        assertEquals(expectedRejectComment, assessmentRejectOutcomeResource.getReason());
    }

    @Test
    public void buildMany() {
        String[] expectedRejectComments = {"Comment 1", "Comment 2"};

        List<AssessmentReviewRejectOutcomeResource> assessmentReviewRejectOutcomeResources = newAssessmentReviewRejectOutcomeResource()
                .withRejectComment(expectedRejectComments)
                .build(2);

        AssessmentReviewRejectOutcomeResource first = assessmentReviewRejectOutcomeResources.get(0);

        assertEquals(expectedRejectComments[0], first.getReason());

        AssessmentReviewRejectOutcomeResource second = assessmentReviewRejectOutcomeResources.get(1);

        assertEquals(expectedRejectComments[1], second.getReason());
    }
}
