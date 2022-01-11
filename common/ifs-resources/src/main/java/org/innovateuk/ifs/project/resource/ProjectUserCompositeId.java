package org.innovateuk.ifs.project.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Class to enable spring security to apply type information to determine access
 */
public class ProjectUserCompositeId implements Serializable {

    private long projectId;

    private long userId;

    public ProjectUserCompositeId(long projectId, long userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getUserId() {
        return userId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectUserCompositeId that = (ProjectUserCompositeId) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(userId, that.userId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(userId)
                .toHashCode();
    }
}
