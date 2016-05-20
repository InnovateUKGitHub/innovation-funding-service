package com.worth.ifs.controller.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorFeedbackViewModel that = (AssessorFeedbackViewModel) o;

        return new EqualsBuilder()
                .append(readonly, that.readonly)
                .append(noFileUploaded, that.noFileUploaded)
                .append(filename, that.filename)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(readonly)
                .append(noFileUploaded)
                .append(filename)
                .toHashCode();
    }
}
