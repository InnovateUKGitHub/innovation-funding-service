package org.innovateuk.ifs.cofunder.resource;

public class CofunderAssignmentResource {
    private long assignmentId;
    private CofunderState state;
    private String comments;

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
}
