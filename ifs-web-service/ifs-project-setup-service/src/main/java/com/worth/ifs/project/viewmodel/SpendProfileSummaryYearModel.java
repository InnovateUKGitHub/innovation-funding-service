package com.worth.ifs.project.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SpendProfileSummaryYearModel {
    private int year;
    private String amount;

    public SpendProfileSummaryYearModel(final int year, final String amount) {
        this.year = year;
        this.amount = amount;
    }

    public int getYear() {
        return year;
    }

    public String getAmount() {
        return amount;
    }

    @Override public boolean equals(final Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        final SpendProfileSummaryYearModel that = (SpendProfileSummaryYearModel) o;

        return new EqualsBuilder()
            .append(year, that.year)
            .append(amount, that.amount)
            .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(year)
            .append(amount)
            .toHashCode();
    }
}
