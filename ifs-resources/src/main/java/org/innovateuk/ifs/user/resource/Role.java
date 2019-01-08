package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.identity.Identifiable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * Role defines database relations and a model to use client side and server side.
 */
public enum Role implements Identifiable {

    LEADAPPLICANT               ( 1, "leadapplicant",   "Lead Applicant"),
    COLLABORATOR                ( 2, "collaborator",    "Collaborator"),
    ASSESSOR                    ( 3, "assessor",        "Assessor"),
    APPLICANT                   ( 4, "applicant",       "Applicant"),

    COMP_ADMIN                  ( 5, "comp_admin",              "Competition Administrator"),
    SYSTEM_REGISTRATION_USER    ( 6, "system_registrar",        "System Registration User"),
    SYSTEM_MAINTAINER           ( 7, "system_maintainer",       "System Maintainer"),
    PROJECT_FINANCE             ( 8, "project_finance",         "Project Finance"),
    FINANCE_CONTACT             ( 9, "finance_contact",         "Finance Contact"),
    PARTNER                     (10, "partner",                 "Partner"),
    PROJECT_MANAGER             (11, "project_manager",         "Project Manager"),
    COMP_EXEC                   (12, "competition_executive",   "Portfolio Manager"),

    INNOVATION_LEAD             (13, "innovation_lead",     "Innovation Lead"),
    IFS_ADMINISTRATOR           (14, "ifs_administrator",   "IFS Administrator"),
    SUPPORT                     (15, "support",             "IFS Support User"),

    PANEL_ASSESSOR              (16, "panel_assessor",              "Panel Assessor"),
    INTERVIEW_ASSESSOR          (17, "interview_assessor",          "Interview Assessor"),
    INTERVIEW_LEAD_APPLICANT    (18, "interview_lead_applicant",    "Interview Lead Applicant"),
    MONITORING_OFFICER          (19, "monitoring_officer",       "Monitoring Officer"),
    STAKEHOLDER                 (20, "stakeholder",               "Stakeholder"),
    ACC_USER                    (21, "acc_user",               "ACC User");

    final long id;
    final String name;
    final String displayName;

    Role(final long id, final String name, final String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }

    public static Role getByName(String name) {
        return Stream.of(values()).filter(role -> role.name.equals(name)).findFirst().get();
    }

    public static Role getById (long id) {
        return Stream.of(values()).filter(role -> role.id == id).findFirst().get();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPartner() {
        return this == PARTNER;
    }

    public boolean isLeadApplicant() {
        return this == LEADAPPLICANT;
    }

    public boolean isCollaborator() {
        return this == COLLABORATOR;
    }

    public boolean isProjectManager() {
        return this == PROJECT_MANAGER;
    }

    public boolean isStakeHolder() {return this == STAKEHOLDER; }

    public static List<Role> applicantProcessRoles() { return Arrays.asList(LEADAPPLICANT, COLLABORATOR); }

    public static Set<Role> internalRoles(){
        return new HashSet<>(asList(IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD));
    }

    public static Set<Role> externalApplicantRoles(){
        return new HashSet<>(asList(APPLICANT, COLLABORATOR, FINANCE_CONTACT, PARTNER, PROJECT_MANAGER));
    }
}