package com.worth.ifs.project.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class CompetitionProjectsStatusResource {
    private String competitionNumber;
    private String competitionName;

    private List<ProjectStatusResource> projectStatusResources;

    public CompetitionProjectsStatusResource(String competitionNumber, String competitionName, List<ProjectStatusResource> projectStatusResources) {
        this.competitionNumber = competitionNumber;
        this.competitionName = competitionName;
        this.projectStatusResources = projectStatusResources;
    }

    // Required for JSON mapping
    public CompetitionProjectsStatusResource() {
    }

    public String getCompetitionNumber() {
        return competitionNumber;
    }

    public void setCompetitionNumber(String competitionNumber) {
        this.competitionNumber = competitionNumber;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public List<ProjectStatusResource> getProjectStatusResources() {
        return projectStatusResources;
    }

    public void setProjectStatusResources(List<ProjectStatusResource> projectStatusResources) {
        this.projectStatusResources = projectStatusResources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionProjectsStatusResource that = (CompetitionProjectsStatusResource) o;

        return new EqualsBuilder()
                .append(competitionNumber, that.competitionNumber)
                .append(competitionName, that.competitionName)
                .append(projectStatusResources, that.projectStatusResources)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionNumber)
                .append(competitionName)
                .append(projectStatusResources)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionNumber", competitionNumber)
                .append("competitionName", competitionName)
                .append("projectStatusResources", projectStatusResources)
                .toString();
    }
}
