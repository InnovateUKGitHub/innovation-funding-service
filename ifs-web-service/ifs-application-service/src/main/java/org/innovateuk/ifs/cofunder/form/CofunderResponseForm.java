package org.innovateuk.ifs.cofunder.form;

import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;

import javax.validation.constraints.NotBlank;

public class CofunderResponseForm {

    private long assignmentId;
    private Boolean decision;
    @NotBlank(message = "{validation.cofunder.response.comments.required}")
    private String comments;

    public CofunderResponseForm() {
    }

    public CofunderResponseForm(CofunderAssignmentResource assignment) {
        this.assignmentId = assignment.getAssignmentId();
        if (assignment.getState() == CofunderState.CREATED) {
            this.decision = null;
        } else if (assignment.getState() == CofunderState.ACCEPTED) {
            this.decision = true;
        } else {
            this.decision = false;
        }
        this.comments = assignment.getComments();
    }

    public long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Boolean getDecision() {
        return decision;
    }

    public void setDecision(Boolean decision) {
        this.decision = decision;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
