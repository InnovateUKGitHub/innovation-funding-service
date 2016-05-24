package com.worth.ifs.controller.viewmodel;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

/**
 * Holder of model attributes around the uploaded Assessor Feedback
 */
public class AssessorFeedbackViewModel {

    private FileDetailsViewModel fileDetails;
    private boolean readonly;

    private AssessorFeedbackViewModel(boolean readonly, String filename, Long filesizeBytes) {
        this(readonly, new FileDetailsViewModel(filename, filesizeBytes));
    }

    private AssessorFeedbackViewModel(boolean readonly, FileDetailsViewModel fileDetails) {
        this.readonly = readonly;
        this.fileDetails = fileDetails;
    }

    public static AssessorFeedbackViewModel withExistingFile(FileEntryResource fileEntry, boolean readonly) {
        return withExistingFile(fileEntry.getName(), fileEntry.getFilesizeBytes(), readonly);
    }

    public static AssessorFeedbackViewModel withExistingFile(String filename, long filesizeBytes, boolean readonly) {
        return new AssessorFeedbackViewModel(readonly, filename, filesizeBytes);
    }

    public static AssessorFeedbackViewModel withNoFile(boolean readonly) {
        return new AssessorFeedbackViewModel(readonly, null);
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isFileUploaded() {
        return fileDetails != null;
    }

    public String getFilename() {
        return fileDetails.getFilename();
    }

    public BigDecimal getFilesizeKbytes() {
        return fileDetails.getFilesizeKbytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorFeedbackViewModel that = (AssessorFeedbackViewModel) o;

        return new EqualsBuilder()
                .append(readonly, that.readonly)
                .append(fileDetails, that.fileDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fileDetails)
                .append(readonly)
                .toHashCode();
    }
}
