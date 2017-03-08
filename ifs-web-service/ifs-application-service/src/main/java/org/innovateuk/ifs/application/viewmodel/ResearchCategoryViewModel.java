package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

import java.util.List;

/**
 * View Model for an Research category selection overview.
 */
public class ResearchCategoryViewModel {
    Long selectedResearchCategoryId;
    String currentCompetitionName;

    Long applicationId;
    Long questionId;

    List<ResearchCategoryResource> availableResearchCategories;

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
}
