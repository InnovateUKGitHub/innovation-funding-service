package org.innovateuk.ifs.assessment.summary.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * Holder of model attributes for the questions shown as part of the Assessment Summary.
 */
public class AssessmentSummaryQuestionViewModel {

    private final Long questionId;
    private final String displayLabel;
    private final String displayLabelShort;
    private final boolean scoreFormInputExists;
    private final Integer scoreGiven;
    private final Integer scorePossible;
    private final String feedback;
    private final Boolean applicationInScope;
    private final boolean complete;

    public AssessmentSummaryQuestionViewModel(final QuestionResource question,
                                              final List<FormInputResource> formInputsForQuestion,
                                              final List<AssessorFormInputResponseResource> responsesForQuestion) {
        requireNonNull(question, "question cannot be null");
        requireNonNull(formInputsForQuestion, "formInputsForQuestion cannot be null");
        requireNonNull(responsesForQuestion, "responsesForQuestion cannot be null");

        this.questionId = question.getId();
        this.displayLabel = getQuestionDisplayLabel(question);
        this.displayLabelShort = getQuestionDisplayLabelShort(question);
        this.scoreFormInputExists = formInputsForQuestion.stream().anyMatch(formInput -> ASSESSOR_SCORE == formInput.getType());

        this.scorePossible = scoreFormInputExists ? question.getAssessorMaximumScore() : null;

        this.complete = isComplete(formInputsForQuestion, responsesForQuestion);

        final Map<FormInputType, String> responsesByFieldType = getAssessorResponsesByFormInputType(formInputsForQuestion, responsesForQuestion);
        this.scoreGiven = ofNullable(responsesByFieldType.get(ASSESSOR_SCORE)).map(Integer::valueOf).orElse(null);
        this.feedback = responsesByFieldType.get(TEXTAREA);

        this.applicationInScope = ofNullable(responsesByFieldType.get(ASSESSOR_APPLICATION_IN_SCOPE)).map(Boolean::valueOf).orElse(null);
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getDisplayLabelShort() {
        return displayLabelShort;
    }

    public boolean isScoreFormInputExists() {
        return scoreFormInputExists;
    }

    public Integer getScoreGiven() {
        return scoreGiven;
    }

    public Integer getScorePossible() {
        return scorePossible;
    }

    public String getFeedback() {
        return feedback;
    }

    public Boolean getApplicationInScope() {
        return applicationInScope;
    }

    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentSummaryQuestionViewModel that = (AssessmentSummaryQuestionViewModel) o;

        return new EqualsBuilder()
                .append(scoreFormInputExists, that.scoreFormInputExists)
                .append(complete, that.complete)
                .append(questionId, that.questionId)
                .append(displayLabel, that.displayLabel)
                .append(displayLabelShort, that.displayLabelShort)
                .append(scoreGiven, that.scoreGiven)
                .append(scorePossible, that.scorePossible)
                .append(feedback, that.feedback)
                .append(applicationInScope, that.applicationInScope)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(questionId)
                .append(displayLabel)
                .append(displayLabelShort)
                .append(scoreFormInputExists)
                .append(scoreGiven)
                .append(scorePossible)
                .append(feedback)
                .append(applicationInScope)
                .append(complete)
                .toHashCode();
    }

    private static String getQuestionDisplayLabel(QuestionResource question) {
        return ofNullable(question.getQuestionNumber()).map(questionNumber -> format("%s. %s", questionNumber, question.getShortName())).orElse(question.getShortName());
    }

    private static String getQuestionDisplayLabelShort(QuestionResource question) {
        return ofNullable(question.getQuestionNumber()).map(questionNumber -> format("Q%s", questionNumber)).orElse(StringUtils.EMPTY);
    }

    private static boolean isComplete(List<FormInputResource> formInputs, List<AssessorFormInputResponseResource> responses) {
        Map<Long, AssessorFormInputResponseResource> responsesMap = simpleToMap(responses, AssessorFormInputResponseResource::getFormInput);
        return formInputs.stream().allMatch(formInput ->
                ofNullable(responsesMap.get(formInput.getId())).map(response -> StringUtils.isNotBlank(response.getValue())).orElse(false));
    }

    private static Map<FormInputType, String> getAssessorResponsesByFormInputType(List<FormInputResource> formInputs, List<AssessorFormInputResponseResource> responses) {
        Map<Long, FormInputResource> formInputsMap = simpleToMap(formInputs, FormInputResource::getId);
        return simpleToMap(simpleFilter(responses, response -> response.getValue() != null), response -> formInputsMap.get(response.getFormInput()).getType(), AssessorFormInputResponseResource::getValue);
    }
}
