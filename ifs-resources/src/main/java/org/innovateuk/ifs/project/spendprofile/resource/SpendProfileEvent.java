package org.innovateuk.ifs.project.spendprofile.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

public enum SpendProfileEvent implements ProcessEvent {
    PROJECT_CREATED("project-created"),
    SPEND_PROFILE_GENERATED("spend-profile-generated"),
    SPEND_PROFILE_SUBMITTED("spend-profile-submitted"),
    SPEND_PROFILE_APPROVED("spend-profile-approved"),
    SPEND_PROFILE_REJECTED("spend-profile-rejected");

    String event;

    SpendProfileEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
