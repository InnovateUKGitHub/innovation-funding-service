package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * View model to support "Your organisation" pages
 */
public class ApplicationYourOrganisationViewModel implements BaseAnalyticsViewModel {

    private long applicationId;
    private long competitionId;
    private String competitionName;
    private FundingRules fundingRules;
    private OrganisationTypeEnum organisationType;
    private boolean showOrganisationSizeAlert;
    private boolean maximumFundingLevelConstant;
    private boolean internal;


    public ApplicationYourOrganisationViewModel(long applicationId, CompetitionResource competition,
                                                OrganisationTypeEnum organisationType,
                                                boolean maximumFundingLevelConstant,
                                                boolean showOrganisationSizeAlert,
                                                boolean internal) {
        this.applicationId = applicationId;
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.fundingRules = competition.getFundingRules();
        this.organisationType = organisationType;
        this.maximumFundingLevelConstant = maximumFundingLevelConstant;
        this.showOrganisationSizeAlert = showOrganisationSizeAlert;
        this.internal = internal;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public List<FormOption> getOrganisationSizeOptions() {
        return simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public boolean isMaximumFundingLevelConstant() {
        return maximumFundingLevelConstant;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isShowOrganisationSizeAlert() {
        return showOrganisationSizeAlert;
    }

    public boolean isShowEligibilityMessage() {
        return organisationType == OrganisationTypeEnum.BUSINESS && fundingRules != FundingRules.NOT_AID;
    }

    public String getHint() {
        String organisation = internal ? "the organisation" : "your organisation";
        String you = internal ? "the organisation" : "you";

        String maximumFundingLevelConstantMessage = "<p class=\"govuk-body\">You must tell us the size of " + organisation + " to determine the level of funding " + you + " are eligible for.</p>";

        String nonStateAidUrl = "https://www.gov.uk/guidance/innovation-apply-for-a-funding-award#funding-rules";
        String stateAidUrl = "http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm";

        String url = fundingRules == FundingRules.STATE_AID ? stateAidUrl : nonStateAidUrl;

        String sizeDefinition = "<p class=\"govuk-body\">Please use <a href=\"" + url + "\">our guidance (opens in a new window)</a> to determine " + organisation + ".</p>";

        String message = "";

        if (!maximumFundingLevelConstant) {
            message += maximumFundingLevelConstantMessage;
        }
        message += sizeDefinition;
        return message;
    }
}
