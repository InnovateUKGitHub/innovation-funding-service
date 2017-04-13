package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for appendices displayed within the Assessment Overview view.
 */
public class AssessmentOverviewAppendixViewModel {

    private long formInputId;
    private String title;
    private String name;
    private String size;

    public AssessmentOverviewAppendixViewModel(long formInputId, String title, String name, String size) {
        this.formInputId = formInputId;
        this.title = title;
        this.name = name;
        this.size = size;
    }

    public long getFormInputId() {
        return formInputId;
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