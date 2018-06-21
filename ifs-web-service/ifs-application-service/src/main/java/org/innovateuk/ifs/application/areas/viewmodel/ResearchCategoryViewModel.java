package org.innovateuk.ifs.application.areas.viewmodel;

import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

import java.util.List;

/**
 * View Model for a Research category selection overview.
 */
public class ResearchCategoryViewModel extends AbstractLeadOnlyViewModel {

    private String currentCompetitionName;
    private long applicationId;
    private List<ResearchCategoryResource> availableResearchCategories;
    private boolean hasApplicationFinances;

    public ResearchCategoryViewModel(String currentCompetitionName,
                                     long applicationId,
                                     long questionId,
                                     List<ResearchCategoryResource> availableResearchCategories,
                                     boolean hasApplicationFinances,
                                     boolean closed,
                                     boolean complete,
                                     boolean canMarkAsComplete) {
        super(questionId, closed, complete, canMarkAsComplete);
        this.currentCompetitionName = currentCompetitionName;
        this.applicationId = applicationId;
        this.availableResearchCategories = availableResearchCategories;
        this.hasApplicationFinances = hasApplicationFinances;
    }

    public String getCurrentCompetitionName() {
        return currentCompetitionName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public List<ResearchCategoryResource> getAvailableResearchCategories() {
        return availableResearchCategories;
    }

    public boolean isHasApplicationFinances() {
        return hasApplicationFinances;
    }

    @Override
    public boolean isSummary() {
        return false;
    }
}
