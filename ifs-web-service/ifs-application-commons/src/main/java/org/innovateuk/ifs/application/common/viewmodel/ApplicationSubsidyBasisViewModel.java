package org.innovateuk.ifs.application.common.viewmodel;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ApplicationSubsidyBasisViewModel {

    private final List<ApplicationSubsidyBasisPartnerRowViewModel> partners;

    public ApplicationSubsidyBasisViewModel(List<ApplicationSubsidyBasisPartnerRowViewModel> partners) {
        this.partners = partners;
    }

    public boolean isSubsidyBasisCompletedByAllOrganisations(){
        return partners.stream().allMatch(partner -> partner.isQuestionnaireMarkedAsComplete());
    }

    public List<ApplicationSubsidyBasisPartnerRowViewModel> getPartners() {
        return partners;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationSubsidyBasisViewModel that = (ApplicationSubsidyBasisViewModel) o;

        return new EqualsBuilder()
                .append(partners, that.partners)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(partners)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ApplicationSubsidyBasisViewModel{" +
                "partners=" + partners +
                '}';
    }
}