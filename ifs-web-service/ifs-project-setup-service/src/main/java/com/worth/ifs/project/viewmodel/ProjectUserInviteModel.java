package com.worth.ifs.project.viewmodel;

// model for selecting project users

public class ProjectUserInviteModel {
    private String name;
    private String status;
    private Long id;

    public ProjectUserInviteModel(final ProjectUserInviteStatus status, final String name, final Long id) {
        this.status = status.name();
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }
}
