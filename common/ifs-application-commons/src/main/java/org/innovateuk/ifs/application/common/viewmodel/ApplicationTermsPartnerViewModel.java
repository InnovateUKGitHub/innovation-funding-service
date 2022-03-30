package org.innovateuk.ifs.application.common.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Model attributes for the application terms partner view.
 */
public class ApplicationTermsPartnerViewModel implements BaseAnalyticsViewModel {
    private final long applicationId;
    private final String competitionName;
    private final long questionId;
    private final List<ApplicationTermsPartnerRowViewModel> partners;
    private final boolean isThirdPartyProcurementCompetition;
    private final CompetitionThirdPartyConfigResource thirdPartyConfig;

    public ApplicationTermsPartnerViewModel(long applicationId, String competitionName, long questionId, List<ApplicationTermsPartnerRowViewModel> partnerStatus,
                                            boolean isThirdPartyProcurementCompetition, CompetitionThirdPartyConfigResource thirdPartyConfig) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.partners = partnerStatus;
        this.questionId = questionId;
        this.isThirdPartyProcurementCompetition = isThirdPartyProcurementCompetition;
        this.thirdPartyConfig = thirdPartyConfig;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public List<ApplicationTermsPartnerRowViewModel> getPartners() {
        return partners;
    }

    public List<ApplicationTermsPartnerRowViewModel> getNonAcceptedPartners() {
        return getPartners()
                .stream()
                .filter(p -> !p.isTermsAccepted())
                .collect(toList());
    }

    public boolean isThirdPartyProcurementCompetition() {
        return isThirdPartyProcurementCompetition;
    }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() {
        return thirdPartyConfig;
    }

    @JsonIgnore
    public String getPageTitle() {
        String pageTitle =  "Partners' acceptance of";
        String subPageTitle = isThirdPartyProcurementCompetition ? thirdPartyConfig.getTermsAndConditionsLabel() :
                "terms and conditions";
        return pageTitle + " " + subPageTitle;
    }

    @JsonIgnore
    public String getLinkTitle() {
        String linkSubTitle = "of an Innovate UK grant award";
        String linkTitle = isThirdPartyProcurementCompetition ? thirdPartyConfig.getTermsAndConditionsLabel() :
                "Terms and conditions";
        return linkTitle + " " + linkSubTitle;
    }
}