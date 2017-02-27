package org.innovateuk.ifs.project.notes.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksNotesAddNoteForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.notesandqueries.note.required}")
    @Size(max = FinanceChecksNotesFormConstraints.MAX_NOTE_CHARACTERS, message = "{validation.notesandqueries.note.character.length.max}")
    @WordCount(max = FinanceChecksNotesFormConstraints.MAX_NOTE_WORDS, message = "{validation.notesandqueries.note.word.length.max}")
    private String note;

    @NotBlank(message = "{validation.notesandqueries.note.title.required}")
    @Size(max = FinanceChecksNotesFormConstraints.MAX_TITLE_CHARACTERS, message = "{validation.notesandqueries.note.title.length.max}")
    private String noteTitle;

    private MultipartFile attachment;

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this. note = note;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String title) {
        this.noteTitle = title;
    }

}
