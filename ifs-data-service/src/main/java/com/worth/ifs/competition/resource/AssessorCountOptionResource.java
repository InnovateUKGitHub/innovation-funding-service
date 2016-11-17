package com.worth.ifs.competition.resource;

public class AssessorCountOptionResource {

    private Long id;

    private Long competitionType;

    private String optionName;

    private Integer optionValue;

    private Boolean defaultOption;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public Integer getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(Integer optionValue) {
        this.optionValue = optionValue;
    }

    public Boolean getDefaultOption() {
        return defaultOption;
    }

    public void setDefaultOption(Boolean defaultOption) {
        this.defaultOption = defaultOption;
    }

    public Long getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(Long competitionType) {
        this.competitionType = competitionType;
    }
}
