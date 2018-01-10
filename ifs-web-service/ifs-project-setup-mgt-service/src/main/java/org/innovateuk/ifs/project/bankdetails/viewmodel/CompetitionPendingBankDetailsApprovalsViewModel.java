package org.innovateuk.ifs.project.bankdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;

import java.util.List;

/**
 * View model to support the view for displaying organisations for which Bank Details Approval is pending
 */
public class CompetitionPendingBankDetailsApprovalsViewModel {

    private List<BankDetailsReviewResource> pendingBankDetails;

    public CompetitionPendingBankDetailsApprovalsViewModel() {
    }

    public CompetitionPendingBankDetailsApprovalsViewModel(List<BankDetailsReviewResource> pendingBankDetails) {
        this.pendingBankDetails = pendingBankDetails;
    }

    public List<BankDetailsReviewResource> getPendingBankDetails() {
        return pendingBankDetails;
    }

    public void setPendingBankDetails(List<BankDetailsReviewResource> pendingBankDetails) {
        this.pendingBankDetails = pendingBankDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionPendingBankDetailsApprovalsViewModel that = (CompetitionPendingBankDetailsApprovalsViewModel) o;

        return new EqualsBuilder()
                .append(pendingBankDetails, that.pendingBankDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pendingBankDetails)
                .toHashCode();
    }
}

