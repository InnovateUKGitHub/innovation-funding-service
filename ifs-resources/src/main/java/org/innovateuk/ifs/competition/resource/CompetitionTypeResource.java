package org.innovateuk.ifs.competition.resource;

import java.util.List;

public class CompetitionTypeResource {
    private Long id;
    private String name;
    private List<Long> competitions;
    //@ZeroDowntime(reference = "IFS-3288", description = "Remove stateAid flag from CompetitionTypeResource in the
    // next release")
    private Boolean stateAid = Boolean.TRUE;
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

    //@ZeroDowntime(reference = "IFS-3288", description = "Remove stateAid flag from CompetitionTypeResource in the
    // next release")
    public Boolean getStateAid() {
        return stateAid;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
