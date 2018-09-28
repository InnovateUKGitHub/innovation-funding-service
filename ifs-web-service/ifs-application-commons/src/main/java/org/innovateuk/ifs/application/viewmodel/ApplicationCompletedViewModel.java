package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * View model for the application overview - users
 */
public class ApplicationCompletedViewModel {
    private Set<Long> sectionsMarkedAsComplete;
    private Future<Set<Long>> markedAsComplete;
    private Set<Long> completedSectionsByUserOrganisation;
    private boolean financeSectionComplete;
    private boolean financeOverviewSectionComplete;

    public ApplicationCompletedViewModel(Set<Long> sectionsMarkedAsComplete,
                                         Future<Set<Long>> markedAsComplete,
                                         Set<Long> completedSectionsByUserOrganisation,
                                         boolean financeSectionComplete,
                                         boolean financeOverviewSectionComplete) {
        this.sectionsMarkedAsComplete = sectionsMarkedAsComplete;
        this.markedAsComplete = markedAsComplete;
        this.completedSectionsByUserOrganisation = completedSectionsByUserOrganisation;
        this.financeSectionComplete = financeSectionComplete;
        this.financeOverviewSectionComplete = financeOverviewSectionComplete;
    }

    public Set<Long> getSectionsMarkedAsComplete() {
        return sectionsMarkedAsComplete;
    }

    public Set<Long> getMarkedAsComplete() throws ExecutionException, InterruptedException {
        return markedAsComplete.get();
    }

    public Boolean completedOrMarkedAsComplete(QuestionResource questionResource, SectionResource sectionResource) throws ExecutionException, InterruptedException {
        return (questionResource.isMarkAsCompletedEnabled() && getMarkedAsComplete().contains(questionResource.getId()))
                || completedSectionsByUserOrganisation.contains(sectionResource.getId());
    }

    public boolean isFinanceSectionComplete() {
        return financeSectionComplete;
    }

    public boolean isFinanceOverviewSectionComplete() {
        return financeOverviewSectionComplete;
    }
}
