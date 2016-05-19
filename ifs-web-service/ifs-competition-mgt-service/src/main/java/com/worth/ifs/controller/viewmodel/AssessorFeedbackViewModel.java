package com.worth.ifs.controller.viewmodel;

/**
 * Holder of model attributes around the uploaded Assessor Feedback
 */
public class AssessorFeedbackViewModel {

    private boolean readonly;
    private boolean noFileUploaded;
    private String filename;

    private AssessorFeedbackViewModel(boolean readonly, boolean noFileUploaded, String filename) {
        this.readonly = readonly;
        this.noFileUploaded = noFileUploaded;
        this.filename = filename;
    }

    public static AssessorFeedbackViewModel withExistingFile(String filename, boolean readonly) {
        return new AssessorFeedbackViewModel(readonly, false, filename);
    }

    public static AssessorFeedbackViewModel withNoFile(boolean readonly) {
        return new AssessorFeedbackViewModel(readonly, true, null);
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isNoFileUploaded() {
        return noFileUploaded;
    }

    public String getFilename() {
        return filename;
    }
}
