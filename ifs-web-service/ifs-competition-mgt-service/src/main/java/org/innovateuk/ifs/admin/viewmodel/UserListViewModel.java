package org.innovateuk.ifs.admin.viewmodel;

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

    public PaginationViewModel getPagination() {
        return activeUsersPagination;
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
}
