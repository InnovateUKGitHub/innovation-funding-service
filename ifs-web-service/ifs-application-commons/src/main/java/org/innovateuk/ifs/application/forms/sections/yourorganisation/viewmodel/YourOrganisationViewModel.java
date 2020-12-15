package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * View model to support "Your organisation" pages
 */
public class YourOrganisationViewModel implements BaseAnalyticsViewModel {

    private long applicationId;
    private String competitionName;
    private boolean showStateAidAgreement;
    private boolean showOrganisationSizeAlert;
    private boolean h2020;
    private boolean procurementCompetition;
    private FundingRules fundingRules;

    public YourOrganisationViewModel(long applicationId, CompetitionResource competitionResource, boolean showStateAidAgreement, boolean showOrganisationSizeAlert) {
        this.applicationId = applicationId;
        this.competitionName = competitionResource.getName();
        this.showStateAidAgreement = showStateAidAgreement;
        this.showOrganisationSizeAlert = showOrganisationSizeAlert;
        this.h2020 = competitionResource.isH2020();
        this.procurementCompetition = competitionResource.isProcurement();
        this.fundingRules = competitionResource.getFundingRules();
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public List<FormOption> getOrganisationSizeOptions() {
        return simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));
    }

    public boolean isShowStateAidAgreement() {
        return showStateAidAgreement;
    }

    public boolean isShowOrganisationSizeAlert() {
        return showOrganisationSizeAlert;
    }

    public boolean isH2020() {
        return h2020;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }

    public String getFundingRulesText() {
        return fundingRules.getDisplayName();
    }
}
