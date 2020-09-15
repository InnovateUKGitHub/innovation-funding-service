package org.innovateuk.ifs.application.areas.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * View Model for an Innovation Area selection overview.
 */
public class InnovationAreaViewModel implements BaseAnalyticsViewModel {

    private Long selectedInnovationAreaId;
    private boolean noInnovationAreaApplicable;
    private String applicationName;
    private String competitionName;
    private long applicationId;
    private Long questionId;
    private List<InnovationAreaResource> availableInnovationAreas;

    public InnovationAreaViewModel() {
    }

    public InnovationAreaViewModel(Long selectedInnovationAreaId, boolean noInnovationAreaApplicable, String applicationName, String competitionName, long applicationId, Long questionId, List<InnovationAreaResource> availableInnovationAreas) {
        this.selectedInnovationAreaId = selectedInnovationAreaId;
        this.noInnovationAreaApplicable = noInnovationAreaApplicable;
        this.applicationName = applicationName;
        this.competitionName = competitionName;
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.availableInnovationAreas = availableInnovationAreas;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
