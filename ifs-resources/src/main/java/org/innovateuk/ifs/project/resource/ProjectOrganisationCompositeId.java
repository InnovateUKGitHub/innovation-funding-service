package org.innovateuk.ifs.project.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;


public class ProjectOrganisationCompositeId implements Serializable {

    private long projectId;

    private long organisationId;

    public static ProjectOrganisationCompositeId id(long projectId, long organisationId){
        return new ProjectOrganisationCompositeId(projectId, organisationId);
    }

    public ProjectOrganisationCompositeId() {}

    public ProjectOrganisationCompositeId(long projectId, long organisationId) {

        this.projectId = projectId;
        this.organisationId = organisationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

//    public void setProjectId(long projectId) {
//        this.projectId = projectId;
//    }
//
//    public void setOrganisationId(long organisationId) {
//        this.organisationId = organisationId;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectOrganisationCompositeId that = (ProjectOrganisationCompositeId) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(organisationId, that.organisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(organisationId)
                .toHashCode();
    }
}