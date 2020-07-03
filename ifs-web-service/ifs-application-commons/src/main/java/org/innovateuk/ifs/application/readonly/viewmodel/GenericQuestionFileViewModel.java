package org.innovateuk.ifs.application.readonly.viewmodel;

public class GenericQuestionFileViewModel {

    private final long fileEntryId;
    private final String filename;
    private final String url;

    public GenericQuestionFileViewModel(long fileEntryId, String filename, String url) {
        this.fileEntryId = fileEntryId;
        this.filename = filename;
        this.url = url;
    }

    public long getFileEntryId() {
        return fileEntryId;
    }

    public String getFilename() {
        return filename;
    }

    public String getUrl() {
        return url;
    }
}
