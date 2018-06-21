package org.innovateuk.ifs.application.areas.viewmodel;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

import java.util.List;

/**
 * View Model for a Research category selection overview.
 */
public class ResearchCategoryViewModel {

    private String currentCompetitionName;
    private long applicationId;
    private long questionId;
    private List<ResearchCategoryResource> availableResearchCategories;
    private boolean canMarkAsComplete;
    private boolean closed;
    private boolean complete;
    private boolean hasApplicationFinances;

    public ResearchCategoryViewModel(final String currentCompetitionName,
                                     final long applicationId,
                                     final long questionId,
                                     final List<ResearchCategoryResource> availableResearchCategories,
                                     final boolean canMarkAsComplete,
                                     final boolean closed,
                                     final boolean complete,
                                     final boolean hasApplicationFinances) {
        this.currentCompetitionName = currentCompetitionName;
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.availableResearchCategories = availableResearchCategories;
        this.canMarkAsComplete = canMarkAsComplete;
        this.closed = closed;
        this.complete = complete;
        this.hasApplicationFinances = hasApplicationFinances;
    }

    public String getCurrentCompetitionName() {
        return currentCompetitionName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public List<ResearchCategoryResource> getAvailableResearchCategories() {
        return availableResearchCategories;
    }

    public boolean isCanMarkAsComplete() {
        return canMarkAsComplete;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isHasApplicationFinances() {
        return hasApplicationFinances;
    }

    public boolean isSummary() {
        return false;
    }
}
