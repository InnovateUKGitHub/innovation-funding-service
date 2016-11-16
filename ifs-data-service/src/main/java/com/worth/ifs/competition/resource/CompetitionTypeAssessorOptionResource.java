package com.worth.ifs.competition.resource;

public class CompetitionTypeAssessorOptionResource {

    private Long id;

    private Long competitionType;

    private String assessorOptionName;

    private Integer assessorOptionValue;

    private Boolean defaultOption;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(Long competitionType) {
        this.competitionType = competitionType;
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
