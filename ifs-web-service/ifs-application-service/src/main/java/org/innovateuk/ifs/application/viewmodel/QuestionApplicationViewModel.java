package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Set;

/**
 * ViewModel for the application part of the question
 */
public class QuestionApplicationViewModel {

    private Set<Long> markedAsComplete;
    private Boolean allReadOnly;
    private OrganisationResource leadOrganisation;
    private ApplicationResource currentApplication;
    private CompetitionResource currentCompetition;
    private OrganisationResource userOrganisation;
    private List<ResearchCategoryResource> researchCategories;
    private Long researchCategoryId;
    private boolean hasApplicationFinances;
    private String selectedInnovationAreaName;

    public QuestionApplicationViewModel(Set<Long> markedAsComplete, Boolean allReadOnly, ApplicationResource currentApplication,
                                        CompetitionResource competitionResource, OrganisationResource userOrganisation) {
        this.markedAsComplete = markedAsComplete;
        this.allReadOnly = allReadOnly;
        this.currentApplication = currentApplication;
        this.currentCompetition = competitionResource;
        this.userOrganisation = userOrganisation;
    }

    public void setLeadOrganisation(OrganisationResource leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public Set<Long> getMarkedAsComplete() {
        return markedAsComplete;
    }

    public Boolean getAllReadOnly() {
        return allReadOnly;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public OrganisationResource getUserOrganisation() {
        return userOrganisation;
    }

    public Boolean getApplicationIsClosed() {
        return !currentCompetition.isOpen() || !currentApplication.isOpen();
    }

    public Boolean getApplicationIsReadOnly() {
        return !currentCompetition.isOpen() || !currentApplication.isOpen();
    }

    public List<ResearchCategoryResource> getResearchCategories() {
        return researchCategories;
    }

    public void setResearchCategories(List<ResearchCategoryResource> researchCategories) {
        this.researchCategories = researchCategories;
    }

    public Long getResearchCategoryId() {
        return researchCategoryId;
    }

    public void setResearchCategoryId(Long researchCategoryId) {
        this.researchCategoryId = researchCategoryId;
    }

    public boolean getHasApplicationFinances() {
        return hasApplicationFinances;
    }

    public void setHasApplicationFinances(boolean hasApplicationFinances) {
        this.hasApplicationFinances = hasApplicationFinances;
    }

    public String getSelectedInnovationAreaName() {
        return selectedInnovationAreaName;
    }

    public void setSelectedInnovationAreaName(String selectedInnovationAreaName) {
        this.selectedInnovationAreaName = selectedInnovationAreaName;
    }
}
