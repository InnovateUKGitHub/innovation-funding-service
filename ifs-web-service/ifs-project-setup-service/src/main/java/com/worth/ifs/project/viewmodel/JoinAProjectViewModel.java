package com.worth.ifs.project.viewmodel;


import com.worth.ifs.organisation.resource.OrganisationAddressResource;

public class JoinAProjectViewModel {
    private String projectName;
    private String competitionName;
    private String leadOrganisationName;
    private String leadApplicantName;
    private String organisationName;
    private String organisationResgistrationNumber;
    private OrganisationAddressResource organisationAddress;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public void setLeadOrganisationName(String leadOrganisationName) {
        this.leadOrganisationName = leadOrganisationName;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public void setLeadApplicantName(String leadApplicantName) {
        this.leadApplicantName = leadApplicantName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationResgistrationNumber() {
        return organisationResgistrationNumber;
    }

    public void setOrganisationResgistrationNumber(String organisationResgistrationNumber) {
        this.organisationResgistrationNumber = organisationResgistrationNumber;
    }

    public OrganisationAddressResource getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(OrganisationAddressResource organisationAddress) {
        this.organisationAddress = organisationAddress;
    }
}
