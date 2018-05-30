package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.core.viewmodel.PaginationViewModel;

import java.util.List;

/**
 * A view model for displaying the application search results when searched on the dashboard by the support user.
 */
public class ApplicationSearchDashboardViewModel {

    private List<ApplicationResource> applications;

    private long applicationCount;

    private PaginationViewModel applicationPagination;

    private String searchString;

    public ApplicationSearchDashboardViewModel(List<ApplicationResource> applications, long applicationCount, PaginationViewModel applicationPagination, String searchString) {
        this.applications = applications;
        this.applicationCount = applicationCount;
        this.applicationPagination = applicationPagination;
        this.searchString = searchString;
    }

    public List<ApplicationResource> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationResource> applications) {
        this.applications = applications;
    }

    public long getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(long applicationCount) {
        this.applicationCount = applicationCount;
    }

    public PaginationViewModel getApplicationPagination() {
        return applicationPagination;
    }

    public void setApplicationPagination(PaginationViewModel applicationPagination) {
        this.applicationPagination = applicationPagination;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean isSearchStringPresent() {
        return StringUtils.isNotBlank(searchString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationSearchDashboardViewModel viewModel = (ApplicationSearchDashboardViewModel) o;

        return new EqualsBuilder()
                .append(applicationCount, viewModel.applicationCount)
                .append(applications, viewModel.applications)
                .append(applicationPagination, viewModel.applicationPagination)
                .append(searchString, viewModel.searchString)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applications)
                .append(applicationCount)
                .append(applicationPagination)
                .append(searchString)
                .toHashCode();
    }
}

