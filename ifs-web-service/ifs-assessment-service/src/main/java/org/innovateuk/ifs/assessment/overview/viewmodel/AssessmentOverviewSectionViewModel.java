package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

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