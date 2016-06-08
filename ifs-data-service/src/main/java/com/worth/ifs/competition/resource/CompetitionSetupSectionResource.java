package com.worth.ifs.competition.resource;

public class CompetitionSetupSectionResource {
    private Long id;
    private String name;
    private String path;
    private Integer priority;

    public CompetitionSetupSectionResource() {
    }

    public CompetitionSetupSectionResource(Long id, String name, String path, Integer priority) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

