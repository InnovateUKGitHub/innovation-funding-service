package org.innovateuk.ifs.admin.viewmodel;

import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Set;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class UserListViewModel {
    private Set<UserResource> users;

    public Set<UserResource> getUsers() {
        return users;
    }

    public void setUsers(Set<UserResource> users) {
        this.users = users;
    }
}
