package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * View model extending the {@link BaseSectionViewModel} for open sections (not finance, but used by finances overview)
 */
public class OpenSectionViewModel extends BaseSectionViewModel {
    private SortedSet<OrganisationResource> academicOrganisations;
    private SortedSet<OrganisationResource> applicationOrganisations;
    private List<String> pendingOrganisationNames;
    private OrganisationResource leadOrganisation;

    private Map<Long, Set<Long>> completedSectionsByOrganisation;
    private Set<Long> sectionsMarkedAsComplete;
    private Boolean allQuestionsCompleted;
    private Long eachCollaboratorFinanceSectionId;

    private Integer completedQuestionsPercentage;

    public OpenSectionViewModel() {
        subFinanceSection = Boolean.FALSE;
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

    public Map<Long, Set<Long>> getCompletedSectionsByOrganisation() {
        return completedSectionsByOrganisation;
    }

    public void setCompletedSectionsByOrganisation(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        this.completedSectionsByOrganisation = completedSectionsByOrganisation;
    }

    public Set<Long> getSectionsMarkedAsComplete() {
        return sectionsMarkedAsComplete;
    }

    public void setSectionsMarkedAsComplete(Set<Long> sectionsMarkedAsComplete) {
        this.sectionsMarkedAsComplete = sectionsMarkedAsComplete;
    }

    public Boolean getAllQuestionsCompleted() {
        return allQuestionsCompleted;
    }

    public void setAllQuestionsCompleted(Boolean allQuestionsCompleted) {
        this.allQuestionsCompleted = allQuestionsCompleted;
    }

    public Long getEachCollaboratorFinanceSectionId() {
        return eachCollaboratorFinanceSectionId;
    }

    public void setEachCollaboratorFinanceSectionId(Long eachCollaboratorFinanceSectionId) {
        this.eachCollaboratorFinanceSectionId = eachCollaboratorFinanceSectionId;
    }

    public Integer getCompletedQuestionsPercentage() {
        return completedQuestionsPercentage;
    }

    public void setCompletedQuestionsPercentage(Integer completedQuestionsPercentage) {
        this.completedQuestionsPercentage = completedQuestionsPercentage;
    }
}
