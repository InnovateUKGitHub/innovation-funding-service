package com.worth.ifs.project.bankdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChangeBankDetailsViewModel extends BankDetailsReviewViewModel{
    private boolean updated;

    public ChangeBankDetailsViewModel(Long projectId, Long applicationId, String projectNumber, String projectName, String financeContactName, String financeContactEmail, String financeContactPhoneNumber, Long organisationId, String organisationName, String registrationNumber, String bankAccountNumber, String sortCode, String organisationAddress, Boolean verified, Short companyNameScore, Boolean registrationNumberMatched, Short addressScore, Boolean approved, Boolean approvedManually, boolean updated) {
        super(projectId, applicationId, projectNumber, projectName, financeContactName, financeContactEmail, financeContactPhoneNumber, organisationId, organisationName, registrationNumber, bankAccountNumber, sortCode, organisationAddress, verified, companyNameScore, registrationNumberMatched, addressScore, approved, approvedManually);
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ChangeBankDetailsViewModel that = (ChangeBankDetailsViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(updated, that.updated)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(updated)
                .toHashCode();
    }
}
