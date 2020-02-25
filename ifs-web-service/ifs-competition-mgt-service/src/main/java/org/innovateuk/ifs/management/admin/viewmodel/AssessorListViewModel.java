package org.innovateuk.ifs.management.admin.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * A view model for serving page listing assessors to be managed
 */
public class AssessorListViewModel {

    private String tab;

    private List<UserResource> availableAssessors;

    private List<UserResource> unavailableAssessors;

    private List<UserResource> disabledAssessors;

    private long availableCount;

    private long unavailableCount;

    private long disabledCount;

    private PaginationViewModel availableAssessorsPagination;

    private PaginationViewModel unavailableAssessorsPagination;

    private PaginationViewModel disabledAssessorsPagination;


    public AssessorListViewModel(String tab,
                                 String filter,
                                 List<UserResource> availableAssessors,
                                 List<UserResource> unavailableAssessors,
                                 List<UserResource> disabledAssessors,
                                 long availableCount,
                                 long unavailableCount,
                                 long disabledCount,
                                 PaginationViewModel availableAssessorsPagination,
                                 PaginationViewModel unavailableAssessorsPagination,
                                 PaginationViewModel disabledAssessorsPagination) {
        this.tab = tab;
        this.availableAssessors = availableAssessors;
        this.unavailableAssessors = unavailableAssessors;
        this.disabledAssessors = disabledAssessors;
        this.availableCount = availableCount;
        this.unavailableCount = unavailableCount;
        this.disabledCount = disabledCount;
        this.availableAssessorsPagination = availableAssessorsPagination;
        this.unavailableAssessorsPagination = unavailableAssessorsPagination;
        this.disabledAssessorsPagination = disabledAssessorsPagination;
    }

    public long getAvailableCount() {
        return availableCount;
    }

    public long getUnavailableCount() {
        return unavailableCount;
    }

    public long getDisabledCount() {
        return disabledCount;
    }

    public List<UserResource> getAvailableAssessors() {
        return availableAssessors;
    }

    public List<UserResource> getUnavailableAssessors() {
        return unavailableAssessors;
    }

    public List<UserResource> getDisabledAssessors() {
        return disabledAssessors;
    }

    public PaginationViewModel getAvailableAssessorsPagination() {
        return availableAssessorsPagination;
    }

    public PaginationViewModel getUnavailableAssessorsPagination() {
        return unavailableAssessorsPagination;
    }

    public PaginationViewModel getDisabledAssessorsPagination() {
        return disabledAssessorsPagination;
    }

    public String getTab() {
        return tab;
    }

    public long getTotalCount() {
        return getAvailableCount() + getUnavailableCount() + getDisabledCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorListViewModel that = (AssessorListViewModel) o;

        return new EqualsBuilder()
                .append(availableCount, that.availableCount)
                .append(unavailableCount, that.unavailableCount)
                .append(disabledCount, that.disabledCount)
                .append(tab, that.tab)
                .append(availableAssessors, that.availableAssessors)
                .append(unavailableAssessors, that.unavailableAssessors)
                .append(disabledAssessors, that.disabledAssessors)
                .append(availableAssessorsPagination, that.availableAssessorsPagination)
                .append(unavailableAssessorsPagination, that.unavailableAssessorsPagination)
                .append(disabledAssessorsPagination, that.disabledAssessorsPagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tab)
                .append(availableAssessors)
                .append(unavailableAssessors)
                .append(disabledAssessors)
                .append(availableCount)
                .append(unavailableCount)
                .append(disabledCount)
                .append(availableAssessorsPagination)
                .append(unavailableAssessorsPagination)
                .append(disabledAssessorsPagination)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("tab", tab)
                .append("availableAssessors", availableAssessors)
                .append("unavailableAssessors", unavailableAssessors)
                .append("disabledAssessors", disabledAssessors)
                .append("availableCount", availableCount)
                .append("unavailableCount", unavailableCount)
                .append("disabledCount", disabledCount)
                .append("availableAssessorsPagination", availableAssessorsPagination)
                .append("unavailableAssessorsPagination", unavailableAssessorsPagination)
                .append("disabledAssessorsPagination", disabledAssessorsPagination)
                .toString();
    }
}