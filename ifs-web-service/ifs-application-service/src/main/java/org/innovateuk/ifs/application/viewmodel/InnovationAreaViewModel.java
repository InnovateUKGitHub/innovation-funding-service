package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * View Model for an Innovation Area selection overview.
 */
public class InnovationAreaViewModel {
    Long selectedInnovationAreaId;
    boolean noInnovationAreaApplicable;
    String currentCompetitionName;

    Long applicationId;
    Long questionId;

    List<InnovationAreaResource> availableInnovationAreas;

    public List<InnovationAreaResource> getAvailableInnovationAreas() {
        return availableInnovationAreas;
    }

    public void setAvailableInnovationAreas(List<InnovationAreaResource> availableInnovationAreas) {
        this.availableInnovationAreas = availableInnovationAreas;
    }

    public Long getSelectedInnovationAreaId() {
        return selectedInnovationAreaId;
    }

    public void setSelectedInnovationAreaId(Long selectedInnovationAreaId) {
        this.selectedInnovationAreaId = selectedInnovationAreaId;
    }

    public boolean isNoInnovationAreaApplicable() {
        return noInnovationAreaApplicable;
    }

    public void setNoInnovationAreaApplicable(boolean noInnovationAreaApplicable) {
        this.noInnovationAreaApplicable = noInnovationAreaApplicable;
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
