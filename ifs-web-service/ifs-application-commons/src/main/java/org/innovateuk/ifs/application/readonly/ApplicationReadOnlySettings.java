package org.innovateuk.ifs.application.readonly;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ApplicationReadOnlySettings {

    private boolean includeStatuses = false;
    private boolean includeQuestionLinks = false;
    private Long assessmentId = null;

    private ApplicationReadOnlySettings() {}

    public static ApplicationReadOnlySettings defaultSettings() {
        return new ApplicationReadOnlySettings();
    }

    public boolean isIncludeStatuses() {
        return includeStatuses;
    }

    public ApplicationReadOnlySettings setIncludeStatuses(boolean includeStatuses) {
        this.includeStatuses = includeStatuses;
        return this;
    }

    public boolean isIncludeQuestionLinks() {
        return includeQuestionLinks;
    }

    public ApplicationReadOnlySettings setIncludeQuestionLinks(boolean includeQuestionLinks) {
        this.includeQuestionLinks = includeQuestionLinks;
        return this;
    }

    public boolean isIncludeAssessment() {
        return assessmentId != null;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public ApplicationReadOnlySettings setAssessmentId(Long assesmentId) {
        this.assessmentId = assesmentId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationReadOnlySettings that = (ApplicationReadOnlySettings) o;

        return new EqualsBuilder()
                .append(includeStatuses, that.includeStatuses)
                .append(includeQuestionLinks, that.includeQuestionLinks)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(includeStatuses)
                .append(includeQuestionLinks)
                .toHashCode();
    }
}
