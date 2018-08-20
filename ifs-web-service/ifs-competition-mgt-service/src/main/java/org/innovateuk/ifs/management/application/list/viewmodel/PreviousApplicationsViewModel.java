package org.innovateuk.ifs.management.application.list.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * View model for Competition Management Previous Applications page
 */
public class PreviousApplicationsViewModel {

    private Long competitionId;
    private String competitionName;
    private List<ApplicationResource> previousApplications;
    private long previousApplicationsSize;
    private Pagination previousApplicationsPagination;
    private boolean isIfsAdmin;

    public PreviousApplicationsViewModel(Long competitionId, String competitionName, boolean isIfsAdmin,
                                         List<ApplicationResource> previousApplications,
                                         long previousApplicationsSize,
                                         Pagination previousApplicationsPagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.isIfsAdmin = isIfsAdmin;
        this.previousApplications = previousApplications;
        this.previousApplicationsSize = previousApplicationsSize;
        this.previousApplicationsPagination = previousApplicationsPagination;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isIfsAdmin() { return isIfsAdmin; }

    public List<ApplicationResource> getPreviousApplications() {
        return previousApplications;
    }

    public long getPreviousApplicationsSize() {
        return previousApplicationsSize;
    }

    public Pagination getPreviousApplicationsPagination() {
        return previousApplicationsPagination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PreviousApplicationsViewModel viewModel = (PreviousApplicationsViewModel) o;

        return new EqualsBuilder()
                .append(previousApplicationsSize, viewModel.previousApplicationsSize)
                .append(competitionId, viewModel.competitionId)
                .append(competitionName, viewModel.competitionName)
                .append(isIfsAdmin, viewModel.isIfsAdmin)
                .append(previousApplications, viewModel.previousApplications)
                .append(previousApplicationsPagination, viewModel.previousApplicationsPagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(isIfsAdmin)
                .append(previousApplications)
                .append(previousApplicationsSize)
                .append(previousApplicationsPagination)
                .toHashCode();
    }
}
