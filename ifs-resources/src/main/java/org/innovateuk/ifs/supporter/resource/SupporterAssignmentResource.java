package org.innovateuk.ifs.supporter.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SupporterAssignmentResource {
    private long assignmentId;
    private SupporterState state;
    private String comments;
    private String userName;
    private String userEmail;
    private String userSimpleOrganisation;

    public long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public SupporterState getState() {
        return state;
    }

    public void setState(SupporterState state) {
        this.state = state;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserSimpleOrganisation() {
        return userSimpleOrganisation;
    }

    public void setUserSimpleOrganisation(String userSimpleOrganisation) {
        this.userSimpleOrganisation = userSimpleOrganisation;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assignmentId)
                .append(state)
                .append(comments)
                .append(userName)
                .append(userEmail)
                .append(userSimpleOrganisation)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SupporterAssignmentResource that = (SupporterAssignmentResource) o;

        return new EqualsBuilder()
                .append(assignmentId, that.assignmentId)
                .append(state, that.state)
                .append(comments, that.comments)
                .append(userName, that.userName)
                .append(userEmail, that.userEmail)
                .append(userSimpleOrganisation, that.userSimpleOrganisation)
                .isEquals();
    }
}
