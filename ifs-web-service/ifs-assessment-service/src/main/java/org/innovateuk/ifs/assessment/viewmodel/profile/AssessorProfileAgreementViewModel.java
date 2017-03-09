package org.innovateuk.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

/**
 * Holder of model attributes for the Assessor Profile Agreement view.
 */
public class AssessorProfileAgreementViewModel {

    private boolean currentAgreement;
    private LocalDateTime agreementSignedDate;
    private String text;

    public boolean isCurrentAgreement() {
        return currentAgreement;
    }

    public void setCurrentAgreement(boolean currentAgreement) {
        this.currentAgreement = currentAgreement;
    }

    public LocalDateTime getAgreementSignedDate() {
        return agreementSignedDate;
    }

    public void setAgreementSignedDate(LocalDateTime agreementSignedDate) {
        this.agreementSignedDate = agreementSignedDate;
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

        AssessorProfileAgreementViewModel that = (AssessorProfileAgreementViewModel) o;

        return new EqualsBuilder()
                .append(currentAgreement, that.currentAgreement)
                .append(agreementSignedDate, that.agreementSignedDate)
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(currentAgreement)
                .append(agreementSignedDate)
                .append(text)
                .toHashCode();
    }
}
