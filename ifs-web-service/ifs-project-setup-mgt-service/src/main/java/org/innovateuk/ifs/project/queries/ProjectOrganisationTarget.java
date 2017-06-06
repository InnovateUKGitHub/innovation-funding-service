package org.innovateuk.ifs.project.queries;

/**
 * Combine organisation id and project id for use in permission rule
 */
public class ProjectOrganisationTarget {

    private long projectId;
    private long organisationId;
    public ProjectOrganisationTarget(long projectId, long organisationId) {
        this.projectId = projectId;
        this.organisationId = organisationId;
    }
    public long getProjectId() {
        return projectId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectOrganisationTarget)) return false;

        ProjectOrganisationTarget that = (ProjectOrganisationTarget) o;

        if (projectId != that.projectId) return false;
        return organisationId == that.organisationId;
    }

    @Override
    public int hashCode() {
        int result = (int) (projectId ^ (projectId >>> 32));
        result = 31 * result + (int) (organisationId ^ (organisationId >>> 32));
        return result;
    }


}
