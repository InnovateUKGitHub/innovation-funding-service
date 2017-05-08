package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.section.AbstractSectionViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.Set;

/**
 * Shared Application viewModel used by {@link AbstractSectionViewModel}
 */
public class SectionApplicationViewModel {
    private Set<Long> markedAsComplete;
    private Boolean allReadOnly;
    private ApplicationResource currentApplication;
    private CompetitionResource currentCompetition;
    private OrganisationResource userOrganisation;

    public Set<Long> getMarkedAsComplete() {
        return markedAsComplete;
    }

    public void setMarkedAsComplete(Set<Long> markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    public Boolean getAllReadOnly() {
        return allReadOnly;
    }

    public void setAllReadOnly(Boolean allReadOnly) {
        this.allReadOnly = allReadOnly;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public void setCurrentApplication(ApplicationResource currentApplication) {
        this.currentApplication = currentApplication;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public void setCurrentCompetition(CompetitionResource currentCompetition) {
        this.currentCompetition = currentCompetition;
    }

    public OrganisationResource getUserOrganisation() {
        return userOrganisation;
    }

    public void setUserOrganisation(OrganisationResource userOrganisation) {
        this.userOrganisation = userOrganisation;
    }

    public Boolean getApplicationIsClosed() {
        return !currentCompetition.isOpen() || !currentApplication.isOpen();
    }

    public Boolean getApplicationIsReadOnly() {
        return !currentCompetition.isOpen() || !currentApplication.isOpen();
    }
}
