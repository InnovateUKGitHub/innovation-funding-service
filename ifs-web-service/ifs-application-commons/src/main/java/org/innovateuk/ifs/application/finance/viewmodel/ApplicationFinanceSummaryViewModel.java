package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.competition.resource.CollaborationLevel;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.negate;

/**
 * View model for finance/finance-summary :: application-finances-summary.
 */
public class ApplicationFinanceSummaryViewModel {

    private final long applicationId;
    private final List<FinanceSummaryTableRow> rows;
    private final boolean readOnly;
    private final boolean collaborativeProject;
    private final CollaborationLevel collaborationLevel;
    private final boolean fundingLevelFirst;

    private final Long currentUsersOrganisationId;

    public ApplicationFinanceSummaryViewModel(long applicationId,
                                              List<FinanceSummaryTableRow> rows,
                                              boolean readOnly,
                                              boolean collaborativeProject,
                                              CollaborationLevel collaborationLevel,
                                              boolean fundingLevelFirst,
                                              Long currentUsersOrganisationId) {
        this.applicationId = applicationId;
        this.rows = rows;
        this.readOnly = readOnly;
        this.collaborativeProject = collaborativeProject;
        this.collaborationLevel = collaborationLevel;
        this.fundingLevelFirst = fundingLevelFirst;
        this.currentUsersOrganisationId = currentUsersOrganisationId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public List<FinanceSummaryTableRow> getRows() {
        return rows;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isFundingLevelFirst() {
        return fundingLevelFirst;
    }

    public Long getCurrentUsersOrganisationId() {
        return currentUsersOrganisationId;
    }

    public boolean isFundingSoughtFirst() {
        return !isFundingLevelFirst();
    }

    public boolean isAllFinancesComplete() {
        return rows.stream()
                .filter(negate(FinanceSummaryTableRow::isPendingOrganisation))
                .allMatch(FinanceSummaryTableRow::isComplete);
    }

    public List<FinanceSummaryTableRow> getIncompleteOrganisations() {
        return rows.stream()
                .filter(negate(FinanceSummaryTableRow::isComplete))
                .filter(negate(FinanceSummaryTableRow::isPendingOrganisation))
                .collect(toList());
    }

    public boolean isUsersFinancesIncomplete() {
        return rows.stream()
                .filter(row -> row.getOrganisationId() != null)
                .anyMatch(row -> row.getOrganisationId().equals(currentUsersOrganisationId) && !row.isComplete());
    }

    public BigDecimal getCosts() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getCosts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getClaimPercentage() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getClaimPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFundingSought() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getFundingSought)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOtherFunding() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getOtherFunding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getContribution() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getContribution)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean showCollaborationWarning() {
        return CollaborationLevel.COLLABORATIVE.equals(collaborationLevel)
                && !atLeastTwoCompleteOrganisationFinances();
    }

    private boolean atLeastTwoCompleteOrganisationFinances() {
        return rows.stream().filter(FinanceSummaryTableRow::isComplete)
                .count() > 1;
    }
}
