package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * View model for the application overview - users
 */
public class ApplicationOverviewCompletedViewModel {
    private Set<Long> sectionsMarkedAsComplete;
    private Boolean allQuestionsCompleted;
    private Future<Set<Long>> markedAsComplete;
    private Set<Long> completedSections;
    private Boolean userFinanceSectionCompleted;

    public ApplicationOverviewCompletedViewModel(Set<Long> sectionsMarkedAsComplete, Boolean allQuestionsCompleted, Future<Set<Long>> markedAsComplete,
                                                 Boolean userFinanceSectionCompleted) {
        this.sectionsMarkedAsComplete = sectionsMarkedAsComplete;
        this.allQuestionsCompleted = allQuestionsCompleted;
        this.markedAsComplete = markedAsComplete;
        this.userFinanceSectionCompleted = userFinanceSectionCompleted;
    }

    public Set<Long> getSectionsMarkedAsComplete() {
        return sectionsMarkedAsComplete;
    }

    public Boolean getAllQuestionsCompleted() {
        return allQuestionsCompleted;
    }

    public Set<Long> getMarkedAsComplete() throws ExecutionException, InterruptedException {
        return markedAsComplete.get();
    }

    public void setCompletedSections(Set<Long> completedSections) {
        this.completedSections = completedSections;
    }

    public Set<Long> getCompletedSections() {
        return completedSections;
    }

    public Boolean getUserFinanceSectionCompleted() {
        return userFinanceSectionCompleted;
    }

    public Boolean completedOrMarkedAsComplete(QuestionResource questionResource, SectionResource sectionResource) throws ExecutionException, InterruptedException {
        return (questionResource.isMarkAsCompletedEnabled() && getMarkedAsComplete().contains(questionResource.getId()))
                || completedSections.contains(sectionResource.getId());
    }
}
