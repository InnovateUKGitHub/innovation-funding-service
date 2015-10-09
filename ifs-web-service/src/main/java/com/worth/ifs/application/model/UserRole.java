package com.worth.ifs.application.model;
/**
 * This {@code UserRole} enumeration defines the available global user roles.
 */
public enum UserRole {
    APPLICANT("applicant"),
    ASSESSOR("assessor");

    private String roleName;
    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
