package com.worth.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A summary of competition information displayed during competition search
 */
public class CompetitionSearchResultItem {

    private Long id;
    private String name;
    private String innovationAreaName;
    private Integer numberOfApplications;
    private String startDateDisplay;
    private CompetitionStatus competitionStatus;
    private String competitionTypeName;

    // for JSON marshalling
    CompetitionSearchResultItem() {
    }

    public CompetitionSearchResultItem(Long id, String name, String innovationAreaName, Integer numberOfApplications, String startDateDisplay, CompetitionStatus competitionStatus, String competitionTypeName) {
        this.id = id;
        this.name = name;
        this.innovationAreaName = innovationAreaName;
        this.numberOfApplications = numberOfApplications;
        this.startDateDisplay = startDateDisplay;
        this.competitionStatus = competitionStatus;
        this.competitionTypeName = competitionTypeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInnovationAreaName() {
        return innovationAreaName;
    }

    public void setInnovationAreaName(String innovationAreaName) {
        this.innovationAreaName = innovationAreaName;
    }

    public Integer getNumberOfApplications() {
        return numberOfApplications;
    }

    public void setNumberOfApplications(Integer numberOfApplications) {
        this.numberOfApplications = numberOfApplications;
    }

    public String getStartDateDisplay() {
        return startDateDisplay;
    }

    public void setStartDateDisplay(String startDateDisplay) {
        this.startDateDisplay = startDateDisplay;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompetitionTypeName() {
        return competitionTypeName;
    }

    public void setCompetitionTypeName(String competitionTypeName) {
        this.competitionTypeName = competitionTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionSearchResultItem that = (CompetitionSearchResultItem) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(innovationAreaName, that.innovationAreaName)
                .append(numberOfApplications, that.numberOfApplications)
                .append(startDateDisplay, that.startDateDisplay)
                .append(competitionStatus, that.competitionStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(innovationAreaName)
                .append(numberOfApplications)
                .append(startDateDisplay)
                .append(competitionStatus)
                .toHashCode();
    }
}
