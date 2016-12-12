package org.innovateuk.ifs.assessment.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Holder of model attributes for response information displayed on the Assessment Overview shown as part of the assessment journey.
 */
public class AssessmentOverviewRowViewModel {

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
                if (ASSESSOR_APPLICATION_IN_SCOPE == input.getType()) {
                    this.hasScope = true;
                    if (!isBlank(formInputResponseMap.get(input.getId()).getValue())) {
                        this.scopeResponse = formInputResponseMap.get(input.getId()).getValue().equals(TRUE) ? YES : NO;
                    }
                } else if (ASSESSOR_SCORE == input.getType()) {
                    this.assessedScore = formInputResponseMap.get(input.getId()).getValue();
                }
            }
        });

        this.hasBeenCompleted = true;
        formInputs.forEach(input -> {
            if (formInputResponseMap.get(input.getId()) == null ||
                    isBlank(formInputResponseMap.get(input.getId()).getValue())) {
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

    public String getScopeResponse() {
        return scopeResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentOverviewRowViewModel that = (AssessmentOverviewRowViewModel) o;

        return new EqualsBuilder()
                .append(hasInput, that.hasInput)
                .append(hasScope, that.hasScope)
                .append(hasBeenCompleted, that.hasBeenCompleted)
                .append(question, that.question)
                .append(assessedScore, that.assessedScore)
                .append(maximumScore, that.maximumScore)
                .append(scopeResponse, that.scopeResponse)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(question)
                .append(hasInput)
                .append(hasScope)
                .append(hasBeenCompleted)
                .append(assessedScore)
                .append(maximumScore)
                .append(scopeResponse)
                .toHashCode();
    }
}
