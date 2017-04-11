package org.innovateuk.ifs.assessment.feedback.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * Holder of model attributes for the navigation during the assessment journey.
 */
public class AssessmentNavigationViewModel {

    private final Long assessmentId;
    private final Optional<QuestionResource> previousQuestion;
    private final Optional<QuestionResource> nextQuestion;

    public AssessmentNavigationViewModel(Long assessmentId, Optional<QuestionResource> previousQuestion, Optional<QuestionResource> nextQuestion) {
        this.assessmentId = assessmentId;
        this.previousQuestion = previousQuestion;
        this.nextQuestion = nextQuestion;
    }

    public String getPreviousUrl() {
        return previousQuestion.map(this::getNavigationUrl).orElse(StringUtils.EMPTY);
    }

    public String getPreviousText() {
        return previousQuestion.flatMap(question -> ofNullable(question.getShortName())).orElse(StringUtils.EMPTY);
    }

    public String getNextUrl() {
        return nextQuestion.map(this::getNavigationUrl).orElse(StringUtils.EMPTY);
    }

    public String getNextText() {
        return nextQuestion.flatMap(question -> ofNullable(question.getShortName())).orElse(StringUtils.EMPTY);
    }

    public String getOverviewUrl() {
        return format("/%s", assessmentId);
    }

    private String getNavigationUrl(final QuestionResource question) {
        return format("/%s/question/%s", assessmentId, question.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentNavigationViewModel that = (AssessmentNavigationViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(previousQuestion, that.previousQuestion)
                .append(nextQuestion, that.nextQuestion)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(previousQuestion)
                .append(nextQuestion)
                .toHashCode();
    }
}
