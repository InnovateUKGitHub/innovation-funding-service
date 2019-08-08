package org.innovateuk.ifs.application.finance.viewmodel;

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

    private final Long currentUsersOrganisationId;

    public ApplicationFinanceSummaryViewModel(long applicationId, List<FinanceSummaryTableRow> rows, boolean readOnly, boolean collaborativeProject, Long currentUsersOrganisationId) {
        this.applicationId = applicationId;
        this.rows = rows;
        this.readOnly = readOnly;
        this.collaborativeProject = collaborativeProject;
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

    public Long getCurrentUsersOrganisationId() {
        return currentUsersOrganisationId;
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
                .anyMatch(row -> row.getOrganisationId().equals(currentUsersOrganisationId) && !row.isComplete());
    }

    public BigDecimal getCosts() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getCosts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getClaimPercentage() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getClaimPercentage)
                .reduce(0, (number, other) -> number + other);
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
}
