package org.innovateuk.ifs.application.forms.questions.generic.viewmodel;

public class GenericQuestionAppendix {

    private final long fileEntryId;
    private final String filename;

    public GenericQuestionAppendix(long fileEntryId, String filename) {
        this.fileEntryId = fileEntryId;
        this.filename = filename;
    }

    public long getFileEntryId() {
        return fileEntryId;
    }

    public String getFilename() {
        return filename;
    }
}
