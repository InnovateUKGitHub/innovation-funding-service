package org.innovateuk.ifs.management.decision.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.decision.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.management.decision.form.FundingDecisionSelectionForm;
import org.innovateuk.ifs.management.navigation.Pagination;

public class ManageFundingApplicationsViewModel {

    private Pagination pagination;
    private ApplicationSummaryPageResource results;
    private FundingDecisionSelectionForm selectionForm;
    private FundingDecisionFilterForm fundingDecisionFilterForm;
    private CompetitionSummaryResource competitionSummary;
    private boolean selectAllDisabled;
    private boolean selectionLimitWarning;
<<<<<<< HEAD:ifs-web-service/ifs-competition-mgt-service/src/main/java/org/innovateuk/ifs/management/funding/viewmodel/ManageFundingApplicationsViewModel.java
    private boolean expressionOfInterestEnabled;
=======
    private boolean readOnly;
    private boolean eoi;
>>>>>>> development:ifs-web-service/ifs-competition-mgt-service/src/main/java/org/innovateuk/ifs/management/decision/viewmodel/ManageFundingApplicationsViewModel.java
    private CompetitionInFlightStatsViewModel keyStatistics;

    public ManageFundingApplicationsViewModel(Pagination pagination,
                                              ApplicationSummaryPageResource results,
                                              FundingDecisionSelectionForm selectionForm,
                                              FundingDecisionFilterForm fundingDecisionFilterForm,
                                              CompetitionSummaryResource competitionSummary,
                                              boolean selectAllDisabled,
                                              boolean selectionLimitWarning,
<<<<<<< HEAD:ifs-web-service/ifs-competition-mgt-service/src/main/java/org/innovateuk/ifs/management/funding/viewmodel/ManageFundingApplicationsViewModel.java
                                              boolean expressionOfInterestEnabled,
=======
                                              boolean readOnly,
                                              boolean eoi,
>>>>>>> development:ifs-web-service/ifs-competition-mgt-service/src/main/java/org/innovateuk/ifs/management/decision/viewmodel/ManageFundingApplicationsViewModel.java
                                              CompetitionInFlightStatsViewModel keyStatistics) {
        this.pagination = pagination;
        this.results = results;
        this.selectionForm = selectionForm;
        this.fundingDecisionFilterForm = fundingDecisionFilterForm;
        this.competitionSummary = competitionSummary;
        this.selectAllDisabled = selectAllDisabled;
        this.selectionLimitWarning = selectionLimitWarning;
<<<<<<< HEAD:ifs-web-service/ifs-competition-mgt-service/src/main/java/org/innovateuk/ifs/management/funding/viewmodel/ManageFundingApplicationsViewModel.java
        this.expressionOfInterestEnabled = expressionOfInterestEnabled;
=======
        this.readOnly = readOnly;
        this.eoi = eoi;
>>>>>>> development:ifs-web-service/ifs-competition-mgt-service/src/main/java/org/innovateuk/ifs/management/decision/viewmodel/ManageFundingApplicationsViewModel.java
        this.keyStatistics = keyStatistics;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public ApplicationSummaryPageResource getResults() {
        return results;
    }

    public FundingDecisionSelectionForm getSelectionForm() {
        return selectionForm;
    }

    public FundingDecisionFilterForm getFundingDecisionFilterForm() {
        return fundingDecisionFilterForm;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public boolean isSelectionLimitWarning() {
        return selectionLimitWarning;
    }

    public boolean isReadOnly() { return readOnly; }

    public boolean isAnythingChangeable() {
        if (results != null) {
            return results.getContent().stream().anyMatch(ApplicationSummaryResource::applicationFundingDecisionIsChangeable);
        } else {
            return false;
        }
    }

    public boolean isExpressionOfInterestEnabled() {
        return expressionOfInterestEnabled;
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }

    public void setKeyStatistics(CompetitionInFlightStatsViewModel keyStatistics) {
        this.keyStatistics = keyStatistics;
    }
}
