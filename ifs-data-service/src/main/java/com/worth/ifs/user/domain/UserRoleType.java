package com.worth.ifs.user.domain;

/**
 * The UserRoleType enumeration defines the available user roles.
 */
public enum UserRoleType {

    APPLICANT("applicant"), COLLABORATOR("collaborator"), ASSESSOR("assessor"),LEADAPPLICANT("leadapplicant");


    private final String name;

    UserRoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
