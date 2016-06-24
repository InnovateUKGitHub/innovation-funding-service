package com.worth.ifs.competition.resource;

import java.time.LocalDateTime;

public class MilestoneResource {
    private Long id;
    private String name;
    private LocalDateTime date;
    private Long competition;


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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }
}
