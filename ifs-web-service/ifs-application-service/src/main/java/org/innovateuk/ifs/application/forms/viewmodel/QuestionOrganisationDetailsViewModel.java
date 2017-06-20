package org.innovateuk.ifs.application.forms.viewmodel;

import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.SortedSet;

/**
 * ViewModel for question Organisation details
 */
public class QuestionOrganisationDetailsViewModel {
    private SortedSet<OrganisationResource> academicOrganisations;
    private SortedSet<OrganisationResource> applicationOrganisations;
    private List<String> pendingOrganisationNames;
    private OrganisationResource leadOrganisation;

    public QuestionOrganisationDetailsViewModel() {
    }

    public QuestionOrganisationDetailsViewModel(SortedSet<OrganisationResource> academicOrganisations, SortedSet<OrganisationResource> applicationOrganisations, List<String> pendingOrganisationNames, OrganisationResource leadOrganisation) {
        this.academicOrganisations = academicOrganisations;
        this.applicationOrganisations = applicationOrganisations;
        this.pendingOrganisationNames = pendingOrganisationNames;
        this.leadOrganisation = leadOrganisation;
    }

    public SortedSet<OrganisationResource> getAcademicOrganisations() {
        return academicOrganisations;
    }

    public void setAcademicOrganisations(SortedSet<OrganisationResource> academicOrganisations) {
        this.academicOrganisations = academicOrganisations;
    }

    public SortedSet<OrganisationResource> getApplicationOrganisations() {
        return applicationOrganisations;
    }

    public void setApplicationOrganisations(SortedSet<OrganisationResource> applicationOrganisations) {
        this.applicationOrganisations = applicationOrganisations;
    }

    public List<String> getPendingOrganisationNames() {
        return pendingOrganisationNames;
    }

    public void setPendingOrganisationNames(List<String> pendingOrganisationNames) {
        this.pendingOrganisationNames = pendingOrganisationNames;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(OrganisationResource leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }
}