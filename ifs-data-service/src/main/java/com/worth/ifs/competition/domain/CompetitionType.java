package com.worth.ifs.competition.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class CompetitionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Boolean stateAid;
    private Boolean active;

    @OneToMany(mappedBy="competitionType",fetch=FetchType.LAZY)
    private List<Competition> competitions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
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
