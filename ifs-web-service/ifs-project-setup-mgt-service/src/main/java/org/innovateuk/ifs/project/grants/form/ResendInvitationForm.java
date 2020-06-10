package org.innovateuk.ifs.project.grants.form;

public class ResendInvitationForm {
    private long projectId;
    private long inviteId;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getInviteId() {
        return inviteId;
    }

    public void setInviteId(long inviteId) {
        this.inviteId = inviteId;
    }
}
