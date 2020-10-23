package org.innovateuk.ifs.supporter.form;

import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SupporterResponseForm {

    private long assignmentId;
    @NotNull(message = "{validation.supporter.response.decision.required}")
    private Boolean decision;
    @NotBlank(message = "{validation.supporter.response.comments.required}")
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 250, message = "{validation.field.max.word.count}")
    private String comments;

    public SupporterResponseForm() {
    }

    public SupporterResponseForm(SupporterAssignmentResource assignment) {
        this.assignmentId = assignment.getAssignmentId();
        if (assignment.getState() == SupporterState.CREATED) {
            this.decision = null;
        } else if (assignment.getState() == SupporterState.ACCEPTED) {
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
