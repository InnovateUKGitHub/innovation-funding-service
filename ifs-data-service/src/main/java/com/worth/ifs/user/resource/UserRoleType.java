package com.worth.ifs.user.resource;

/**
 * The UserRoleType enumeration defines the available user roles.
 */
public enum UserRoleType {

    APPLICANT("applicant"),
    COLLABORATOR("collaborator"),
    ASSESSOR("assessor"),
    LEADAPPLICANT("leadapplicant"),
    COMP_ADMIN("comp_admin"),
    COMP_EXEC("competition_executive"),
    COMP_TECHNOLOGIST("competition_technologist"),
    SYSTEM_MAINTAINER("system_maintainer"),
    SYSTEM_REGISTRATION_USER("system_registrar"),
    PROJECT_FINANCE("project_finance");

    private final String name;

    UserRoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static UserRoleType fromName(final String name){
        for (final UserRoleType userRoleType : UserRoleType.values()){
            if (userRoleType.getName().equals(name)){
                return userRoleType;
            }
        }
        throw new IllegalArgumentException("No UserRoleType with name " + name);
    }
}
