package com.worth.ifs.application.helper;

public enum UserApplicationRole {
    LEAD_APPLICANT("leadapplicant"),
    COLLABORATOR("collaborator"),
    ASSESSOR("assessor");

    String roleName;
    UserApplicationRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
