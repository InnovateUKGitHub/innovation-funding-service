package com.worth.ifs.project.viewmodel;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
    private Long organisationId;
    private String projectName;
    private String organisationName;
    private LocalDate targetProjectStartDate;
    private Long durationInMonths;
    private SpendProfileSummaryModel summary;
    private SpendProfileTableResource table;
    private Boolean markedAsComplete;
    private Map<String, BigDecimal> categoryToActualTotal;
    private List<BigDecimal> totalForEachMonth;
    private BigDecimal totalOfAllActualTotals;
    private BigDecimal totalOfAllEligibleTotals;

    public ProjectSpendProfileViewModel(ProjectResource project, OrganisationResource organisationResource, SpendProfileTableResource table,
                                        SpendProfileSummaryModel summary, Boolean markedAsComplete,
                                        Map<String, BigDecimal> categoryToActualTotal, List<BigDecimal> totalForEachMonth,
                                        BigDecimal totalOfAllActualTotals, BigDecimal totalOfAllEligibleTotals) {
        this.projectId = project.getId();
        this.organisationId = organisationResource.getId();
        this.projectName = project.getName();
        this.organisationName = organisationResource.getName();
        this.targetProjectStartDate = project.getTargetStartDate();
        this.durationInMonths = project.getDurationInMonths();
        this.summary = summary;
        this.table = table;
        this.markedAsComplete = markedAsComplete;
        this.categoryToActualTotal = categoryToActualTotal;
        this.totalForEachMonth = totalForEachMonth;
        this.totalOfAllActualTotals = totalOfAllActualTotals;
        this.totalOfAllEligibleTotals = totalOfAllEligibleTotals;
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

    public Map<String, BigDecimal> getCategoryToActualTotal() {
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

    public void setCategoryToActualTotal(Map<String, BigDecimal> categoryToActualTotal) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileViewModel viewModel = (ProjectSpendProfileViewModel) o;

        return new EqualsBuilder()
                .append(objectErrors, viewModel.objectErrors)
                .append(projectId, viewModel.projectId)
                .append(organisationId, viewModel.organisationId)
                .append(projectName, viewModel.projectName)
                .append(organisationName, viewModel.organisationName)
                .append(targetProjectStartDate, viewModel.targetProjectStartDate)
                .append(durationInMonths, viewModel.durationInMonths)
                .append(summary, viewModel.summary)
                .append(table, viewModel.table)
                .append(markedAsComplete, viewModel.markedAsComplete)
                .append(categoryToActualTotal, viewModel.categoryToActualTotal)
                .append(totalForEachMonth, viewModel.totalForEachMonth)
                .append(totalOfAllActualTotals, viewModel.totalOfAllActualTotals)
                .append(totalOfAllEligibleTotals, viewModel.totalOfAllEligibleTotals)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(objectErrors)
                .append(projectId)
                .append(organisationId)
                .append(projectName)
                .append(targetProjectStartDate)
                .append(durationInMonths)
                .append(summary)
                .append(table)
                .append(markedAsComplete)
                .append(categoryToActualTotal)
                .append(totalForEachMonth)
                .append(totalOfAllActualTotals)
                .append(totalOfAllEligibleTotals)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("objectErrors", objectErrors)
                .append("projectId", projectId)
                .append("organisationId", organisationId)
                .append("projectName", projectName)
                .append("organisationName", organisationName)
                .append("targetProjectStartDate", targetProjectStartDate)
                .append("durationInMonths", durationInMonths)
                .append("summary", summary)
                .append("table", table)
                .append("markedAsComplete", markedAsComplete)
                .append("categoryToActualTotal", categoryToActualTotal)
                .append("totalForEachMonth", totalForEachMonth)
                .append("totalOfAllActualTotals", totalOfAllActualTotals)
                .append("totalOfAllEligibleTotals", totalOfAllEligibleTotals)
                .toString();
    }
}