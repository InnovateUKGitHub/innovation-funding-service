package org.innovateuk.ifs.project.notes.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksNotesAddCommentForm extends BaseBindingResultTarget {
    @NotBlank(message = "{validation.notesandqueries.comment.required}")
    @Size(max = FinanceChecksNotesFormConstraints.MAX_NOTE_CHARACTERS, message = "{validation.notesandqueries.comment.character.length.max}")
    @WordCount(max = FinanceChecksNotesFormConstraints.MAX_NOTE_WORDS, message = "{validation.notesandqueries.comment.word.length.max}")
    private String comment;

    private MultipartFile attachment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }

}
