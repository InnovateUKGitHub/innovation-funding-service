package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.form.resource.FormInputResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.lang.Integer.max;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * Holder of model attributes for the feedback given as part of the assessment journey to a question for an application.
 */
public class AssessmentFeedbackViewModel {

    private final long daysLeft;
    private final long daysLeftPercentage;
    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final Long questionId;
    private final String questionNumber;
    private final String questionShortName;
    private final String questionName;
    private final Integer maximumScore;
    private final String applicantResponse;
    private final List<FormInputResource> assessmentFormInputs;
    private final Map<Long, AssessorFormInputResponseResource> assessorResponses;
    private final boolean appendixExists;
    private final FileDetailsViewModel appendixDetails;

    public AssessmentFeedbackViewModel(long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, Long questionId, String questionNumber, String questionShortName, String questionName, Integer maximumScore, String applicantResponse, List<FormInputResource> assessmentFormInputs, Map<Long, AssessorFormInputResponseResource> assessorResponses) {
        this(daysLeft, daysLeftPercentage, competition, application, questionId, questionNumber, questionShortName, questionName, maximumScore, applicantResponse, assessmentFormInputs, assessorResponses, false, null);
    }

    public AssessmentFeedbackViewModel(long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, Long questionId, String questionNumber, String questionShortName, String questionName, Integer maximumScore, String applicantResponse, List<FormInputResource> assessmentFormInputs, Map<Long, AssessorFormInputResponseResource> assessorResponses, boolean appendixExists, FileDetailsViewModel appendixDetails) {
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
        this.assessorResponses = assessorResponses;
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

    public Map<Long, AssessorFormInputResponseResource> getAssessorResponses() {
        return assessorResponses;
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

    public Integer getWordsRemaining(Long formInputId) {
        Optional<FormInputResource> formInput = simpleFindFirst(assessmentFormInputs, assessmentFormInput -> formInputId.equals(assessmentFormInput.getId()));
        Optional<AssessorFormInputResponseResource> response = ofNullable(assessorResponses.get(formInputId));

        if (!(formInput.isPresent() && response.isPresent())) {
            return null;
        }

        Integer maxWordCount = formInput.get().getWordCount();
        String value = response.get().getValue();
        if (maxWordCount == null || value == null) {
            return null;
        }

        // clean any HTML markup from the value
        Document doc = Jsoup.parse(value);
        String cleaned = doc.text();

        int valueLength = cleaned.split("\\s+").length;
        int wordsRemaining = maxWordCount - valueLength;

        return max(0, wordsRemaining);
    }
}
