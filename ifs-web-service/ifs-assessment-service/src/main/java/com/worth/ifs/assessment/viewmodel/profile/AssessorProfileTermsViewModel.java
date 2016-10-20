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
    private String annexOne;
    private String annexTwo;
    private String annexThree;

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

    public String getAnnexOne() {
        return annexOne;
    }

    public void setAnnexOne(String annexOne) {
        this.annexOne = annexOne;
    }

    public String getAnnexTwo() {
        return annexTwo;
    }

    public void setAnnexTwo(String annexTwo) {
        this.annexTwo = annexTwo;
    }

    public String getAnnexThree() {
        return annexThree;
    }

    public void setAnnexThree(String annexThree) {
        this.annexThree = annexThree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileTermsViewModel that = (AssessorProfileTermsViewModel) o;

        return new EqualsBuilder()
                .append(currentAgreement, that.currentAgreement)
                .append(contractSignedDate, that.contractSignedDate)
                .append(text, that.text)
                .append(annexOne, that.annexOne)
                .append(annexTwo, that.annexTwo)
                .append(annexThree, that.annexThree)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(currentAgreement)
                .append(contractSignedDate)
                .append(text)
                .append(annexOne)
                .append(annexTwo)
                .append(annexThree)
                .toHashCode();
    }
}