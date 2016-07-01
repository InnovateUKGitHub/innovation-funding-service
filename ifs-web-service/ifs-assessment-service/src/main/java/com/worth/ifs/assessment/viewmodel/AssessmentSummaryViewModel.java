package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.competition.resource.CompetitionResource;

import java.util.List;

/**
 * Holder of model attribute of Assessment Summary, which combine the all of questions and existing assessment feedback data
 * , application and competitions
 */
public class AssessmentSummaryViewModel {
    private List<QuestionResource> listOfQuestionResource;
    private List<AssessmentFeedbackResource> listOfAssessmentFeedbackResource;
    private ApplicationResource application;
    private CompetitionResource competition;

    public AssessmentSummaryViewModel(List<QuestionResource> listOfQuestionResource, List<AssessmentFeedbackResource> listOfAssessmentFeedback, ApplicationResource application, CompetitionResource competition) {
        this.listOfQuestionResource = listOfQuestionResource;
        this.listOfAssessmentFeedbackResource = listOfAssessmentFeedback;
        this.application = application;
        this.competition = competition;
    }


    public List<QuestionResource> getQuestionResource(){
        return this.listOfQuestionResource;
    }
    public List<AssessmentFeedbackResource> getAssessmentFeedbackResource(){
        return this.listOfAssessmentFeedbackResource;
    }
    public ApplicationResource getApplicationResource(){
        return this.application;
    }
    public CompetitionResource getCompetitionResource(){
        return this.competition;
    }
}
