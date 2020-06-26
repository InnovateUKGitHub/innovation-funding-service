package org.innovateuk.ifs.registration.grantsinvite.viewmodel;

import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;

public class GrantsInviteViewModel {

    private final long applicationId;
    private final long projectId;
    private final String projectName;
    private final GrantsInviteRole role;
    private final boolean userExists;
    private final boolean userLoggedIn;

    public GrantsInviteViewModel(long applicationId, long projectId, String projectName, GrantsInviteRole role, boolean userExists, boolean userLoggedIn) {
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.role = role;
        this.userExists = userExists;
        this.userLoggedIn = userLoggedIn;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public GrantsInviteRole getRole() {
        return role;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }
}
