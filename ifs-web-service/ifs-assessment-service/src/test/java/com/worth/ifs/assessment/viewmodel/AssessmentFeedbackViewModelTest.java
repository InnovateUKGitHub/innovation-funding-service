package com.worth.ifs.assessment.viewmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AssessmentFeedbackViewModelTest {

    @Test
    public void getAppendixFileDescription() throws Exception {
        final String questionShortName = "Technical approach";
        final AssessmentFeedbackViewModel assessmentFeedbackViewModel = new AssessmentFeedbackViewModel(0L, 0L, null, null, null, null, questionShortName, null, null, false, false, false, false, null, null, null, null);

        assertEquals("View technical approach appendix", assessmentFeedbackViewModel.getAppendixFileDescription());
    }

}