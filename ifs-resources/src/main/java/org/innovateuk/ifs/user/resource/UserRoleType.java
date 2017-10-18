package org.innovateuk.ifs.user.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * The UserRoleType enumeration defines the available user roles.
 */
public enum UserRoleType {

    APPLICANT("applicant", "Applicant"),
    COLLABORATOR("collaborator", "Collaborator"),
    ASSESSOR("assessor","Assessor"),
    LEADAPPLICANT("leadapplicant", "Lead Applicant"),
    COMP_ADMIN("comp_admin", "Competition Administrator"),
    COMP_EXEC("competition_executive", "Portfolio Manager"),
    INNOVATION_LEAD("innovation_lead", "Innovation Lead"),
    SYSTEM_MAINTAINER("system_maintainer", "System Maintainer"),
    SYSTEM_REGISTRATION_USER("system_registrar", "System Registration User"),
    PROJECT_FINANCE("project_finance", "Project Finance"),
    FINANCE_CONTACT("finance_contact", "Finance Contact"),
    PARTNER("partner", "Partner"),
    PROJECT_MANAGER("project_manager", "Project Manager"),
    IFS_ADMINISTRATOR("ifs_administrator", "IFS Administrator"),
    SUPPORT("support", "IFS Support User");
    private String name;
    private String displayName;

    UserRoleType(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRoleType fromName(final String name){
        for (final UserRoleType userRoleType : UserRoleType.values()){
            if (userRoleType.getName().equals(name)){
                return userRoleType;
            }
        }
        throw new IllegalArgumentException("No UserRoleType with name " + name);
    }

    public static UserRoleType fromDisplayName(final String displayName){
        for (final UserRoleType userRoleType : UserRoleType.values()){
            if (userRoleType.getDisplayName().equals(displayName)){
                return userRoleType;
            }
        }
        throw new IllegalArgumentException("No UserRoleType with displayName " + displayName);
    }

    public static List<String> roleNames(UserRoleType... roles){
        return Arrays.stream(roles).map(UserRoleType::getName).collect(toList());
    }

    public static Set<UserRoleType> internalRoles(){
        return new HashSet<>(Arrays.asList(IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD));
    }

    public static Set<UserRoleType> externalApplicantRoles(){
        return new HashSet<>(Arrays.asList(APPLICANT, COLLABORATOR, FINANCE_CONTACT, PARTNER, PROJECT_MANAGER));
    }
}
