package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

import java.util.List;

/**
 * View Model for an Research category selection overview.
 */
public class ResearchCategoryViewModel {

    private Long selectedResearchCategoryId;
    private String currentCompetitionName;
    private Long competitionId;
    private Long applicationId;
    private Long questionId;
    private List<ResearchCategoryResource> availableResearchCategories;
    private boolean hasApplicationFinances;

    public List<ResearchCategoryResource> getAvailableResearchCategories() {
        return availableResearchCategories;
    }

    public void setAvailableResearchCategories(List<ResearchCategoryResource> availableResearchCategories) {
        this.availableResearchCategories = availableResearchCategories;
    }

    public Long getSelectedResearchCategoryId() {
        return selectedResearchCategoryId;
    }

    public void setSelectedResearchCategoryId(Long selectedResearchCategoryId) {
        this.selectedResearchCategoryId = selectedResearchCategoryId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    public String getCurrentCompetitionName() {
        return currentCompetitionName;
    }

    public void setCurrentCompetitionName(String currentCompetitionName) {
        this.currentCompetitionName = currentCompetitionName;
    }

    public boolean getHasApplicationFinances() {
        return hasApplicationFinances;
    }

    public void setHasApplicationFinances(boolean hasApplicationFinances) {
        this.hasApplicationFinances = hasApplicationFinances;
    }
}
