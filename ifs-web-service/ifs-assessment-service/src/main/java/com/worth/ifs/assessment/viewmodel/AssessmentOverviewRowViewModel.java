package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.util.CollectionFunctions;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AssessmentOverviewRowViewModel {

    private static final String SCOPE_INPUT_TYPE = "assessor_application_in_scope";
    private static final String SCORE_INPUT_TYPE = "assessor_score";
    private static final String TRUE = "true";
    private static final String YES = "Yes";
    private static final String NO = "No";

    private Long question;
    private boolean hasInput;
    private boolean hasScope;
    private boolean hasBeenCompleted;

    private String assessedScore;
    private Integer maximumScore;
    private String scopeResponse;


    public AssessmentOverviewRowViewModel(QuestionResource question, List<FormInputResource> formInputs, List<AssessorFormInputResponseResource> allAssessorFormInputResponses) {
        this.question = question.getId();

        if (formInputs.isEmpty()) {
            this.hasInput = false;
        } else {
            this.hasInput = true;
            setInputFields(allAssessorFormInputResponses, question, formInputs);
        }
    }

    private void setInputFields(List<AssessorFormInputResponseResource> allAssessorFormInputResponses, QuestionResource question, List<FormInputResource> formInputs) {
        Map<Long, AssessorFormInputResponseResource> formInputResponseMap = CollectionFunctions.simpleToMap(
                                                                                allAssessorFormInputResponses,
                                                                                AssessorFormInputResponseResource :: getFormInput);
        this.maximumScore = question.getAssessorMaximumScore();

        formInputs.forEach(input -> {
            if (formInputResponseMap.get(input.getId()) != null) {
                if (input.getFormInputTypeTitle().equals(SCOPE_INPUT_TYPE)) {
                    this.hasScope = true;
                    if (!isBlank(formInputResponseMap.get(input.getId()).getValue())) {
                        this.scopeResponse = formInputResponseMap.get(input.getId()).getValue().equals(TRUE) ? YES : NO;
                    }
                } else if (input.getFormInputTypeTitle().equals(SCORE_INPUT_TYPE)) {
                     this.assessedScore = formInputResponseMap.get(input.getId()).getValue();
                }
            }
        });

        this.hasBeenCompleted = true;
        formInputs.forEach(input -> {
            if (formInputResponseMap.get(input.getId()) == null ||
                    isBlank(formInputResponseMap.get(input.getId()).getValue()) ) {
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

    public Integer getMaximumScore() {
        return maximumScore;
    }

    public String getScopeResponse() { return scopeResponse; }
}
