package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Holder of model attributes for sections displayed within the Assessment Overview view.
 */
public class AssessmentOverviewSectionViewModel {

    private final long id;
    private final String name;
    private final String guidance;
    private final List<AssessmentOverviewQuestionViewModel> questions;
    private final boolean finance;
    private final boolean termsAndConditions;

    public AssessmentOverviewSectionViewModel(long id,
                                              String name,
                                              String guidance,
                                              List<AssessmentOverviewQuestionViewModel> questions,
                                              boolean finance,
                                              boolean termsAndConditions ) {
        this.id = id;
        this.name = name;
        this.guidance = guidance;
        this.questions = questions;
        this.finance = finance;
        this.termsAndConditions = termsAndConditions;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGuidance() {
        return guidance;
    }

    public List<AssessmentOverviewQuestionViewModel> getQuestions() {
        return questions;
    }

    public boolean isFinance() {
        return finance;
    }

    public boolean isTermsAndConditions() {
        return termsAndConditions;
    }

    public boolean hasAnyScoredQuestions() {
        return questions.stream().anyMatch(AssessmentOverviewQuestionViewModel::isScoreRequired);
    }

    public Integer getScore() {
        return questions.stream()
                .map(AssessmentOverviewQuestionViewModel::getScoreResponse)
                .filter(Objects::nonNull)
                .map(Integer::valueOf)
                .reduce(0, Integer::sum);
    }

    public Integer getMaximumScore() {
        return questions.stream()
                .filter(AssessmentOverviewQuestionViewModel::isScoreRequired)
                .map(AssessmentOverviewQuestionViewModel::getMaximumScore)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    public Integer getScorePercentage() {
        if (!getMaximumScore().equals(0)) {
            return BigDecimal.valueOf(getScore())
                    .divide(BigDecimal.valueOf(getMaximumScore()), 0, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentOverviewSectionViewModel that = (AssessmentOverviewSectionViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(finance, that.finance)
                .append(termsAndConditions, that.termsAndConditions)
                .append(name, that.name)
                .append(guidance, that.guidance)
                .append(questions, that.questions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(guidance)
                .append(questions)
                .append(finance)
                .append(termsAndConditions)
                .toHashCode();
    }
}