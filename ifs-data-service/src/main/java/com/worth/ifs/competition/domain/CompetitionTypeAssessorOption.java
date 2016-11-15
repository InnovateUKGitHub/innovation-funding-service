package com.worth.ifs.competition.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CompetitionTypeAssessorOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long competitionTypeId;

    private String assessorOptionName;

    private Integer assessorOptionValue;

    private Boolean defaultOption;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetitionTypeId() {
        return competitionTypeId;
    }

    public void setCompetitionTypeId(Long competitionTypeId) {
        this.competitionTypeId = competitionTypeId;
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
}
