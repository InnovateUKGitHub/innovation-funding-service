package org.innovateuk.ifs.management.externalrole.viewmodel;

import org.innovateuk.ifs.user.resource.Role;

public class ExternalRoleViewModel {

    private final long userId;
    private final String userName;
    private final String email;
    private final Role role;
    private final boolean cofunderEnabled;

    public ExternalRoleViewModel(long userId, String userName, String email, Role role, boolean cofunderEnabled) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.cofunderEnabled = cofunderEnabled;
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

    public boolean isCofunderEnabled() {
        return cofunderEnabled;
    }

    public String getLinkTitle() {
        return cofunderEnabled ? "Back to invite a new external role" : "Back to view user details";
    }

    public String getBackLink() {
        return cofunderEnabled ? String.format("/admin/user/%d/select", userId) : String.format("/admin/user/%d/active", userId);
    }

}
