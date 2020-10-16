package org.innovateuk.ifs.cofunder.resource;

public class CofunderAssignmentResource {
    private long assignmentId;
    private CofunderState state;
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

    public CofunderState getState() {
        return state;
    }

    public void setState(CofunderState state) {
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
}
