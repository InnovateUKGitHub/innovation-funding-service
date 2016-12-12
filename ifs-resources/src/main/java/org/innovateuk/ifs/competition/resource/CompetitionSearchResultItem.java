package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

/**
 * A summary of competition information displayed during competition search
 */
public class CompetitionSearchResultItem {

    private Long id;
    private String name;
    private Set<String> innovationAreaNames;
    private Integer numberOfApplications;
    private String startDateDisplay;
    private CompetitionStatus competitionStatus;
    private String competitionTypeName;
    private Integer projectsCount;

    // for JSON marshalling
    CompetitionSearchResultItem() {
    }

    public CompetitionSearchResultItem(Long id, String name, Set<String> innovationAreaNames, Integer numberOfApplications,
                                       String startDateDisplay, CompetitionStatus competitionStatus,
                                       String competitionTypeName, Integer projectsCount) {
        this.id = id;
        this.name = name;
        this.innovationAreaNames = innovationAreaNames;
        this.numberOfApplications = numberOfApplications;
        this.startDateDisplay = startDateDisplay;
        this.competitionStatus = competitionStatus;
        this.competitionTypeName = competitionTypeName;
        this.projectsCount = projectsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }

    public void setInnovationAreaNames(Set<String> innovationAreaNames) {
        this.innovationAreaNames = innovationAreaNames;
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

    public Integer getProjectsCount() {
        return projectsCount;
    }

    public void setProjectsCount(Integer projectsCount) {
        this.projectsCount = projectsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionSearchResultItem that = (CompetitionSearchResultItem) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(innovationAreaNames, that.innovationAreaNames)
                .append(numberOfApplications, that.numberOfApplications)
                .append(startDateDisplay, that.startDateDisplay)
                .append(competitionStatus, that.competitionStatus)
                .append(projectsCount, that.projectsCount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(innovationAreaNames)
                .append(numberOfApplications)
                .append(startDateDisplay)
                .append(competitionStatus)
                .append(projectsCount)
                .toHashCode();
    }
}
