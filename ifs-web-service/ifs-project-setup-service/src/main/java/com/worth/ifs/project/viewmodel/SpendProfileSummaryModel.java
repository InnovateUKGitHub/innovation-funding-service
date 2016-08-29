package com.worth.ifs.project.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * View model used to display financial year-wise summary below spend profile
 */
public class SpendProfileSummaryModel {
    List<SpendProfileSummaryYearModel> years;

    public SpendProfileSummaryModel(final List<SpendProfileSummaryYearModel> years) {
        this.years = years;
    }

    public List<SpendProfileSummaryYearModel> getYears() {
        return years;
    }

    @Override public boolean equals(final Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        final SpendProfileSummaryModel that = (SpendProfileSummaryModel) o;

        return new EqualsBuilder()
            .append(years, that.years)
            .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(years)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("years", years)
                .toString();
    }
}

