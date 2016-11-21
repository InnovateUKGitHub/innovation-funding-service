package com.worth.ifs.user.resource;

import java.util.List;

import static java.util.Arrays.asList;
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
    COMP_EXEC("competition_executive", "Competition Executive"),
    COMP_TECHNOLOGIST("competition_technologist", "Competition Technologist"),
    SYSTEM_MAINTAINER("system_maintainer", "System Maintainer"),
    SYSTEM_REGISTRATION_USER("system_registrar", "System Registration User"),
    PROJECT_FINANCE("project_finance", "Project Finance"),
    FINANCE_CONTACT("finance_contact", "Finance Contact"),
    PARTNER("partner", "Partner"),
    PROJECT_MANAGER("project_manager", "Project Manager"),
    IFS_ADMIN("ifs_admin", "IFS Administrator")
    ;

    private final String name;
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

    public static List<String> roleNames(UserRoleType... roles){
        return asList(roles).stream().map(r -> r.getName()).collect(toList());
    }
}
