package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

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
    private String selectedInnovationAreaName;
    private String selectedResearchCategoryName;
    private boolean noInnovationAreaApplicable;

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

    public String getSelectedInnovationAreaName() {
        return selectedInnovationAreaName;
    }

    public void setSelectedInnovationAreaName(String selectedInnovationAreaName) {
        this.selectedInnovationAreaName = selectedInnovationAreaName;
    }

    public String getSelectedResearchCategoryName() {
        return selectedResearchCategoryName;
    }

    public void setSelectedResearchCategoryName(String selectedResearchCategoryName) {
        this.selectedResearchCategoryName = selectedResearchCategoryName;
    }

    public boolean isNoInnovationAreaApplicable() {
        return noInnovationAreaApplicable;
    }

    public void setNoInnovationAreaApplicable(boolean noInnovationAreaApplicable) {
        this.noInnovationAreaApplicable = noInnovationAreaApplicable;
    }
}
