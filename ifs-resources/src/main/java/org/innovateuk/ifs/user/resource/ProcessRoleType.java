package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.identity.Identifiable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Role defines database relations and a model to use client side and server side.
 */
public enum ProcessRoleType implements Identifiable {

    LEADAPPLICANT               ( 1, "leadapplicant",   "Lead Applicant"),
    COLLABORATOR                ( 2, "collaborator",    "Collaborator"),
    ASSESSOR                    ( 3, "assessor",        "Assessor"),
    PANEL_ASSESSOR              (16, "panel_assessor",             "Panel Assessor"),
    INTERVIEW_ASSESSOR          (17, "interview_assessor",         "Interview Assessor"),
    INTERVIEW_LEAD_APPLICANT    (18, "interview_lead_applicant",   "Interview Lead Applicant"),
    KNOWLEDGE_TRANSFER_ADVISER  (23, "knowledge_transfer_adviser", "Knowledge transfer adviser"),
    SUPPORTER                   (24, "supporter",                    "Supporter");

    final long id;
    final String name;
    final String displayName;

    ProcessRoleType(final long id, final String name, final String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
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