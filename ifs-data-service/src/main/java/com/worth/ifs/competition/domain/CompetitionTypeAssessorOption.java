package com.worth.ifs.competition.domain;

import javax.persistence.*;

@Entity
public class CompetitionTypeAssessorOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="competitionTypeId", referencedColumnName="id")
    private CompetitionType competitionType;

    private String assessorOptionName;

    private Integer assessorOptionValue;

    private Boolean defaultOption;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssessorOptionName() {
        return assessorOptionName;
    }

    public void setAssessorOptionName(String assessorOptionName) {
        this.assessorOptionName = assessorOptionName;
    }

    public Integer getAssessorOptionValue() {
        return assessorOptionValue;
    }

    public void setAssessorOptionValue(Integer assessorOptionValue) {
        this.assessorOptionValue = assessorOptionValue;
    }

    public Boolean getDefaultOption() {
        return defaultOption;
    }

    public void setDefaultOption(Boolean defaultOption) {
        this.defaultOption = defaultOption;
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }
}
