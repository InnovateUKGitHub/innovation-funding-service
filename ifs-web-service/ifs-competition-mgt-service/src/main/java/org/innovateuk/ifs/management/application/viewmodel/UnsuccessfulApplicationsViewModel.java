package org.innovateuk.ifs.management.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * View model for Competition Management Unsuccessful Applications page
 */
public class UnsuccessfulApplicationsViewModel {

    private Long competitionId;
    private String competitionName;
    private List<ApplicationResource> unsuccessfulApplications;
    private long unsuccessfulApplicationsSize;
    private Pagination unsuccessfulApplicationsPagination;
    private boolean isIfsAdmin;

    public UnsuccessfulApplicationsViewModel(Long competitionId, String competitionName, boolean isIfsAdmin,
                                             List<ApplicationResource> unsuccessfulApplications,
                                             long unsuccessfulApplicationsSize,
                                             Pagination unsuccessfulApplicationsPagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.isIfsAdmin = isIfsAdmin;
        this.unsuccessfulApplications = unsuccessfulApplications;
        this.unsuccessfulApplicationsSize = unsuccessfulApplicationsSize;
        this.unsuccessfulApplicationsPagination = unsuccessfulApplicationsPagination;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isIfsAdmin() { return isIfsAdmin; }

    public List<ApplicationResource> getUnsuccessfulApplications() {
        return unsuccessfulApplications;
    }

    public long getUnsuccessfulApplicationsSize() {
        return unsuccessfulApplicationsSize;
    }

    public Pagination getUnsuccessfulApplicationsPagination() {
        return unsuccessfulApplicationsPagination;
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
                .append(isIfsAdmin, viewModel.isIfsAdmin)
                .append(unsuccessfulApplications, viewModel.unsuccessfulApplications)
                .append(unsuccessfulApplicationsPagination, viewModel.unsuccessfulApplicationsPagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(isIfsAdmin)
                .append(unsuccessfulApplications)
                .append(unsuccessfulApplicationsSize)
                .append(unsuccessfulApplicationsPagination)
                .toHashCode();
    }
}
