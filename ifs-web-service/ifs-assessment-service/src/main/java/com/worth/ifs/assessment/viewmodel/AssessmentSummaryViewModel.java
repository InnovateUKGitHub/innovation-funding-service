package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;

import java.util.List;

/**
 *
 */
public class AssessmentSummaryViewModel {
    private List<AssessmentFeedbackResource> listOfAssessmentFeedback;


    public AssessmentSummaryViewModel(List<AssessmentFeedbackResource> listOfAssessmentFeedback){
        this.listOfAssessmentFeedback = listOfAssessmentFeedback;
    }

    public List<AssessmentFeedbackResource> getListOfAssessmentFeedback(){
        return this.listOfAssessmentFeedback;
    }
}
