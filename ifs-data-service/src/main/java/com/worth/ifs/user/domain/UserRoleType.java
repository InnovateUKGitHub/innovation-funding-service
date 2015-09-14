package com.worth.ifs.user.domain;

/**
 * Created by nunoalexandre on 14/09/15.
 */
public enum UserRoleType {

    APPLICANT("applicant"), COLLABORATOR("collaborator"), ASSESSOR("assessor");


    private final String name;

    UserRoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
