package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.negate;

/**
 * View model for finance/finance-summary :: application-finances-summary.
 */
public class ApplicationFinanceSummaryViewModel implements BaseAnalyticsViewModel {

    private final FinanceSummaryTableViewModel financeSummaryTableViewModel;
    private final CollaborationLevel collaborationLevel;
    private final Long currentUsersOrganisationId;

    public ApplicationFinanceSummaryViewModel(CompetitionResource competition,
                                              FinanceSummaryTableViewModel financeSummaryTableViewModel,
                                              Long currentUsersOrganisationId) {
        this.financeSummaryTableViewModel = financeSummaryTableViewModel;
        this.collaborationLevel = competition.getCollaborationLevel();
        this.currentUsersOrganisationId = currentUsersOrganisationId;
    }


    @Override
    public Long getApplicationId() {
        return financeSummaryTableViewModel.getApplicationId();
    }

    @Override
    public String getCompetitionName() {
        return financeSummaryTableViewModel.getCompetitionName();
    }


    public boolean isReadOnly() {
        return financeSummaryTableViewModel.isReadOnly();
    }

    public boolean isCollaborativeProject() {
        return financeSummaryTableViewModel.isCollaborativeProject();
    }

    public Long getCurrentUsersOrganisationId() {
        return currentUsersOrganisationId;
    }

    public BigDecimal getCompetitionMaximumFundingSought() {
        return financeSummaryTableViewModel.getCompetitionMaximumFundingSought();
    }

    public FinanceSummaryTableViewModel getFinanceSummaryTableViewModel() {
        return financeSummaryTableViewModel;
    }

    public boolean isKtp() {
        return financeSummaryTableViewModel.isKtp();
    }

    public boolean showCollaborationWarning() {
        return CollaborationLevel.COLLABORATIVE.equals(collaborationLevel)
                && !atLeastTwoCompleteOrganisationFinances();
    }

    public boolean showFundingSoughtWarning() {
        return !isFundingSoughtValid();
    }

    private boolean isFundingSoughtValid() {
        if (financeSummaryTableViewModel.getCompetitionMaximumFundingSought() == null) {
            return true;
        }
        return financeSummaryTableViewModel.getRows().stream()
                .map(row -> row.getFundingSought())
                .reduce(BigDecimal::add).get().compareTo(financeSummaryTableViewModel.getCompetitionMaximumFundingSought()) <= 0;
    }

    public List<FinanceSummaryTableRow> getIncompleteOrganisations() {
        return financeSummaryTableViewModel.getRows().stream()
                .filter(negate(FinanceSummaryTableRow::isComplete))
                .filter(negate(FinanceSummaryTableRow::isPendingOrganisation))
                .collect(toList());
    }
    
    public boolean isUsersFinancesIncomplete() {
        return financeSummaryTableViewModel.getRows().stream()
                .filter(row -> row.getOrganisationId() != null)
                .anyMatch(row -> row.getOrganisationId().equals(currentUsersOrganisationId) && !row.isComplete());
    }
    public boolean showFinancesIncompleteWarning() {
        return !financeSummaryTableViewModel.isAllFinancesComplete() && !showFundingSoughtWarning();
    }

    private boolean atLeastTwoCompleteOrganisationFinances() {
        return financeSummaryTableViewModel.getRows().stream().filter(FinanceSummaryTableRow::isComplete)
                .count() > 1;
    }

}
