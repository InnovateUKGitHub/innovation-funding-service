package org.innovateuk.ifs.project.projectteam.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Represents a pending organisation joining a project.
 */
@Entity
public class PendingPartnerProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_organisation_id", referencedColumnName = "id", nullable = false)
    private PartnerOrganisation partnerOrganisation;

    private ZonedDateTime yourOrganisationCompletedOn;
    private ZonedDateTime yourFundingCompletedOn;
    private ZonedDateTime termsAndConditionsCompletedOn;
    private ZonedDateTime completedOn;

    public PendingPartnerProgress() {}

    public PendingPartnerProgress(PartnerOrganisation partnerOrganisation) {
        this.partnerOrganisation = partnerOrganisation;
    }

    public Long getId() {
        return id;
    }

    public PartnerOrganisation getPartnerOrganisation() {
        return partnerOrganisation;
    }

    public ZonedDateTime getYourOrganisationCompletedOn() {
        return yourOrganisationCompletedOn;
    }

    public ZonedDateTime getYourFundingCompletedOn() {
        return yourFundingCompletedOn;
    }

    public ZonedDateTime getTermsAndConditionsCompletedOn() {
        return termsAndConditionsCompletedOn;
    }

    public ZonedDateTime getCompletedOn() {
        return completedOn;
    }

    public void markYourOrganisationComplete() {
        yourOrganisationCompletedOn = ZonedDateTime.now();
    }

    public void markYourFundingComplete() {
        yourFundingCompletedOn = ZonedDateTime.now();
    }

    public void markTermsAndConditionsComplete() {
        termsAndConditionsCompletedOn = ZonedDateTime.now();
    }

    public void complete() {
        this.completedOn = ZonedDateTime.now();
    }

    public void markYourOrganisationIncomplete() {
        yourOrganisationCompletedOn = null;
    }

    public void markYourFundingIncomplete() {
        yourFundingCompletedOn = null;
    }

    public void markTermsAndConditionsIncomplete() {
        termsAndConditionsCompletedOn = null;
    }

    public boolean isYourOrganisationComplete() {
        return yourOrganisationCompletedOn != null;
    }

    public boolean isYourFundingComplete() {
        return yourFundingCompletedOn != null;
    }

    public boolean isTermsAndConditionsComplete() {
        return termsAndConditionsCompletedOn != null;
    }

    public boolean isYourOrganisationRequired() {
        PartnerOrganisation partnerOrganisation = getPartnerOrganisation();
        Competition competition = partnerOrganisation.getProject().getApplication().getCompetition();
        Organisation organisation =  partnerOrganisation.getOrganisation();
        return competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum());
    }

    public boolean isReadyToJoinProject() {
        return (isYourOrganisationComplete() || isYourOrganisationRequired()) &&
                isYourFundingComplete() &&
                isTermsAndConditionsComplete() &&
                !isComplete();
    }

    public boolean isComplete() {
        return completedOn != null;
    }
}
