package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an application's statistics
 */
public class ApplicationCountSummaryResource extends AssessmentCountSummaryResource {
    private String name;
    private String leadOrganisation;
    private long assessors; // TODO: rename to assigned (or similar) to be consistent with AssessmentCountSummaryResource - IFS-3757
    private long accepted;
    private long submitted;

    public enum Sort {
        APPLICATION_NUMBER("Application number"),
        TITLE("Title"),
        LEAD_ORGANISATION("Lead organisation"),
        ASSESSORS("Assessors"),
        ACCEPTED("Accepted", true),
        SUBMITTED("Submitted", true);

        Sort(String columnName) {
            this(columnName, false);
        }

        Sort(String columnName, boolean onlyInAssessment) {
            this.columnName = columnName;
            this.onlyInAssessment = onlyInAssessment;
        }

        private String columnName;
        private boolean onlyInAssessment;

        public String getColumnName() {
            return columnName;
        }

        public boolean isOnlyInAssessment() {
            return onlyInAssessment;
        }
    }

    public ApplicationCountSummaryResource() { }

    public ApplicationCountSummaryResource(Long id, String name, String leadOrganisation, long assessors, long accepted, long submitted) {
        super(id);
        this.name = name;
        this.leadOrganisation = leadOrganisation;
        this.assessors = assessors;
        this.accepted = accepted;
        this.submitted = submitted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public long getAssessors() {
        return assessors;
    }

    public void setAssessors(long assessors) {
        this.assessors = assessors;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationCountSummaryResource that = (ApplicationCountSummaryResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(assessors, that.assessors)
                .append(accepted, that.accepted)
                .append(submitted, that.submitted)
                .append(name, that.name)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(name)
                .append(leadOrganisation)
                .append(assessors)
                .append(accepted)
                .append(submitted)
                .toHashCode();
    }
}
