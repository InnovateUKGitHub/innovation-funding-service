package com.worth.ifs.invite.domain;

import com.worth.ifs.project.domain.Project;

/**
 * The exact kind of {@link ProjectParticipant}.
 */
public enum ProjectParticipantRole implements ParticipantRole<Project> {
    PROJECT_MANAGER, PROJECT_FINANCE_OFFICER;
}
