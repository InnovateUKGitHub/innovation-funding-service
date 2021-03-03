package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.identity.Identifiable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Role defines roles that link an application and user.
 */
public enum ProcessRoleType implements Identifiable {

    LEADAPPLICANT               ( 1),
    COLLABORATOR                ( 2),
    ASSESSOR                    ( 3),
    PANEL_ASSESSOR              (16),
    INTERVIEW_ASSESSOR          (17),
    INTERVIEW_LEAD_APPLICANT    (18),
    KNOWLEDGE_TRANSFER_ADVISER  (23),
    SUPPORTER                   (24);

    final long id;

    ProcessRoleType(final long id) {
        this.id = id;
    }
    public static Set<ProcessRoleType> applicantProcessRoles() {
        return EnumSet.of(LEADAPPLICANT, COLLABORATOR);
    }

    public static Set<ProcessRoleType> assessorProcessRoles() {
        return EnumSet.of(ASSESSOR, INTERVIEW_ASSESSOR, PANEL_ASSESSOR);
    }

    public boolean isLeadApplicant() {
        return this == LEADAPPLICANT;
    }

    public boolean isCollaborator() {
        return this == COLLABORATOR;
    }

    public boolean isKta() {
        return this == KNOWLEDGE_TRANSFER_ADVISER;
    }

    public static Set<ProcessRoleType> externalApplicantRoles() {
        return EnumSet.of(LEADAPPLICANT, COLLABORATOR);
    }

    @Override
    public long getId() {
        return id;
    }
}