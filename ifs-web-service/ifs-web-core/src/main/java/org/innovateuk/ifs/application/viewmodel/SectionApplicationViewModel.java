package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Shared Application viewModel used by {@link BaseSectionViewModel}
 */
public class SectionApplicationViewModel {
    private Future<Set<Long>> markedAsComplete;
    private Boolean allReadOnly;
    private ApplicationResource currentApplication;
    private CompetitionResource currentCompetition;
    private OrganisationResource userOrganisation;

    public Set<Long> getMarkedAsComplete() throws ExecutionException, InterruptedException {
        return markedAsComplete.get();
    }

    public void setMarkedAsComplete(Future<Set<Long>> markedAsComplete) {
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
