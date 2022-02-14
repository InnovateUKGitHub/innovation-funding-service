package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class ApplicationYourOrganisationViewModel implements BaseAnalyticsViewModel {

    private static final String NON_STATE_AID_URL = "https://www.gov.uk/guidance/innovation-apply-for-a-funding-award#funding-rules";
    private static final String STATE_AID_URL = "http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm";

    private long applicationId;
    private long competitionId;
    private String competitionName;
    private FundingRules fundingRules;
    private OrganisationTypeEnum organisationType;
    private boolean showOrganisationSizeAlert;
    private boolean maximumFundingLevelConstant;
    private boolean internal;
    private YourOrganisationDetailsReadOnlyViewModel orgDetailsViewModel;
    private boolean partnerOrgDisplay;


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
        String orgSize = internal ? "the organisation's size" : "your organisation size";
        String you = internal ? "the organisation" : "you";
        String url = fundingRules == FundingRules.STATE_AID ? STATE_AID_URL : NON_STATE_AID_URL;

        String fundingLevelDependsOnOrganisationSizeMessage = String.format("<p class=\"govuk-body\">The size of %s determines the level of funding  %s are eligible for.</p>", organisation, you);
        String sizeDefinition = String.format("<p class=\"govuk-body\">Please use our <a href=\"%s\"> guidance </a> (opens in a new window) to determine %s.</p>", url, orgSize);

        if (maximumFundingLevelConstant) {
            return sizeDefinition;
        } else {
            return fundingLevelDependsOnOrganisationSizeMessage + sizeDefinition;
        }
    }

    public boolean isPartnerOrgDisplay() {
        return partnerOrgDisplay;
    }

    public void setPartnerOrgDisplay(boolean partnerOrgDisplay) {
        this.partnerOrgDisplay = partnerOrgDisplay;
    }

    public YourOrganisationDetailsReadOnlyViewModel getOrgDetailsViewModel() {
        return orgDetailsViewModel;
    }

    public void setOrgDetailsViewModel(YourOrganisationDetailsReadOnlyViewModel orgDetailsViewModel) {
        this.orgDetailsViewModel = orgDetailsViewModel;
    }
}
