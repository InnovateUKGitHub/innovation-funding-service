package com.worth.ifs.application.form;

import javax.validation.constraints.NotNull;

public class RemoveContributorsForm {
    @NotNull
    private Long applicationInviteId;

    public Long getApplicationInviteId() {
        return applicationInviteId;
    }

    public void setApplicationInviteId(Long applicationInviteId) {
        this.applicationInviteId = applicationInviteId;
    }
}

