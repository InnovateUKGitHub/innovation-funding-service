package com.worth.ifs.user.domain;

/**
 * The UserRoleType enumeration defines the available user roles.
 */
public enum UserRoleType {

    APPLICANT("applicant"),
    COLLABORATOR("collaborator"),
    ASSESSOR("assessor"),
    LEADAPPLICANT("leadapplicant"),
    COMP_ADMIN("comp_admin"),
    SYSTEM_REGISTRATION_USER("system_registrar");

    private final String name;

    UserRoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
