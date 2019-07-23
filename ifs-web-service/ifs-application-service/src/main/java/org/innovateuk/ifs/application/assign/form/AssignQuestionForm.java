package org.innovateuk.ifs.application.assign.form;

import javax.validation.constraints.NotNull;

public class AssignQuestionForm {

    @NotNull
    private Long assignee;

    public AssignQuestionForm(Long assignee){
        this.assignee = assignee;
    }

    public Long getAssignee() {
        return assignee;
    }

    public void setAssignee(Long assignee) {
        this.assignee = assignee;
    }
}
