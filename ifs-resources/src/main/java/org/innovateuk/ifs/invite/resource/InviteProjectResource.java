package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;

/**
 * DTO to transfer Project Invite related Entities
 */
public class InviteProjectResource extends InviteResource {

    private Long id;
    private Long user;
    private String name;
    private String nameConfirmed;
    private String email;
    private Long project;
    private Long applicationId;
    private Long organisation;
    private String projectName;
    private String hash;
    private InviteStatus status;
    private Long leadOrganisationId;
    private String leadOrganisation;
    private String organisationName;
    private String leadApplicant;
    private String competitionName;


    public InviteProjectResource() {
        // no-arg constructor
    }

    public InviteProjectResource(Long id, Long user, String name, String email, Long project, Long organisation, Long applicationId, String hash, InviteStatus status, String leadApplicant, String competitionName) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.email = email;
        this.project = project;
        this.organisation = organisation;
        this.applicationId  = applicationId;
        this.hash = hash;
        this.status = status;
        this.competitionName = competitionName;
        this.leadApplicant = leadApplicant;
    }

    public InviteProjectResource(String name, String email, Long project) {
        this.name = name;
        this.email = email;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameConfirmed() {
        return nameConfirmed;
    }

    public void setNameConfirmed(String nameConfirmed) {
        this.nameConfirmed = nameConfirmed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public Long getApplicationId() {
        return applicationId; }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId; }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Long getLeadOrganisationId() {
        return leadOrganisationId;
    }

    public void setLeadOrganisationId(Long leadOrganisationId) {
        this.leadOrganisationId = leadOrganisationId;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(String leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InviteProjectResource that = (InviteProjectResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(user, that.user)
                .append(name, that.name)
                .append(nameConfirmed, that.nameConfirmed)
                .append(email, that.email)
                .append(project, that.project)
                .append(applicationId, that.applicationId)
                .append(organisation, that.organisation)
                .append(organisationName, that.organisationName)
                .append(projectName, that.projectName)
                .append(hash, that.hash)
                .append(status, that.status)
                .append(leadOrganisationId, that.leadOrganisationId)
                .append(leadOrganisation, that.leadOrganisation)
                .append(leadApplicant, that.leadApplicant)
                .append(competitionName, that.competitionName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(user)
                .append(name)
                .append(nameConfirmed)
                .append(email)
                .append(project)
                .append(applicationId)
                .append(organisation)
                .append(organisationName)
                .append(projectName)
                .append(hash)
                .append(status)
                .append(leadOrganisationId)
                .append(leadOrganisation)
                .append(leadApplicant)
                .append(competitionName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("user", user)
                .append("name", name)
                .append("nameConfirmed", nameConfirmed)
                .append("email", email)
                .append("project", project)
                .append("applicationId", applicationId)
                .append("organisation", organisation)
                .append("organisationName", organisationName)
                .append("projectName", projectName)
                .append("hash", hash)
                .append("status", status)
                .append("leadOrganisationId", leadOrganisationId)
                .append("leadOrganisation", leadOrganisation)
                .append("leadApplicant", leadApplicant)
                .append("competitionName", competitionName)
                .toString();
    }
}
