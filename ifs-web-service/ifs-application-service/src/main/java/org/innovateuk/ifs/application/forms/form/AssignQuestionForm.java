package org.innovateuk.ifs.application.forms.form;

public class AssignQuestionForm {

    private Long assigneeId;

    public AssignQuestionForm(Long assigneeId){
        this.assigneeId = assigneeId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}
