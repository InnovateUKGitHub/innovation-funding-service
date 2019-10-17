package org.innovateuk.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;

import javax.validation.constraints.Digits;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.project.resource.ProjectState.COMPLETED_STATES;

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
}