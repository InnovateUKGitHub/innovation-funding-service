package org.innovateuk.ifs.application.common.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ApplicationTermsPartnerRowViewModel {
    private final String name;
    private final boolean lead;
    private final boolean termsAccepted;

    public ApplicationTermsPartnerRowViewModel(String name, boolean lead, boolean termsAccepted) {
        this.name = name;
        this.lead = lead;
        this.termsAccepted = termsAccepted;
    }

    public String getName() {
        return name;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationTermsPartnerRowViewModel that = (ApplicationTermsPartnerRowViewModel) o;

        return new EqualsBuilder()
                .append(isLead(), that.isLead())
                .append(isTermsAccepted(), that.isTermsAccepted())
                .append(getName(), that.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(isLead())
                .append(isTermsAccepted())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ApplicationTermsPartnerRowViewModel{" +
                "name='" + name + '\'' +
                ", lead=" + lead +
                ", termsAccepted=" + termsAccepted +
                '}';
    }
}