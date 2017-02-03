package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.NOT_AREA_OF_EXPERTISE;
import static org.junit.Assert.assertEquals;

public class AssessmentRejectOutcomeBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        AssessmentRejectOutcomeValue expectedRejectReason = CONFLICT_OF_INTEREST;
        String expectedRejectComment = "Comment";

        AssessmentRejectOutcome assessmentRejectOutcome = newAssessmentRejectOutcome()
                .withId(expectedId)
                .withRejectReason(expectedRejectReason)
                .withRejectComment(expectedRejectComment)
                .build();

        assertEquals(expectedId, assessmentRejectOutcome.getId());
        assertEquals(expectedRejectReason, assessmentRejectOutcome.getRejectReason());
        assertEquals(expectedRejectComment, assessmentRejectOutcome.getRejectComment());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        AssessmentRejectOutcomeValue[] expectedRejectReasons = {CONFLICT_OF_INTEREST, NOT_AREA_OF_EXPERTISE};
        String[] expectedRejectComments = {"Comment 1", "Comment 2"};

        List<AssessmentRejectOutcome> assessmentRejectOutcomes = newAssessmentRejectOutcome()
                .withId(expectedIds)
                .withRejectReason(expectedRejectReasons)
                .withRejectComment(expectedRejectComments)
                .build(2);

        AssessmentRejectOutcome first = assessmentRejectOutcomes.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedRejectReasons[0], first.getRejectReason());
        assertEquals(expectedRejectComments[0], first.getRejectComment());

        AssessmentRejectOutcome second = assessmentRejectOutcomes.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedRejectReasons[1], second.getRejectReason());
        assertEquals(expectedRejectComments[1], second.getRejectComment());
    }

}