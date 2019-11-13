package org.innovateuk.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;

public class PendingPartnerProgressResource {

    private ZonedDateTime yourOrganisationCompletedOn;
    private ZonedDateTime yourFundingCompletedOn;
    private ZonedDateTime termsAndConditionsCompletedOn;

    public ZonedDateTime getYourOrganisationCompletedOn() {
        return yourOrganisationCompletedOn;
    }

    public void setYourOrganisationCompletedOn(ZonedDateTime yourOrganisationCompletedOn) {
        this.yourOrganisationCompletedOn = yourOrganisationCompletedOn;
    }

    public ZonedDateTime getYourFundingCompletedOn() {
        return yourFundingCompletedOn;
    }

    public void setYourFundingCompletedOn(ZonedDateTime yourFundingCompletedOn) {
        this.yourFundingCompletedOn = yourFundingCompletedOn;
    }

    public ZonedDateTime getTermsAndConditionsCompletedOn() {
        return termsAndConditionsCompletedOn;
    }

    public void setTermsAndConditionsCompletedOn(ZonedDateTime termsAndConditionsCompletedOn) {
        this.termsAndConditionsCompletedOn = termsAndConditionsCompletedOn;
    }

    @JsonIgnore
    public boolean isYourOrganisationComplete() {
        return yourOrganisationCompletedOn != null;
    }

    @JsonIgnore
    public boolean isYourFundingComplete() {
        return yourFundingCompletedOn != null;
    }

    @JsonIgnore
    public boolean isTermsAndConditionsComplete() {
        return termsAndConditionsCompletedOn != null;
    }
}