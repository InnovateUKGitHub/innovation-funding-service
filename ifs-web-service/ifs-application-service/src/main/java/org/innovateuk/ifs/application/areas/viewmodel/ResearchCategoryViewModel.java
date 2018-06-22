package org.innovateuk.ifs.application.areas.viewmodel;

import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

import java.util.List;

/**
 * View Model for a Research category selection overview.
 */
public class ResearchCategoryViewModel extends AbstractLeadOnlyViewModel {

    private String currentCompetitionName;
    private List<ResearchCategoryResource> availableResearchCategories;
    private boolean hasApplicationFinances;
    private boolean useNewApplicantMenu;

    public ResearchCategoryViewModel(String currentCompetitionName,
                                     Long applicationId,
                                     Long questionId,
                                     List<ResearchCategoryResource> availableResearchCategories,
                                     boolean hasApplicationFinances,
                                     boolean useNewApplicantMenu,
                                     boolean closed,
                                     boolean complete,
                                     boolean canMarkAsComplete) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete);
        this.currentCompetitionName = currentCompetitionName;
        this.availableResearchCategories = availableResearchCategories;
        this.hasApplicationFinances = hasApplicationFinances;
        this.useNewApplicantMenu = useNewApplicantMenu;
    }

    public String getCurrentCompetitionName() {
        return currentCompetitionName;
    }

    public List<ResearchCategoryResource> getAvailableResearchCategories() {
        return availableResearchCategories;
    }

    public boolean hasApplicationFinances() {
        return hasApplicationFinances;
    }

    public boolean isUseNewApplicantMenu() {
        return useNewApplicantMenu;
    }

    @Override
    public boolean isSummary() {
        return false;
    }
}
