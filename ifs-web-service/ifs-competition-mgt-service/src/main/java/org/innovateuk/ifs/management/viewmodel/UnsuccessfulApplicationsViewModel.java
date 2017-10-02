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
    private long unsuccessfulApplicationsSize;
    private PaginationViewModel unsuccessfulApplicationsPagination;

    public UnsuccessfulApplicationsViewModel(Long competitionId, String competitionName,
                                             List<ApplicationResource> unsuccessfulApplications,
                                             long unsuccessfulApplicationsSize,
                                             PaginationViewModel unsuccessfulApplicationsPagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.unsuccessfulApplications = unsuccessfulApplications;
        this.unsuccessfulApplicationsSize = unsuccessfulApplicationsSize;
        this.unsuccessfulApplicationsPagination = unsuccessfulApplicationsPagination;
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

    public long getUnsuccessfulApplicationsSize() {
        return unsuccessfulApplicationsSize;
    }

    public void setUnsuccessfulApplicationsSize(long unsuccessfulApplicationsSize) {
        this.unsuccessfulApplicationsSize = unsuccessfulApplicationsSize;
    }

    public PaginationViewModel getUnsuccessfulApplicationsPagination() {
        return unsuccessfulApplicationsPagination;
    }

    public void setUnsuccessfulApplicationsPagination(PaginationViewModel unsuccessfulApplicationsPagination) {
        this.unsuccessfulApplicationsPagination = unsuccessfulApplicationsPagination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UnsuccessfulApplicationsViewModel viewModel = (UnsuccessfulApplicationsViewModel) o;

        return new EqualsBuilder()
                .append(unsuccessfulApplicationsSize, viewModel.unsuccessfulApplicationsSize)
                .append(competitionId, viewModel.competitionId)
                .append(competitionName, viewModel.competitionName)
                .append(unsuccessfulApplications, viewModel.unsuccessfulApplications)
                .append(unsuccessfulApplicationsPagination, viewModel.unsuccessfulApplicationsPagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(unsuccessfulApplications)
                .append(unsuccessfulApplicationsSize)
                .append(unsuccessfulApplicationsPagination)
                .toHashCode();
    }
}
