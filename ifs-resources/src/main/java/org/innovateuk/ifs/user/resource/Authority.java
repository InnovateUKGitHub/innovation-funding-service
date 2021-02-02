package org.innovateuk.ifs.user.resource;

public enum Authority {
    ASSESSOR,
    APPLICANT,
    COMP_ADMIN,
    SYSTEM_REGISTRATION_USER,
    SYSTEM_MAINTAINER,
    PROJECT_FINANCE,
    INNOVATION_LEAD,
    IFS_ADMINISTRATOR,
    SUPPORT,
    MONITORING_OFFICER,
    STAKEHOLDER,
    LIVE_PROJECTS_USER,
    EXTERNAL_FINANCE,
    KNOWLEDGE_TRANSFER_ADVISER,
    SUPPORTER;

    public String toSpringSecurityAuthorityString() {
        return this.name().toLowerCase();
    }
}