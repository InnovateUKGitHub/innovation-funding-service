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
    private String researchCategory;

    public ResearchCategoryViewModel(String currentCompetitionName,
                                     Long applicationId,
                                     Long questionId,
                                     List<ResearchCategoryResource> availableResearchCategories,
                                     boolean hasApplicationFinances,
                                     boolean useNewApplicantMenu,
                                     String researchCategory,
                                     boolean closed,
                                     boolean complete,
                                     boolean canMarkAsComplete,
                                     boolean allReadonly) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete, allReadonly);
        this.currentCompetitionName = currentCompetitionName;
        this.availableResearchCategories = availableResearchCategories;
        this.hasApplicationFinances = hasApplicationFinances;
        this.useNewApplicantMenu = useNewApplicantMenu;
        this.researchCategory = researchCategory;
    }

    public String getCurrentCompetitionName() {
        return currentCompetitionName;
    }

    public List<ResearchCategoryResource> getAvailableResearchCategories() {
        return availableResearchCategories;
    }

    public boolean getHasApplicationFinances() {
        return hasApplicationFinances;
    }

    public boolean isUseNewApplicantMenu() {
        return useNewApplicantMenu;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    @Override
    public boolean isSummary() {
        return false;
    }
}
