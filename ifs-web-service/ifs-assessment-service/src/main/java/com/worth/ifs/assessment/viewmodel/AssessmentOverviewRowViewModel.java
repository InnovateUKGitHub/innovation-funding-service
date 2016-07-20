package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.util.CollectionFunctions;

import java.util.List;
import java.util.Map;

public class AssessmentOverviewRowViewModel {

    private static final String SCOPE_INPUT_TYPE = "assessor_application_in_scope";
    private static final String SCORE_INPUT_TYPE = "assessor_score";

    private Long question;
    private boolean hasInput;
    private boolean hasScope;
    private boolean hasBeenCompleted;

    private String assessedScore;
    private String maximumScore;


    public AssessmentOverviewRowViewModel(QuestionResource question, List<FormInputResource> formInputs, List<AssessorFormInputResponseResource> allAssessorFormInputResponses) {
        this.question = question.getId();

        if (formInputs.isEmpty()) {
            this.hasInput = false;
        } else {
            setInputFields(allAssessorFormInputResponses, question, formInputs);
        }
    }

    private void setInputFields(List<AssessorFormInputResponseResource> allAssessorFormInputResponses, QuestionResource question, List<FormInputResource> formInputs) {
        Map<Long, AssessorFormInputResponseResource> formInputResponseMap = CollectionFunctions.simpleToMap(
                                                                                allAssessorFormInputResponses,
                                                                                AssessorFormInputResponseResource :: getFormInput);

        formInputs.forEach(input -> {
            if (input.getFormInputTypeTitle().equals(SCOPE_INPUT_TYPE)) {
                this.hasScope = true;
                if (formInputResponseMap.get(input.getId()) != null) {
                    this.assessedScore = formInputResponseMap.get(input.getId()).getValue();
                }
            } else if (input.getFormInputTypeTitle().equals(SCORE_INPUT_TYPE)) {
                this.maximumScore = String.valueOf(question.getAssessorMaximumScore());
                if (formInputResponseMap.get(input.getId()) != null) {
                    this.assessedScore = formInputResponseMap.get(input.getId()).getValue();
                }
            }
        });

        formInputs.forEach(input -> {
            if (formInputResponseMap.get(input.getId()) != null &&
                formInputResponseMap.get(input.getId()).getValue().isEmpty()) {
                this.hasBeenCompleted = false;
            }
        });
    }

    public Long getQuestion() {
        return question;
    }

    public boolean isHasInput() {
        return hasInput;
    }

    public boolean isHasScope() {
        return hasScope;
    }

    public boolean isHasBeenCompleted() {
        return hasBeenCompleted;
    }

    public String getAssessedScore() {
        return assessedScore;
    }

    public String getMaximumScore() {
        return maximumScore;
    }

    public String getScopeAnswer() {
        if (this.hasBeenCompleted) {
            return this.assessedScore.equals("1") ? "Yes" : "No";
        } else {
            return "";
        }
    }
}
