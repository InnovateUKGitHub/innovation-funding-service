package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.identity.Identifiable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Role defines database relations and a model to use client side and server side.
 */
public enum Role implements Identifiable {

    ASSESSOR                    ( 3, "assessor",        "Assessor"),
    APPLICANT                   ( 4, "applicant",       "Applicant"),
    COMP_ADMIN                  ( 5, "comp_admin",              "Competition Administrator"),
    SYSTEM_REGISTRATION_USER    ( 6, "system_registrar",        "System Registration User"),
    SYSTEM_MAINTAINER           ( 7, "system_maintainer",       "System Maintainer"),
    PROJECT_FINANCE             ( 8, "project_finance",         "Project Finance"),
    INNOVATION_LEAD             (13, "innovation_lead",     "Innovation Lead"),
    IFS_ADMINISTRATOR           (14, "ifs_administrator",   "IFS Administrator"),
    SUPPORT                     (15, "support",             "IFS Support User"),
    MONITORING_OFFICER          (19, "monitoring_officer",         "Monitoring Officer"),
    STAKEHOLDER                 (20, "stakeholder",                "Stakeholder"),
    LIVE_PROJECTS_USER          (21, "live_projects_user",         "Live projects user"),
    EXTERNAL_FINANCE            (22, "external_finance",           "External finance reviewer"),
    KNOWLEDGE_TRANSFER_ADVISER  (23, "knowledge_transfer_adviser", "Knowledge transfer adviser"),
    SUPPORTER                   (24, "supporter",                    "Supporter");

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


    public boolean isStakeHolder() {return this == STAKEHOLDER; }

    public boolean isAssessor() {return this == ASSESSOR; }

    public boolean isKta() {
        return this == KNOWLEDGE_TRANSFER_ADVISER;
    }

    public static Set<Role> internalRoles(){
        return EnumSet.of(IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    public static Set<Role> inviteExternalRoles(){
        return EnumSet.of(KNOWLEDGE_TRANSFER_ADVISER);
    }

    public static Set<Role> externalRoles() {
        return EnumSet.of(APPLICANT, ASSESSOR, KNOWLEDGE_TRANSFER_ADVISER, SUPPORTER);
    }

    public static List<Role> multiDashboardRoles() {
        return newArrayList(APPLICANT, ASSESSOR, STAKEHOLDER, MONITORING_OFFICER, LIVE_PROJECTS_USER, SUPPORTER);
    }

    public static boolean containsMultiDashboardRole(Collection<Role> roles){
        return multiDashboardRoles().stream().anyMatch(role -> roles.contains(role));
    }

    public List<String> getAuthorities() {
        if (this == KNOWLEDGE_TRANSFER_ADVISER) {
            return newArrayList(this.name, ASSESSOR.name, MONITORING_OFFICER.name);
        } if (this == SYSTEM_MAINTAINER) {
            return newArrayList(this.name, IFS_ADMINISTRATOR.name, PROJECT_FINANCE.name);
        }
        return newArrayList(this.name);
    }

    public static Set<Role> externalRolesToInvite() {
        return EnumSet.of(KNOWLEDGE_TRANSFER_ADVISER, SUPPORTER);
    }
}