package org.innovateuk.ifs.management.funding.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.competition.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.competition.form.FundingDecisionSelectionForm;
import org.innovateuk.ifs.management.navigation.Pagination;

public class ManageFundingApplicationsViewModel {

    private Pagination pagination;
    private ApplicationSummaryPageResource results;
    private FundingDecisionSelectionForm selectionForm;
    private FundingDecisionFilterForm fundingDecisionFilterForm;
    private CompetitionSummaryResource competitionSummary;
    private String originQuery;
    private boolean selectAllDisabled;
    private boolean selectionLimitWarning;

    public ManageFundingApplicationsViewModel(Pagination pagination,
                                              ApplicationSummaryPageResource results,
                                              FundingDecisionSelectionForm selectionForm,
                                              FundingDecisionFilterForm fundingDecisionFilterForm,
                                              CompetitionSummaryResource competitionSummary,
                                              String originQuery,
                                              boolean selectAllDisabled,
                                              boolean selectionLimitWarning) {
        this.pagination = pagination;
        this.results = results;
        this.selectionForm = selectionForm;
        this.fundingDecisionFilterForm = fundingDecisionFilterForm;
        this.competitionSummary = competitionSummary;
        this.originQuery = originQuery;
        this.selectAllDisabled = selectAllDisabled;
        this.selectionLimitWarning = selectionLimitWarning;
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

    public String getOriginQuery() {
        return originQuery;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public boolean isSelectionLimitWarning() {
        return selectionLimitWarning;
    }
}
