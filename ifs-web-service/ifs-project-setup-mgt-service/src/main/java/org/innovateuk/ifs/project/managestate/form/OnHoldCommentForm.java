package org.innovateuk.ifs.project.managestate.form;

import org.innovateuk.ifs.commons.validation.constraints.WordCount;

import javax.validation.constraints.NotBlank;

public class OnHoldCommentForm {

    @NotBlank(message = "{validation.manage.project.on.hold.details.required}")
    @WordCount(max = 400, message = "{validation.field.max.word.count}")
    private String details;

    private long commentId;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}
