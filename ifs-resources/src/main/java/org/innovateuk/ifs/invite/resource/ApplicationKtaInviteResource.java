package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import java.time.ZonedDateTime;

public class ApplicationKtaInviteResource extends InviteResource {
    private Long id;
    private Long leadOrganisationId;
    private String email;
    private String name;
    private String applicationName;
    private String competitionName;
    private String leadApplicant;
    private String leadOrganisationName;
    private Long application;
    private InviteStatus status;
    private ZonedDateTime sentOn;
    private String hash;

    public ApplicationKtaInviteResource(String email, Long application) {
        this.email = email;
        this.application = application;
    }

    public ApplicationKtaInviteResource() {
        // no-arg constructor
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    public ZonedDateTime getSentOn() {
        return sentOn;
    }

    public void setSentOn(ZonedDateTime sentOn) {
        this.sentOn = sentOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(String leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public void setLeadOrganisationName(String leadOrganisationName) {
        this.leadOrganisationName = leadOrganisationName;
    }

    public Long getLeadOrganisationId() {
        return leadOrganisationId;
    }

    public void setLeadOrganisationId(Long leadOrganisationId) {
        this.leadOrganisationId = leadOrganisationId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationKtaInviteResource that = (ApplicationKtaInviteResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(leadOrganisationId, that.leadOrganisationId)
                .append(email, that.email)
                .append(name, that.name)
                .append(applicationName, that.applicationName)
                .append(competitionName, that. competitionName)
                .append(leadApplicant, that.leadApplicant)
                .append(leadOrganisationName, that.leadOrganisationName)
                .append(application, that.application)
                .append(status, that.status)
                .append(sentOn, that.sentOn)
                .append(hash, that.hash)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(leadOrganisationId)
                .append(email)
                .append(name)
                .append(applicationName)
                .append(competitionName)
                .append(leadApplicant)
                .append(leadOrganisationName)
                .append(application)
                .append(status)
                .append(sentOn)
                .append(hash)
                .toHashCode();
    }
}
