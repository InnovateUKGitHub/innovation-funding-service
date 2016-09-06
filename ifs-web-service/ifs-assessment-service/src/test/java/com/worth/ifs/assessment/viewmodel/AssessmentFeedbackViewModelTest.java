package com.worth.ifs.assessment.viewmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssessmentFeedbackViewModelTest {

    @Test
    public void testGetAppendixFileDescription() throws Exception {
        String questionShortName = "Technical approach";

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = new AssessmentFeedbackViewModel(0L, 0L, null, null, null, null, questionShortName, null, null, null, null, false, false);

        assertEquals("View technical approach appendix", assessmentFeedbackViewModel.getAppendixFileDescription());
    }
}