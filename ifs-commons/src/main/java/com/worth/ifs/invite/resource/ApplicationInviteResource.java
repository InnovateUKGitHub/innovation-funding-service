package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.constant.InviteStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A DTO which enables to application to transfer {@link com.worth.ifs.invite.domain.ApplicationInvite} entities.
 */
public class ApplicationInviteResource extends InviteResource {
    private String leadOrganisation;
    private String leadApplicant;
    private String leadApplicantEmail;
    private Long id;
    private Long user;
    private String name;
    private String nameConfirmed;
    private String email;
    private Long application;
    private Long competitionId;
    private String competitionName;
    private String applicationName;
    private Long inviteOrganisation;
    private String inviteOrganisationName;
    private String inviteOrganisationNameConfirmed;
    private String hash;
    private InviteStatus status;

    public ApplicationInviteResource() {
    	// no-arg constructor
    }


    public ApplicationInviteResource(Long id, String name, String email, Long application, Long inviteOrganisation, String hash, InviteStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.application = application;
        this.inviteOrganisation = inviteOrganisation;
        this.hash = hash;
        this.status = status;
    }

    public ApplicationInviteResource(String name, String email, Long application) {
        this.name = name;
        this.email = email;
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getInviteOrganisation() {
        return inviteOrganisation;
    }

    public void setInviteOrganisation(Long inviteOrganisation) {
        this.inviteOrganisation = inviteOrganisation;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public String getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(String leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public String getNameConfirmed() {
        return this.nameConfirmed;
    }

    public void setNameConfirmed(String nameConfirmed) {
        this.nameConfirmed = nameConfirmed;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getInviteOrganisationName() {
        return inviteOrganisationName;
    }

    public void setInviteOrganisationName(String inviteOrganisationName) {
        this.inviteOrganisationName = inviteOrganisationName;
    }

    public String getLeadApplicantEmail() {
        return leadApplicantEmail;
    }

    public void setLeadApplicantEmail(String leadApplicantEmail) {
        this.leadApplicantEmail = leadApplicantEmail;
    }

    public String getInviteOrganisationNameConfirmed() {
        return inviteOrganisationNameConfirmed;
    }

    public void setInviteOrganisationNameConfirmed(String inviteOrganisationNameConfirmed) {
        this.inviteOrganisationNameConfirmed = inviteOrganisationNameConfirmed;
    }

    public String getInviteOrganisationNameConfirmedSafe() {
        return StringUtils.isBlank(getInviteOrganisationNameConfirmed()) ? getInviteOrganisationName() : getInviteOrganisationNameConfirmed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationInviteResource that = (ApplicationInviteResource) o;

        return new EqualsBuilder()
                .append(leadOrganisation, that.leadOrganisation)
                .append(leadApplicant, that.leadApplicant)
                .append(leadApplicantEmail, that.leadApplicantEmail)
                .append(id, that.id)
                .append(user, that.user)
                .append(name, that.name)
                .append(nameConfirmed, that.nameConfirmed)
                .append(email, that.email)
                .append(application, that.application)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(applicationName, that.applicationName)
                .append(inviteOrganisation, that.inviteOrganisation)
                .append(inviteOrganisationName, that.inviteOrganisationName)
                .append(inviteOrganisationNameConfirmed, that.inviteOrganisationNameConfirmed)
                .append(hash, that.hash)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(leadOrganisation)
                .append(leadApplicant)
                .append(leadApplicantEmail)
                .append(id)
                .append(user)
                .append(name)
                .append(nameConfirmed)
                .append(email)
                .append(application)
                .append(competitionId)
                .append(competitionName)
                .append(applicationName)
                .append(inviteOrganisation)
                .append(inviteOrganisationName)
                .append(inviteOrganisationNameConfirmed)
                .append(hash)
                .append(status)
                .toHashCode();
    }
}
