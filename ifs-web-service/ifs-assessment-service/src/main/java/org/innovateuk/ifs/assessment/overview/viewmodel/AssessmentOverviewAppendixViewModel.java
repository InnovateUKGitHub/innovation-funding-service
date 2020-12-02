package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for appendices displayed within the Assessment Overview view.
 */
public class AssessmentOverviewAppendixViewModel {

    private final long formInputId;
    private final long fileEntryId;
    private final String title;
    private final String name;
    private final String size;

    public AssessmentOverviewAppendixViewModel(long formInputId, long fileEntryId, String title, String name, String size) {
        this.formInputId = formInputId;
        this.fileEntryId = fileEntryId;
        this.title = title;
        this.name = name;
        this.size = size;
    }

    public long getFormInputId() {
        return formInputId;
    }

    public long getFileEntryId() {
        return fileEntryId;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentOverviewAppendixViewModel that = (AssessmentOverviewAppendixViewModel) o;

        return new EqualsBuilder()
                .append(formInputId, that.formInputId)
                .append(title, that.title)
                .append(name, that.name)
                .append(size, that.size)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(formInputId)
                .append(title)
                .append(name)
                .append(size)
                .toHashCode();
    }
}