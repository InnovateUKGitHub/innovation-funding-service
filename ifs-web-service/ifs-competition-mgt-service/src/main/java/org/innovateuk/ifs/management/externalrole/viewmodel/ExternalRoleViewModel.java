package org.innovateuk.ifs.management.externalrole.viewmodel;

import org.innovateuk.ifs.user.resource.Role;

public class ExternalRoleViewModel {

    private final long userId;
    private final String userName;
    private final String email;
    private final Role role;

    public ExternalRoleViewModel(long userId, String userName, String email, Role role) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
