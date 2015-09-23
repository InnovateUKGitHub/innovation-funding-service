package com.worth.ifs.application.model;

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
