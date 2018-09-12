package org.innovateuk.ifs.application.forms.researchcategory.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Bean serves as a container for form parameters.
 */
public class ResearchCategoryForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long researchCategory;

    public Long getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(final Long researchCategory) {
        this.researchCategory = researchCategory;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ResearchCategoryForm that = (ResearchCategoryForm) o;

        return new EqualsBuilder()
                .append(researchCategory, that.researchCategory)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(researchCategory)
                .toHashCode();
    }
}