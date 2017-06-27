package org.innovateuk.ifs.admin.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class UserListViewModel {

    private String tab;

    private List<UserResource> activeUsers;

    private List<UserResource> inactiveUsers;

    private long activeCount;

    private long inactiveCount;

    private PaginationViewModel activeUsersPagination;

    private PaginationViewModel inactiveUsersPagination;

    public UserListViewModel(String tab, List<UserResource> activeUsers, List<UserResource> inactiveUsers, long activeCount, long inactiveCount, PaginationViewModel activeUsersPagination, PaginationViewModel inactiveUsersPagination) {
        this.tab = tab;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.activeCount = activeCount;
        this.inactiveCount = inactiveCount;
        this.activeUsersPagination = activeUsersPagination;
        this.inactiveUsersPagination = inactiveUsersPagination;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public long getInactiveCount() {
        return inactiveCount;
    }

    public List<UserResource> getActiveUsers() {
        return activeUsers;
    }

    public List<UserResource> getInactiveUsers() {
        return inactiveUsers;
    }

    public PaginationViewModel getActiveUsersPagination() {
        return activeUsersPagination;
    }

    public PaginationViewModel getInactiveUsersPagination() {
        return inactiveUsersPagination;
    }

    public String getTab() {
        return tab;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserListViewModel that = (UserListViewModel) o;

        return new EqualsBuilder()
                .append(activeCount, that.activeCount)
                .append(inactiveCount, that.inactiveCount)
                .append(tab, that.tab)
                .append(activeUsers, that.activeUsers)
                .append(inactiveUsers, that.inactiveUsers)
                .append(activeUsersPagination, that.activeUsersPagination)
                .append(inactiveUsersPagination, that.inactiveUsersPagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tab)
                .append(activeUsers)
                .append(inactiveUsers)
                .append(activeCount)
                .append(inactiveCount)
                .append(activeUsersPagination)
                .append(inactiveUsersPagination)
                .toHashCode();
    }
}
