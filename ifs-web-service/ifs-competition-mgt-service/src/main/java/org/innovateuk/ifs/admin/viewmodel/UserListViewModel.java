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

    private List<UserResource> pendingUsers;

    private long activeCount;

    private long inactiveCount;

    private long pendingCount;

    private PaginationViewModel activeUsersPagination;

    private PaginationViewModel inactiveUsersPagination;

    private PaginationViewModel pendingUsersPagination;

    public UserListViewModel(String tab, List<UserResource> activeUsers, List<UserResource> inactiveUsers, List<UserResource> pendingUsers,
                             long activeCount, long inactiveCount, long pendingCount,
                             PaginationViewModel activeUsersPagination, PaginationViewModel inactiveUsersPagination, PaginationViewModel pendingUsersPagination) {
        this.tab = tab;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.pendingUsers = pendingUsers;
        this.activeCount = activeCount;
        this.inactiveCount = inactiveCount;
        this.pendingCount = pendingCount;
        this.activeUsersPagination = activeUsersPagination;
        this.inactiveUsersPagination = inactiveUsersPagination;
        this.pendingUsersPagination = pendingUsersPagination;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public long getInactiveCount() {
        return inactiveCount;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public List<UserResource> getActiveUsers() {
        return activeUsers;
    }

    public List<UserResource> getInactiveUsers() {
        return inactiveUsers;
    }

    public List<UserResource> getPendingUsers() {
        return pendingUsers;
    }

    public PaginationViewModel getActiveUsersPagination() {
        return activeUsersPagination;
    }

    public PaginationViewModel getInactiveUsersPagination() {
        return inactiveUsersPagination;
    }

    public PaginationViewModel getPendingUsersPagination() {
        return pendingUsersPagination;
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
                .append(pendingCount, that.pendingCount)
                .append(tab, that.tab)
                .append(activeUsers, that.activeUsers)
                .append(inactiveUsers, that.inactiveUsers)
                .append(pendingUsers, that.pendingUsers)
                .append(activeUsersPagination, that.activeUsersPagination)
                .append(inactiveUsersPagination, that.inactiveUsersPagination)
                .append(pendingUsersPagination, that.pendingUsersPagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tab)
                .append(activeUsers)
                .append(inactiveUsers)
                .append(pendingUsers)
                .append(activeCount)
                .append(inactiveCount)
                .append(pendingCount)
                .append(activeUsersPagination)
                .append(inactiveUsersPagination)
                .append(pendingUsersPagination)
                .toHashCode();
    }
}
