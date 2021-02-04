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

    ASSESSOR                    ( 3, "Assessor", Authority.ASSESSOR),
    APPLICANT                   ( 4, "Applicant", Authority.APPLICANT),
    COMP_ADMIN                  ( 5,  "Competition Administrator", Authority.COMP_ADMIN),
    SYSTEM_REGISTRATION_USER    ( 6, "System Registration User", Authority.SYSTEM_REGISTRAR),
    SYSTEM_MAINTAINER           ( 7, "System Maintainer", Authority.SYSTEM_MAINTAINER, Authority.IFS_ADMINISTRATOR, Authority.PROJECT_FINANCE, Authority.COMP_ADMIN),
    PROJECT_FINANCE             ( 8, "Project Finance", Authority.PROJECT_FINANCE, Authority.COMP_ADMIN),
    INNOVATION_LEAD             (13, "Innovation Lead", Authority.INNOVATION_LEAD),
    IFS_ADMINISTRATOR           (14, "IFS Administrator", Authority.IFS_ADMINISTRATOR, Authority.PROJECT_FINANCE, Authority.COMP_ADMIN),
    SUPPORT                     (15, "IFS Support User", Authority.SUPPORT),
    MONITORING_OFFICER          (19, "Monitoring Officer", Authority.MONITORING_OFFICER),
    STAKEHOLDER                 (20, "Stakeholder", Authority.STAKEHOLDER),
    LIVE_PROJECTS_USER          (21, "Live projects user", Authority.LIVE_PROJECTS_USER),
    EXTERNAL_FINANCE            (22, "External finance reviewer", Authority.EXTERNAL_FINANCE),
    KNOWLEDGE_TRANSFER_ADVISER  (23, "Knowledge transfer adviser", Authority.KNOWLEDGE_TRANSFER_ADVISER, Authority.ASSESSOR, Authority.MONITORING_OFFICER),
    SUPPORTER                   (24, "Supporter", Authority.SUPPORTER);

    final long id;
    final String displayName;
    final List<Authority> authorities;

    Role(final long id, final String displayName, final Authority... authorities) {
        this.id = id;
        this.displayName = displayName;
        this.authorities = newArrayList(authorities);
    }

    public static Role getById (long id) {
        return Stream.of(values()).filter(role -> role.id == id).findFirst().get();
    }

    public long getId() {
        return id;
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
        return multiDashboardRoles().stream().anyMatch(roles::contains);
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public static Set<Role> externalRolesToInvite() {
        return EnumSet.of(KNOWLEDGE_TRANSFER_ADVISER, SUPPORTER);
    }
}