package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model for a single row of the Competition All Applications paginated table.
 */
public class AllApplicationsRowViewModel {

    private long applicationNumber;
    private String projectTitle;
    private String lead;
    private String innovationArea;
    private String status;
    private int percentageComplete;

    public AllApplicationsRowViewModel(long applicationNumber,
                                       String projectTitle,
                                       String lead,
                                       String innovationArea,
                                       String status,
                                       int percentageComplete) {
        this.applicationNumber = applicationNumber;
        this.projectTitle = projectTitle;
        this.lead = lead;
        this.innovationArea = innovationArea;
        this.status = status;
        this.percentageComplete = percentageComplete;
    }

    public long getApplicationNumber() {
        return applicationNumber;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getLead() {
        return lead;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public String getStatus() {
        return status;
    }

    public int getPercentageComplete() {
        return percentageComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AllApplicationsRowViewModel that = (AllApplicationsRowViewModel) o;

        return new EqualsBuilder()
                .append(applicationNumber, that.applicationNumber)
                .append(percentageComplete, that.percentageComplete)
                .append(projectTitle, that.projectTitle)
                .append(lead, that.lead)
                .append(innovationArea, that.innovationArea)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationNumber)
                .append(projectTitle)
                .append(lead)
                .append(innovationArea)
                .append(status)
                .append(percentageComplete)
                .toHashCode();
    }
}
