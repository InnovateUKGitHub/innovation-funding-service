package org.innovateuk.ifs.application.finance.viewmodel;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceOverviewViewModel;
import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
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
    private Map<FinanceRowType, BigDecimal> sectionDifferences;
    private Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changes;
    private BigDecimal totalApplicationCosts;
    private BigDecimal totalProjectCostsAfterChanges;

    public ProjectFinanceChangesViewModel(boolean isInternal, String organisationName, Long organisationId,
                                          String projectName, Long applicationId, Long projectId,
                                          FinanceCheckEligibilityResource financeCheckEligibilityResource,
                                          LabourCost applicationWorkingDaysPerYearCostItem, LabourCost projectWorkingDaysPerYearCostItem,
                                          Map<FinanceRowType, BigDecimal> sectionDifferences,
                                          Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changes,
                                          BigDecimal totalApplicationCosts, BigDecimal totalProjectCostsAfterChanges) {
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
    }

    public Map<FinanceRowType, BigDecimal> getSectionDifferences() {
        return sectionDifferences;
    }

    public void setSectionDifferences(Map<FinanceRowType, BigDecimal> sectionDifferences) {
        this.sectionDifferences = sectionDifferences;
    }

    public Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> getChanges() {
        return changes;
    }

    public void setChanges(Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changes) {
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
}
