package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;
import org.innovateuk.ifs.project.spendprofile.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * View model to back the Spend Profile page. Also wraps SpendProfileSummaryModel for summary table below spend profile.
 */
public class ProjectSpendProfileViewModel {
    private List<ObjectError> objectErrors;
    private Long projectId;
    private Long applicationId;
    private Long organisationId;
    private String projectName;
    private String organisationName;
    private LocalDate targetProjectStartDate;
    private Long durationInMonths;
    private SpendProfileSummaryModel summary;
    private SpendProfileTableResource table;
    private Boolean markedAsComplete;
    private Boolean usingJesFinances;
    private Map<Long, BigDecimal> categoryToActualTotal;
    private List<BigDecimal> totalForEachMonth;
    private BigDecimal totalOfAllActualTotals;
    private BigDecimal totalOfAllEligibleTotals;
    private Map<String, List<Map<Long, List<BigDecimal>>>> costCategoryGroupMap;
    private Map<Long, CostCategoryResource> costCategoryResourceMap;
    private boolean submitted;
    private boolean userPartOfThisOrganisation;
    private boolean projectManager;
    private boolean approved;
    private boolean leadPartner;

    public ProjectSpendProfileViewModel(ProjectResource project, OrganisationResource organisationResource, SpendProfileTableResource table,
                                        SpendProfileSummaryModel summary, Boolean markedAsComplete,
                                        Map<Long, BigDecimal> categoryToActualTotal, List<BigDecimal> totalForEachMonth,
                                        BigDecimal totalOfAllActualTotals, BigDecimal totalOfAllEligibleTotals, boolean submitted,
                                        Map<String, List<Map<Long, List<BigDecimal>>>> costCategoryGroupMap,
                                        Map<Long, CostCategoryResource> costCategoryResourceMap, Boolean usingJesFinances, boolean userPartOfThisOrganisation,
                                        boolean isProjectManager, boolean approved, boolean leadPartner) {
        this.projectId = project.getId();
        this.organisationId = organisationResource.getId();
        this.projectName = project.getName();
        this.organisationName = organisationResource.getName();
        this.targetProjectStartDate = project.getTargetStartDate();
        this.durationInMonths = project.getDurationInMonths();
        this.applicationId = project.getApplication();
        this.summary = summary;
        this.table = table;
        this.markedAsComplete = markedAsComplete;
        this.categoryToActualTotal = categoryToActualTotal;
        this.totalForEachMonth = totalForEachMonth;
        this.totalOfAllActualTotals = totalOfAllActualTotals;
        this.totalOfAllEligibleTotals = totalOfAllEligibleTotals;
        this.costCategoryGroupMap = costCategoryGroupMap;
        this.costCategoryResourceMap = costCategoryResourceMap;
        this.usingJesFinances = usingJesFinances;
        this.submitted = submitted;
        this.userPartOfThisOrganisation = userPartOfThisOrganisation;
        this.projectManager = isProjectManager;
        this.approved = approved;
        this.leadPartner = leadPartner;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public LocalDate getTargetProjectStartDate() {
        return targetProjectStartDate;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public SpendProfileSummaryModel getSummary() {
        return summary;
    }

    public SpendProfileTableResource getTable() {
        return table;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setTargetProjectStartDate(LocalDate targetProjectStartDate) {
        this.targetProjectStartDate = targetProjectStartDate;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public void setSummary(SpendProfileSummaryModel summary) {
        this.summary = summary;
    }

    public void setTable(SpendProfileTableResource table) {
        this.table = table;
    }

    public Boolean isMarkedAsComplete() {
        return markedAsComplete;
    }

    public void setMarkedAsComplete(Boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Map<Long, BigDecimal> getCategoryToActualTotal() {
        return categoryToActualTotal;
    }

    public List<BigDecimal> getTotalForEachMonth() {
        return totalForEachMonth;
    }

    public BigDecimal getTotalOfAllActualTotals() {
        return totalOfAllActualTotals;
    }

    public BigDecimal getTotalOfAllEligibleTotals() {
        return totalOfAllEligibleTotals;
    }

    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
    }

    public Boolean getMarkedAsComplete() {
        return markedAsComplete;
    }

    public boolean isApproved() { return approved; }

    public void setCategoryToActualTotal(Map<Long, BigDecimal> categoryToActualTotal) {
        this.categoryToActualTotal = categoryToActualTotal;
    }

    public void setTotalForEachMonth(List<BigDecimal> totalForEachMonth) {
        this.totalForEachMonth = totalForEachMonth;
    }

    public void setTotalOfAllActualTotals(BigDecimal totalOfAllActualTotals) {
        this.totalOfAllActualTotals = totalOfAllActualTotals;
    }

    public void setTotalOfAllEligibleTotals(BigDecimal totalOfAllEligibleTotals) {
        this.totalOfAllEligibleTotals = totalOfAllEligibleTotals;
    }

    public Boolean getUsingJesFinances() {
        return usingJesFinances;
    }

    public void setUsingJesFinances(Boolean usingJesFinances) {
        this.usingJesFinances = usingJesFinances;
    }

    public Map<Long, CostCategoryResource> getCostCategoryResourceMap() {
        return costCategoryResourceMap;
    }

    public void setCostCategoryResourceMap(Map<Long, CostCategoryResource> costCategoryResourceMap) {
        this.costCategoryResourceMap = costCategoryResourceMap;
    }

    public boolean isSubmitted() {
            return submitted;
    }

    public boolean isUserPartOfThisOrganisation() {
        return userPartOfThisOrganisation;
    }

    public boolean isProjectManager() { return projectManager; }

    public Long getApplicationId() {
        return applicationId;
    }

    public boolean isLeadPartner() {
        return leadPartner;
    }

    public void setLeadPartner(boolean leadPartner) {
        this.leadPartner = leadPartner;
    }

    public boolean isIncludeFinancialYearTable() {
        return summary != null;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileViewModel that = (ProjectSpendProfileViewModel) o;

        return new EqualsBuilder()
                .append(submitted, that.submitted)
                .append(userPartOfThisOrganisation, that.userPartOfThisOrganisation)
                .append(objectErrors, that.objectErrors)
                .append(projectId, that.projectId)
                .append(applicationId, that.applicationId)
                .append(organisationId, that.organisationId)
                .append(projectName, that.projectName)
                .append(organisationName, that.organisationName)
                .append(targetProjectStartDate, that.targetProjectStartDate)
                .append(durationInMonths, that.durationInMonths)
                .append(summary, that.summary)
                .append(table, that.table)
                .append(markedAsComplete, that.markedAsComplete)
                .append(usingJesFinances, that.usingJesFinances)
                .append(categoryToActualTotal, that.categoryToActualTotal)
                .append(totalForEachMonth, that.totalForEachMonth)
                .append(totalOfAllActualTotals, that.totalOfAllActualTotals)
                .append(totalOfAllEligibleTotals, that.totalOfAllEligibleTotals)
                .append(costCategoryGroupMap, that.costCategoryGroupMap)
                .append(costCategoryResourceMap, that.costCategoryResourceMap)
                .append(projectManager, that.projectManager)
                .append(approved, that.approved)
                .append(leadPartner, that.leadPartner)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(objectErrors)
                .append(projectId)
                .append(applicationId)
                .append(organisationId)
                .append(projectName)
                .append(organisationName)
                .append(targetProjectStartDate)
                .append(durationInMonths)
                .append(summary)
                .append(table)
                .append(markedAsComplete)
                .append(usingJesFinances)
                .append(categoryToActualTotal)
                .append(totalForEachMonth)
                .append(totalOfAllActualTotals)
                .append(totalOfAllEligibleTotals)
                .append(costCategoryGroupMap)
                .append(costCategoryResourceMap)
                .append(submitted)
                .append(userPartOfThisOrganisation)
                .append(projectManager)
                .append(approved)
                .append(leadPartner)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("objectErrors", objectErrors)
                .append("projectId", projectId)
                .append("applicationId", applicationId)
                .append("organisationId", organisationId)
                .append("projectName", projectName)
                .append("organisationName", organisationName)
                .append("targetProjectStartDate", targetProjectStartDate)
                .append("durationInMonths", durationInMonths)
                .append("summary", summary)
                .append("table", table)
                .append("markedAsComplete", markedAsComplete)
                .append("usingJesFinances", usingJesFinances)
                .append("categoryToActualTotal", categoryToActualTotal)
                .append("totalForEachMonth", totalForEachMonth)
                .append("totalOfAllActualTotals", totalOfAllActualTotals)
                .append("totalOfAllEligibleTotals", totalOfAllEligibleTotals)
                .append("costCategoryGroupMap", costCategoryGroupMap)
                .append("costCategoryResourceMap", costCategoryResourceMap)
                .append("submitted", submitted)
                .append("userPartOfThisOrganisation", userPartOfThisOrganisation)
                .append("projectManager", projectManager)
                .append("approved", approved)
                .append("leadPartner", leadPartner)
                .toString();
    }
}
