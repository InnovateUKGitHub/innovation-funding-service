package com.worth.ifs.project.status.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class CompetitionProjectsStatusResource {
    private Long competitionNumber;
    private String formattedCompetitionNumber;
    private String competitionName;

    private List<ProjectStatusResource> projectStatusResources;

    public CompetitionProjectsStatusResource(Long competitionNumber, String formattedCompetitionNumber, String competitionName, List<ProjectStatusResource> projectStatusResources) {
        this.competitionNumber = competitionNumber;
        this.formattedCompetitionNumber = formattedCompetitionNumber;
        this.competitionName = competitionName;
        this.projectStatusResources = projectStatusResources;
    }

    // Required for JSON mapping
    public CompetitionProjectsStatusResource() {
    }

    public Long getCompetitionNumber() {
        return competitionNumber;
    }

    public void setCompetitionNumber(Long competitionNumber) {
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

    public String getFormattedCompetitionNumber() {
        return formattedCompetitionNumber;
    }

    public void setFormattedCompetitionNumber(String formattedCompetitionNumber) {
        this.formattedCompetitionNumber = formattedCompetitionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionProjectsStatusResource that = (CompetitionProjectsStatusResource) o;

        return new EqualsBuilder()
                .append(competitionNumber, that.competitionNumber)
                .append(formattedCompetitionNumber, that.formattedCompetitionNumber)
                .append(competitionName, that.competitionName)
                .append(projectStatusResources, that.projectStatusResources)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionNumber)
                .append(formattedCompetitionNumber)
                .append(competitionName)
                .append(projectStatusResources)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionNumber", competitionNumber)
                .append("formattedCompetitionNumber", formattedCompetitionNumber)
                .append("competitionName", competitionName)
                .append("projectStatusResources", projectStatusResources)
                .toString();
    }
}
