package com.worth.ifs.competition.domain;

import com.worth.ifs.competition.resource.MilestoneType;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity model to store the Competition Milestones
 */
@Entity
public class Milestone {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private MilestoneType type;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    public Milestone() {}

    public Milestone(Long id, MilestoneType type, LocalDateTime date, Competition competition) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.competition = competition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MilestoneType getType() {
        return type;
    }

    public void setType(MilestoneType type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
