package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.form.resource.FormInputResource;

import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * Holder of model attributes for the feedback given as part of the assessment journey to a question for an application.
 */
public class AssessmentFeedbackViewModel {

    private long daysLeft;
    private long daysLeftPercentage;
    private CompetitionResource competition;
    private ApplicationResource application;
    private Long questionId;
    private String questionNumber;
    private String questionShortName;
    private String questionName;
    private Integer maximumScore;
    private String applicantResponse;
    private List<FormInputResource> assessmentFormInputs;
    private boolean scoreFormInputExists;
    private boolean scopeFormInputExists;
    private boolean appendixExists;
    private FileDetailsViewModel appendixDetails;

    public AssessmentFeedbackViewModel(long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, Long questionId, String questionNumber, String questionShortName, String questionName, Integer maximumScore, String applicantResponse, List<FormInputResource> assessmentFormInputs, boolean scoreFormInputExists, boolean scopeFormInputExists) {
        this(daysLeft, daysLeftPercentage, competition, application, questionId, questionNumber, questionShortName, questionName, maximumScore, applicantResponse, assessmentFormInputs, scoreFormInputExists, scopeFormInputExists, false, null);
    }

    public AssessmentFeedbackViewModel(long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, Long questionId, String questionNumber, String questionShortName, String questionName, Integer maximumScore, String applicantResponse, List<FormInputResource> assessmentFormInputs, boolean scoreFormInputExists, boolean scopeFormInputExists, boolean appendixExists, FileDetailsViewModel appendixDetails) {
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.competition = competition;
        this.application = application;
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.questionShortName = questionShortName;
        this.questionName = questionName;
        this.maximumScore = maximumScore;
        this.applicantResponse = applicantResponse;
        this.assessmentFormInputs = assessmentFormInputs;
        this.scoreFormInputExists = scoreFormInputExists;
        this.scopeFormInputExists = scopeFormInputExists;
        this.appendixExists = appendixExists;
        this.appendixDetails = appendixDetails;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionShortName() {
        return questionShortName;
    }

    public String getQuestionName() {
        return questionName;
    }

    public Integer getMaximumScore() {
        return maximumScore;
    }

    public String getApplicantResponse() {
        return applicantResponse;
    }

    public List<FormInputResource> getAssessmentFormInputs() {
        return assessmentFormInputs;
    }

    public boolean isScoreFormInputExists() {
        return scoreFormInputExists;
    }

    public boolean isScopeFormInputExists() {
        return scopeFormInputExists;
    }

    public boolean isAppendixExists() {
        return appendixExists;
    }

    public FileDetailsViewModel getAppendixDetails() {
        return appendixDetails;
    }

    public String getAppendixFileDescription() {
        return format("View %s appendix", lowerCase(getQuestionShortName()));
    }
}
