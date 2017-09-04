package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;

import java.util.List;

/**
 * View model for Competition Management Unsuccessful Applications page
 */
public class UnsuccessfulApplicationsViewModel {

    private Long competitionId;
    private String competitionName;
    private List<ApplicationResource> unsuccessfulApplications;
    private int unsuccessfulApplicationsSize;

    public UnsuccessfulApplicationsViewModel(Long competitionId, String competitionName, List<ApplicationResource> unsuccessfulApplications, int unsuccessfulApplicationsSize) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.unsuccessfulApplications = unsuccessfulApplications;
        this.unsuccessfulApplicationsSize = unsuccessfulApplicationsSize;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }


    public List<ApplicationResource> getUnsuccessfulApplications() {
        return unsuccessfulApplications;
    }

    public void setUnsuccessfulApplications(List<ApplicationResource> unsuccessfulApplications) {
        this.unsuccessfulApplications = unsuccessfulApplications;
    }

    public int getUnsuccessfulApplicationsSize() {
        return unsuccessfulApplicationsSize;
    }

    public void setUnsuccessfulApplicationsSize(int unsuccessfulApplicationsSize) {
        this.unsuccessfulApplicationsSize = unsuccessfulApplicationsSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UnsuccessfulApplicationsViewModel that = (UnsuccessfulApplicationsViewModel) o;

        return new EqualsBuilder()
                .append(unsuccessfulApplicationsSize, that.unsuccessfulApplicationsSize)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(unsuccessfulApplications, that.unsuccessfulApplications)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(unsuccessfulApplications)
                .append(unsuccessfulApplicationsSize)
                .toHashCode();
    }
}
