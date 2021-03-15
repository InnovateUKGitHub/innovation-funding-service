package org.innovateuk.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;

public class PendingPartnerProgressResource {

    private ZonedDateTime yourOrganisationCompletedOn;
    private ZonedDateTime yourFundingCompletedOn;
    private ZonedDateTime termsAndConditionsCompletedOn;
    private ZonedDateTime completedOn;
    private ZonedDateTime subsidyBasisCompletedOn;
    private boolean subsidyBasisRequired;
    private boolean readyToJoinProject;

    public ZonedDateTime getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(ZonedDateTime completedOn) {
        this.completedOn = completedOn;
    }

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

    public void setSubsidyBasisCompletedOn(ZonedDateTime subsidyBasisCompletedOn) {
        this.subsidyBasisCompletedOn = subsidyBasisCompletedOn;
    }

    public ZonedDateTime getSubsidyBasisCompletedOn() {
        return subsidyBasisCompletedOn;
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

    @JsonIgnore
    public boolean isSubsidyBasisComplete() {
        return subsidyBasisCompletedOn != null;
    }

    @JsonIgnore
    public boolean isSubsidyBasisRequiredAndNotCompleted() {
        return isSubsidyBasisRequired() && !isSubsidyBasisComplete();
    }

    @JsonIgnore
    public boolean isCompleted() {
        return completedOn != null;
    }

    public boolean isReadyToJoinProject() {
        return readyToJoinProject;
    }

    public void setReadyToJoinProject(boolean readyToJoinProject) {
        this.readyToJoinProject = readyToJoinProject;
    }

    public boolean isSubsidyBasisRequired() {
        return subsidyBasisRequired;
    }

    public void setSubsidyBasisRequired(boolean subsidyBasisRequired) {
        this.subsidyBasisRequired = subsidyBasisRequired;
    }
}