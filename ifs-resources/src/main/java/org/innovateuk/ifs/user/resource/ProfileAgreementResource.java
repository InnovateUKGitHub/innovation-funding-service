package org.innovateuk.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

/**
 * Profile Agreement Data Transfer Object
 */
public class ProfileAgreementResource {

    private Long user;
    private AgreementResource agreement;
    private boolean currentAgreement;
    private ZonedDateTime agreementSignedDate;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public AgreementResource getAgreement() {
        return agreement;
    }

    public void setAgreement(AgreementResource agreement) {
        this.agreement = agreement;
    }

    public boolean isCurrentAgreement() {
        return currentAgreement;
    }

    public void setCurrentAgreement(boolean currentAgreement) {
        this.currentAgreement = currentAgreement;
    }

    public ZonedDateTime getAgreementSignedDate() {
        return agreementSignedDate;
    }

    public void setAgreementSignedDate(ZonedDateTime agreementSignedDate) {
        this.agreementSignedDate = agreementSignedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProfileAgreementResource that = (ProfileAgreementResource) o;

        return new EqualsBuilder()
                .append(currentAgreement, that.currentAgreement)
                .append(user, that.user)
                .append(agreement, that.agreement)
                .append(agreementSignedDate, that.agreementSignedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(agreement)
                .append(currentAgreement)
                .append(agreementSignedDate)
                .toHashCode();
    }
}
