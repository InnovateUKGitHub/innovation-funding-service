package org.innovateuk.ifs.application.review.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.math.BigDecimal;
import java.util.List;

public class TrackViewModel implements BaseAnalyticsViewModel {

    private CompetitionResource currentCompetition;
    private ApplicationResource currentApplication;
    private String earlyMetricsUrl;
    private BigDecimal completedQuestionsPercentage;
    private boolean reopenLinkVisible;
    private boolean isEoiEvidenceCreated;
    private String eoiEvidenceFileName;
    private List<String> validEoiEvidenceFileTypes;
    private CompetitionEoiEvidenceConfigResource eoiEvidenceConfigResource;
    private boolean userFromLeadOrganisation;

    public TrackViewModel(CompetitionResource currentCompetition,
                          ApplicationResource currentApplication,
                          String earlyMetricsUrl,
                          BigDecimal completedQuestionsPercentage,
                          boolean reopenLinkVisible,
                          boolean isEoiEvidenceCreated,
                          String eoiEvidenceFileName,
                          List<String> validEoiEvidenceFileTypes,
                          CompetitionEoiEvidenceConfigResource eoiEvidenceConfigResource,
                          boolean userFromLeadOrganisation) {
        this.currentCompetition = currentCompetition;
        this.currentApplication = currentApplication;
        this.earlyMetricsUrl = earlyMetricsUrl;
        this.completedQuestionsPercentage = completedQuestionsPercentage;
        this.reopenLinkVisible = reopenLinkVisible;
        this.isEoiEvidenceCreated = isEoiEvidenceCreated;
        this.eoiEvidenceFileName = eoiEvidenceFileName;
        this.validEoiEvidenceFileTypes = validEoiEvidenceFileTypes;
        this.eoiEvidenceConfigResource = eoiEvidenceConfigResource;
        this.userFromLeadOrganisation = userFromLeadOrganisation;
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

    public boolean isEoiEvidenceCreated() {
        return isEoiEvidenceCreated;
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
}
