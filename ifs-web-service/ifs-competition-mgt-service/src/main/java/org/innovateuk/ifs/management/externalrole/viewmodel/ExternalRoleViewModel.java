package org.innovateuk.ifs.management.externalrole.viewmodel;

public class ExternalRoleViewModel {

    private final long userId;
    private final String userName;
    private final String email;

    public ExternalRoleViewModel(long userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
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
}
