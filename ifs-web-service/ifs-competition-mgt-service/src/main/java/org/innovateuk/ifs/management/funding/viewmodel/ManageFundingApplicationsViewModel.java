package org.innovateuk.ifs.management.funding.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.management.funding.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.management.funding.form.FundingDecisionSelectionForm;
import org.innovateuk.ifs.management.navigation.Pagination;

public class ManageFundingApplicationsViewModel {

    private Pagination pagination;
    private ApplicationSummaryPageResource results;
    private FundingDecisionSelectionForm selectionForm;
    private FundingDecisionFilterForm fundingDecisionFilterForm;
    private CompetitionSummaryResource competitionSummary;
    private boolean selectAllDisabled;
    private boolean selectionLimitWarning;

    public ManageFundingApplicationsViewModel(Pagination pagination,
                                              ApplicationSummaryPageResource results,
                                              FundingDecisionSelectionForm selectionForm,
                                              FundingDecisionFilterForm fundingDecisionFilterForm,
                                              CompetitionSummaryResource competitionSummary,
                                              boolean selectAllDisabled,
                                              boolean selectionLimitWarning) {
        this.pagination = pagination;
        this.results = results;
        this.selectionForm = selectionForm;
        this.fundingDecisionFilterForm = fundingDecisionFilterForm;
        this.competitionSummary = competitionSummary;
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

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public boolean isSelectionLimitWarning() {
        return selectionLimitWarning;
    }

    public boolean isAnythingChangeable() {
        if (results != null) {
            return results.getContent().stream().anyMatch(ApplicationSummaryResource::applicationFundingDecisionIsChangeable);
        } else {
            return false;
        }
    }

}
