package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource holding a list of assessment feedback comments.
 */
public class ApplicationAssessmentFeedbackResource {

    private List<String> feedback = new ArrayList<>();

    public ApplicationAssessmentFeedbackResource() {}

    public ApplicationAssessmentFeedbackResource(List<String> feedback) {
        this.feedback = feedback;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<String> feedback) {
        this.feedback = feedback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentFeedbackResource that = (ApplicationAssessmentFeedbackResource) o;

        return new EqualsBuilder()
                .append(feedback, that.feedback)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(feedback)
                .toHashCode();
    }
}
