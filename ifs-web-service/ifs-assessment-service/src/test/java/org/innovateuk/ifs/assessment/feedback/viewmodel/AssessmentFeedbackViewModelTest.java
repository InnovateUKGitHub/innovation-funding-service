package org.innovateuk.ifs.assessment.feedback.viewmodel;

import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackViewModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssessmentFeedbackViewModelTest {

    @Test
    public void testGetAppendixFileDescription() throws Exception {
        String questionShortName = "Technical approach";

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = new AssessmentFeedbackViewModel(0L,
                0L,
                0L,
                0L,
                null,
                0L,
                null,
                questionShortName,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                null,
                null
        );

        assertEquals("View technical approach appendix", assessmentFeedbackViewModel.getAppendixFileDescription());
    }
}
