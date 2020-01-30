package org.innovateuk.ifs.management.admin.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.ManageUserResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class UserListViewModel {

    private String tab;

    private List<ManageUserResource> activeUsers;

    private List<ManageUserResource> inactiveUsers;

    private List<RoleInviteResource> pendingInvites;

    private long activeCount;

    private long inactiveCount;

    private long pendingCount;

    private PaginationViewModel activeUsersPagination;

    private PaginationViewModel inactiveUsersPagination;

    private PaginationViewModel pendingInvitesPagination;

    private boolean includeInternalUsers;

    public UserListViewModel(String tab,
                             String filter,
                             List<ManageUserResource> activeUsers,
                             List<ManageUserResource> inactiveUsers,
                             List<RoleInviteResource> pendingInvites,
                             long activeCount,
                             long inactiveCount,
                             long pendingCount,
                             PaginationViewModel activeUsersPagination,
                             PaginationViewModel inactiveUsersPagination,
                             PaginationViewModel pendingInvitesPagination,
                             boolean includeInternalUsers) {
        this.tab = tab;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.pendingInvites = pendingInvites;
        this.activeCount = activeCount;
        this.inactiveCount = inactiveCount;
        this.pendingCount = pendingCount;
        this.activeUsersPagination = activeUsersPagination;
        this.inactiveUsersPagination = inactiveUsersPagination;
        this.pendingInvitesPagination = pendingInvitesPagination;
        this.includeInternalUsers = includeInternalUsers;
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

    public List<ManageUserResource> getActiveUsers() {
        return activeUsers;
    }

    public List<ManageUserResource> getInactiveUsers() {
        return inactiveUsers;
    }

    public List<RoleInviteResource> getPendingInvites() {
        return pendingInvites;
    }

    public PaginationViewModel getActiveUsersPagination() {
        return activeUsersPagination;
    }

    public PaginationViewModel getInactiveUsersPagination() {
        return inactiveUsersPagination;
    }

    public PaginationViewModel getPendingInvitesPagination() {
        return pendingInvitesPagination;
    }

    public String getTab() {
        return tab;
    }

    public boolean isIncludeInternalUsers() {
        return includeInternalUsers;
    }

    public long getTotalCount() {
        return getActiveCount() + getInactiveCount() + getPendingCount();
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
                .append(pendingInvites, that.pendingInvites)
                .append(activeUsersPagination, that.activeUsersPagination)
                .append(inactiveUsersPagination, that.inactiveUsersPagination)
                .append(pendingInvitesPagination, that.pendingInvitesPagination)
                .append(includeInternalUsers, that.includeInternalUsers)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tab)
                .append(activeUsers)
                .append(inactiveUsers)
                .append(pendingInvites)
                .append(activeCount)
                .append(inactiveCount)
                .append(pendingCount)
                .append(activeUsersPagination)
                .append(inactiveUsersPagination)
                .append(pendingInvitesPagination)
                .append(includeInternalUsers)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("tab", tab)
                .append("activeUsers", activeUsers)
                .append("inactiveUsers", inactiveUsers)
                .append("pendingInvites", pendingInvites)
                .append("activeCount", activeCount)
                .append("inactiveCount", inactiveCount)
                .append("pendingCount", pendingCount)
                .append("activeUsersPagination", activeUsersPagination)
                .append("inactiveUsersPagination", inactiveUsersPagination)
                .append("pendingInvitesPagination", pendingInvitesPagination)
                .append("includeInternalUsers", includeInternalUsers)
                .toString();
    }
}