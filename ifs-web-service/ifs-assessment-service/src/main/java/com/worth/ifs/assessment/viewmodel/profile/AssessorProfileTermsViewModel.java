package com.worth.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

/**
 * Holder of model attributes for the Contract Terms view.
 */
public class AssessorProfileTermsViewModel {

    private boolean currentAgreement;
    private LocalDateTime contractSignedDate;
    private String text;

    public boolean isCurrentAgreement() {
        return currentAgreement;
    }

    public void setCurrentAgreement(boolean currentAgreement) {
        this.currentAgreement = currentAgreement;
    }

    public LocalDateTime getContractSignedDate() {
        return contractSignedDate;
    }

    public void setContractSignedDate(LocalDateTime contractSignedDate) {
        this.contractSignedDate = contractSignedDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorProfileTermsViewModel that = (AssessorProfileTermsViewModel) o;

        return new EqualsBuilder()
                .append(currentAgreement, that.currentAgreement)
                .append(contractSignedDate, that.contractSignedDate)
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(currentAgreement)
                .append(contractSignedDate)
                .append(text)
                .toHashCode();
    }
}