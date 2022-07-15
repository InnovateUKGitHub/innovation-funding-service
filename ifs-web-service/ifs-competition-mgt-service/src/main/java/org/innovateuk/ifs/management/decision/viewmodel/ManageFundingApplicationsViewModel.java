package org.innovateuk.ifs.management.decision.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.decision.form.DecisionFilterForm;
import org.innovateuk.ifs.management.decision.form.DecisionSelectionForm;
import org.innovateuk.ifs.management.navigation.Pagination;

public class ManageFundingApplicationsViewModel {

    private Pagination pagination;
    private ApplicationSummaryPageResource results;
    private DecisionSelectionForm selectionForm;
    private DecisionFilterForm decisionFilterForm;
    private CompetitionSummaryResource competitionSummary;
    private boolean selectAllDisabled;
    private boolean selectionLimitWarning;
    private boolean readOnly;
    private boolean eoi;
    private CompetitionInFlightStatsViewModel keyStatistics;

    public ManageFundingApplicationsViewModel(Pagination pagination,
                                              ApplicationSummaryPageResource results,
                                              DecisionSelectionForm selectionForm,
                                              DecisionFilterForm decisionFilterForm,
                                              CompetitionSummaryResource competitionSummary,
                                              boolean selectAllDisabled,
                                              boolean selectionLimitWarning,
                                              boolean readOnly,
                                              boolean eoi,
                                              CompetitionInFlightStatsViewModel keyStatistics) {
        this.pagination = pagination;
        this.results = results;
        this.selectionForm = selectionForm;
        this.decisionFilterForm = decisionFilterForm;
        this.competitionSummary = competitionSummary;
        this.selectAllDisabled = selectAllDisabled;
        this.selectionLimitWarning = selectionLimitWarning;
        this.readOnly = readOnly;
        this.eoi = eoi;
        this.keyStatistics = keyStatistics;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public ApplicationSummaryPageResource getResults() {
        return results;
    }

    public DecisionSelectionForm getSelectionForm() {
        return selectionForm;
    }

    public DecisionFilterForm getDecisionFilterForm() {
        return decisionFilterForm;
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
            return results.getContent().stream().anyMatch(ApplicationSummaryResource::applicationDecisionIsChangeable);
        } else {
            return false;
        }
    }

    public boolean isEoi() {
        return eoi;
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }

    public void setKeyStatistics(CompetitionInFlightStatsViewModel keyStatistics) {
        this.keyStatistics = keyStatistics;
    }
}
