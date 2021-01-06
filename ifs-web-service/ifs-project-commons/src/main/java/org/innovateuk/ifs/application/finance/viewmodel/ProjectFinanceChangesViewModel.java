package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * A view model for displaying project finance rows and changes made by internal project finance team
 */
public class ProjectFinanceChangesViewModel {
    private boolean isInternal;
    private String organisationName;
    private String projectName;
    private Long applicationId;
    private Long projectId;
    private Long organisationId;
    private FinanceCheckEligibilityResource financeCheckEligibility;
    private LabourCost applicationWorkingDaysPerYearCostItem;
    private LabourCost projectWorkingDaysPerYearCostItem;
    private Map<FinanceRowType, CostChangeViewModel> sectionDifferences;
    private Map<FinanceRowType, List<ChangedFinanceRowPair>> changes;
    private BigDecimal totalApplicationCosts;
    private BigDecimal totalProjectCostsAfterChanges;
    private boolean loanCompetition;

    public ProjectFinanceChangesViewModel(boolean isInternal, String organisationName, Long organisationId,
                                          String projectName, Long applicationId, Long projectId,
                                          FinanceCheckEligibilityResource financeCheckEligibilityResource,
                                          LabourCost applicationWorkingDaysPerYearCostItem, LabourCost projectWorkingDaysPerYearCostItem,
                                          Map<FinanceRowType, CostChangeViewModel> sectionDifferences,
                                          Map<FinanceRowType, List<ChangedFinanceRowPair>> changes,
                                          BigDecimal totalApplicationCosts, BigDecimal totalProjectCostsAfterChanges,
                                          boolean loanCompetition) {
        this.isInternal = isInternal;
        this.organisationName = organisationName;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.financeCheckEligibility = financeCheckEligibilityResource;
        this.applicationWorkingDaysPerYearCostItem = applicationWorkingDaysPerYearCostItem;
        this.projectWorkingDaysPerYearCostItem = projectWorkingDaysPerYearCostItem;
        this.sectionDifferences = sectionDifferences;
        this.changes = changes;
        this.totalApplicationCosts = totalApplicationCosts;
        this.totalProjectCostsAfterChanges = totalProjectCostsAfterChanges;
        this.loanCompetition = loanCompetition;
    }

    public Map<FinanceRowType, CostChangeViewModel> getSectionDifferences() {
        return sectionDifferences;
    }

    public void setSectionDifferences(Map<FinanceRowType, CostChangeViewModel> sectionDifferences) {
        this.sectionDifferences = sectionDifferences;
    }

    public Map<FinanceRowType, List<ChangedFinanceRowPair>> getChanges() {
        return changes;
    }

    public void setChanges(Map<FinanceRowType, List<ChangedFinanceRowPair>> changes) {
        this.changes = changes;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public FinanceCheckEligibilityResource getFinanceCheckEligibility() {
        return financeCheckEligibility;
    }

    public void setFinanceCheckEligibility(FinanceCheckEligibilityResource financeCheckEligibilityResource) {
        this.financeCheckEligibility = financeCheckEligibilityResource;
    }

    public BigDecimal getTotalApplicationCosts() {
        return totalApplicationCosts;
    }

    public void setTotalApplicationCosts(BigDecimal totalApplicationCosts) {
        this.totalApplicationCosts = totalApplicationCosts;
    }

    public BigDecimal getTotalProjectCostsAfterChanges() {
        return totalProjectCostsAfterChanges;
    }

    public void setTotalProjectCostsAfterChanges(BigDecimal totalProjectCostsAfterChanges) {
        this.totalProjectCostsAfterChanges = totalProjectCostsAfterChanges;
    }

    public BigDecimal getDifferenceInTotalCostsAfterChanges(){
        if(totalProjectCostsAfterChanges == null || totalApplicationCosts == null)
            return null;
        else {
            return totalProjectCostsAfterChanges.subtract(totalApplicationCosts);
        }
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setInternal(boolean internal) {
        isInternal = internal;
    }

    public LabourCost getApplicationWorkingDaysPerYearCostItem() {
        return applicationWorkingDaysPerYearCostItem;
    }

    public void setApplicationWorkingDaysPerYearCostItem(LabourCost applicationWorkingDaysPerYearCostItem) {
        this.applicationWorkingDaysPerYearCostItem = applicationWorkingDaysPerYearCostItem;
    }

    public LabourCost getProjectWorkingDaysPerYearCostItem() {
        return projectWorkingDaysPerYearCostItem;
    }

    public void setProjectWorkingDaysPerYearCostItem(LabourCost projectWorkingDaysPerYearCostItem) {
        this.projectWorkingDaysPerYearCostItem = projectWorkingDaysPerYearCostItem;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }
}
