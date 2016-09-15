package com.worth.ifs.project.consortiumoverview.viewmodel;

import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProjectConsortiumStatusViewModel {
    private Long projectId;
    private ProjectTeamStatusResource projectTeamStatusResource;

    public ProjectConsortiumStatusViewModel(final Long projectId, final ProjectTeamStatusResource projectTeamStatusResource) {
        this.projectId = projectId;
        this.projectTeamStatusResource = projectTeamStatusResource;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectTeamStatusResource getProjectTeamStatusResource() {
        return projectTeamStatusResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectConsortiumStatusViewModel that = (ProjectConsortiumStatusViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(projectTeamStatusResource, that.projectTeamStatusResource)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectTeamStatusResource)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("projectId", projectId)
                .append("projectTeamStatusResource", projectTeamStatusResource)
                .toString();
    }
}
