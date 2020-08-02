package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
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

    public YourOrganisationViewModel(long applicationId, String competitionName, boolean showStateAidAgreement, boolean showOrganisationSizeAlert, boolean h2020, boolean procurementCompetition) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.showStateAidAgreement = showStateAidAgreement;
        this.showOrganisationSizeAlert = showOrganisationSizeAlert;
        this.h2020 = h2020;
        this.procurementCompetition = procurementCompetition;
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
}
