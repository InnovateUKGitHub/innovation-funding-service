package org.innovateuk.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for sections displayed within the Assessment Overview view.
 */
public class AssessmentOverviewSectionViewModel {

    private long id;
    private String name;
    private String guidance;
    private List<AssessmentOverviewQuestionViewModel> questions;
    private boolean finance;

    public AssessmentOverviewSectionViewModel(long id, String name, String guidance, List<AssessmentOverviewQuestionViewModel> questions, boolean finance) {
        this.id = id;
        this.name = name;
        this.guidance = guidance;
        this.questions = questions;
        this.finance = finance;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentOverviewSectionViewModel that = (AssessmentOverviewSectionViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
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
                .toHashCode();
    }
}