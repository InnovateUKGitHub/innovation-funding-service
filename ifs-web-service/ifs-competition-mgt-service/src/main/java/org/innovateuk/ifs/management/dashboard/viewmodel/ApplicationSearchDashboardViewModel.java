package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * A view model for displaying the application search results when searched on the dashboard by the support user.
 */
public class ApplicationSearchDashboardViewModel {

    private final List<ApplicationResource> applications;

    private final long applicationCount;

    private final Pagination applicationPagination;

    private final String searchString;

    private final boolean isSupport;

    public ApplicationSearchDashboardViewModel(List<ApplicationResource> applications, long applicationCount, Pagination applicationPagination, String searchString, boolean isSupport) {
        this.applications = applications;
        this.applicationCount = applicationCount;
        this.applicationPagination = applicationPagination;
        this.searchString = searchString;
        this.isSupport = isSupport;
    }

    public List<ApplicationResource> getApplications() {
        return applications;
    }

    public long getApplicationCount() {
        return applicationCount;
    }

    public Pagination getApplicationPagination() {
        return applicationPagination;
    }

    public String getSearchString() {
        return searchString;
    }


    public boolean isSearchStringPresent() {
        return StringUtils.isNotBlank(searchString);
    }

    public boolean isSupport() {
        return isSupport;
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

