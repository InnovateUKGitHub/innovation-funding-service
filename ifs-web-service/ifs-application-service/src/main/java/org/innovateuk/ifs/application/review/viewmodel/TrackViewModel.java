package org.innovateuk.ifs.application.review.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState.*;

public class TrackViewModel implements BaseAnalyticsViewModel {

    private CompetitionResource currentCompetition;
    private ApplicationResource currentApplication;
    private String earlyMetricsUrl;
    private BigDecimal completedQuestionsPercentage;
    private boolean reopenLinkVisible;
    private ApplicationEoiEvidenceState applicationEoiEvidenceState;
    private String eoiEvidenceFileName;
    private List<String> validEoiEvidenceFileTypes;
    private CompetitionEoiEvidenceConfigResource eoiEvidenceConfigResource;
    private boolean userFromLeadOrganisation;
    private boolean applicationEoiEvidenceResponseResourceIsEmpty;

    public TrackViewModel(CompetitionResource currentCompetition,
                          ApplicationResource currentApplication,
                          String earlyMetricsUrl,
                          BigDecimal completedQuestionsPercentage,
                          boolean reopenLinkVisible) {
        this.currentCompetition = currentCompetition;
        this.currentApplication = currentApplication;
        this.earlyMetricsUrl = earlyMetricsUrl;
        this.completedQuestionsPercentage = completedQuestionsPercentage;
        this.reopenLinkVisible = reopenLinkVisible;
    }

    public TrackViewModel(CompetitionResource currentCompetition,
                          ApplicationResource currentApplication,
                          String earlyMetricsUrl,
                          BigDecimal completedQuestionsPercentage,
                          boolean reopenLinkVisible,
                          ApplicationEoiEvidenceState applicationEoiEvidenceState,
                          String eoiEvidenceFileName,
                          List<String> validEoiEvidenceFileTypes,
                          CompetitionEoiEvidenceConfigResource eoiEvidenceConfigResource,
                          boolean userFromLeadOrganisation,
                          boolean applicationEoiEvidenceResponseResourceIsEmpty) {
        this.currentCompetition = currentCompetition;
        this.currentApplication = currentApplication;
        this.earlyMetricsUrl = earlyMetricsUrl;
        this.completedQuestionsPercentage = completedQuestionsPercentage;
        this.reopenLinkVisible = reopenLinkVisible;
        this.applicationEoiEvidenceState = applicationEoiEvidenceState;
        this.eoiEvidenceFileName = eoiEvidenceFileName;
        this.validEoiEvidenceFileTypes = validEoiEvidenceFileTypes;
        this.eoiEvidenceConfigResource = eoiEvidenceConfigResource;
        this.userFromLeadOrganisation = userFromLeadOrganisation;
        this.applicationEoiEvidenceResponseResourceIsEmpty = applicationEoiEvidenceResponseResourceIsEmpty;
    }

    @Override
    public Long getApplicationId() {
        return currentApplication.getId();
    }

    @Override
    public String getCompetitionName() {
        return currentCompetition.getName();
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public void setCurrentCompetition(CompetitionResource currentCompetition) {
        this.currentCompetition = currentCompetition;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public void setCurrentApplication(ApplicationResource currentApplication) {
        this.currentApplication = currentApplication;
    }

    public String getEarlyMetricsUrl() {
        return earlyMetricsUrl;
    }

    public void setEarlyMetricsUrl(String earlyMetricsUrl) {
        this.earlyMetricsUrl = earlyMetricsUrl;
    }

    public BigDecimal getCompletedQuestionsPercentage() {
        return completedQuestionsPercentage;
    }

    public void setCompletedQuestionsPercentage(BigDecimal completedQuestionsPercentage) {
        this.completedQuestionsPercentage = completedQuestionsPercentage;
    }

    public boolean isReopenLinkVisible() {
        return reopenLinkVisible;
    }

    public ApplicationEoiEvidenceState getApplicationEoiEvidenceState() {
        return applicationEoiEvidenceState;
    }

    public String getEoiEvidenceFileName() {
        return eoiEvidenceFileName;
    }

    public List<String> getValidEoiEvidenceFileTypes() {
        return validEoiEvidenceFileTypes;
    }

    public CompetitionEoiEvidenceConfigResource getEoiEvidenceConfigResource() {
        return eoiEvidenceConfigResource;
    }

    public boolean isUserFromLeadOrganisation() {
        return userFromLeadOrganisation;
    }

    public boolean isDisplayIfsAssessmentInformation() {
        return currentCompetition.isProcurement() ||
                (currentCompetition.getCompletionStage() == CompetitionCompletionStage.COMPETITION_CLOSE
                        && !currentCompetition.isKtp());
    }

    @JsonIgnore
    public FundingType getProcurementFundingType() {
        return FundingType.PROCUREMENT;
    }

    @JsonIgnore
    public boolean isEoiEvidenceSubmitted() {
        return applicationEoiEvidenceState == SUBMITTED;
    }

    @JsonIgnore
    public boolean disableSubmitButton() {
        return applicationEoiEvidenceState == NOT_SUBMITTED || applicationEoiEvidenceResponseResourceIsEmpty;
    }

    @JsonIgnore
    public boolean showFeedBackSection() {
        return (currentCompetition.getCompetitionEoiEvidenceConfigResource().isEvidenceRequired() && !userFromLeadOrganisation) || applicationEoiEvidenceState == SUBMITTED;
    }
}
