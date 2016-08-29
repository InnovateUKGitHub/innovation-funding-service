package com.worth.ifs.competition.resource;


import java.util.List;

public class CompetitionTypeResource {
    private Long id;
    private String name;
    private List<Long> competitions;
    private Boolean stateAid;
    private Boolean active;

    public CompetitionTypeResource() {
        // no-arg constructor
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

    public List<Long> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Long> competitions) {
        this.competitions = competitions;
    }

    public Boolean getStateAid() {
        return stateAid;
    }

    public void setStateAid(Boolean stateAid) {
        this.stateAid = stateAid;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
