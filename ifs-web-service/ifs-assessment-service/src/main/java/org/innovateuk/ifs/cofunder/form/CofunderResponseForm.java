package org.innovateuk.ifs.cofunder.form;

import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CofunderResponseForm {

    private long assignmentId;
    @NotNull(message = "{validation.cofunder.response.decision.required}")
    private Boolean decision;
    @NotBlank(message = "{validation.cofunder.response.comments.required}")
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 250, message = "{validation.field.max.word.count}")
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
