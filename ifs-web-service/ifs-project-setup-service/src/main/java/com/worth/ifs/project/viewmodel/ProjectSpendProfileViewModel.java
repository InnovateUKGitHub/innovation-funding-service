package com.worth.ifs.project.viewmodel;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
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
    private LocalDate targetProjectStartDate;
    private Long durationInMonths;
    private SpendProfileSummaryModel summary;
    private SpendProfileTableResource table;
    private Boolean markedAsComplete;
    private Map<String, BigDecimal> categoryToActualTotal;
    private List<BigDecimal> totalForEachMonth;
    private BigDecimal totalOfAllActualTotals;
    private BigDecimal totalOfAllEligibleTotals;
    private boolean submitted;

    public ProjectSpendProfileViewModel(ProjectResource project, Long organisationId, SpendProfileTableResource table,
                                        SpendProfileSummaryModel summary, Boolean markedAsComplete,
                                        Map<String, BigDecimal> categoryToActualTotal, List<BigDecimal> totalForEachMonth,
                                        BigDecimal totalOfAllActualTotals, BigDecimal totalOfAllEligibleTotals, boolean submitted) {
        this.projectId = project.getId();
        this.organisationId = organisationId;
        this.projectName = project.getName();
        this.targetProjectStartDate = project.getTargetStartDate();
        this.durationInMonths = project.getDurationInMonths();
        this.summary = summary;
        this.table = table;
        this.markedAsComplete = markedAsComplete;
        this.categoryToActualTotal = categoryToActualTotal;
        this.totalForEachMonth = totalForEachMonth;
        this.totalOfAllActualTotals = totalOfAllActualTotals;
        this.totalOfAllEligibleTotals = totalOfAllEligibleTotals;
        this.submitted = submitted;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
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

    public boolean isSubmitted() {
        return submitted;
    }

    @Override
    public String toString() {
        return "ProjectSpendProfileViewModel{" +
                "objectErrors=" + objectErrors +
                ", projectId=" + projectId +
                ", organisationId=" + organisationId +
                ", projectName='" + projectName + '\'' +
                ", targetProjectStartDate=" + targetProjectStartDate +
                ", durationInMonths=" + durationInMonths +
                ", summary=" + summary +
                ", table=" + table +
                ", markedAsComplete=" + markedAsComplete +
                ", categoryToActualTotal=" + categoryToActualTotal +
                ", totalForEachMonth=" + totalForEachMonth +
                ", totalOfAllActualTotals=" + totalOfAllActualTotals +
                ", totalOfAllEligibleTotals=" + totalOfAllEligibleTotals +
                ", submitted=" + submitted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileViewModel that = (ProjectSpendProfileViewModel) o;

        if (submitted != that.submitted) return false;
        if (objectErrors != null ? !objectErrors.equals(that.objectErrors) : that.objectErrors != null) return false;
        if (projectId != null ? !projectId.equals(that.projectId) : that.projectId != null) return false;
        if (organisationId != null ? !organisationId.equals(that.organisationId) : that.organisationId != null)
            return false;
        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;
        if (targetProjectStartDate != null ? !targetProjectStartDate.equals(that.targetProjectStartDate) : that.targetProjectStartDate != null)
            return false;
        if (durationInMonths != null ? !durationInMonths.equals(that.durationInMonths) : that.durationInMonths != null)
            return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        if (markedAsComplete != null ? !markedAsComplete.equals(that.markedAsComplete) : that.markedAsComplete != null)
            return false;
        if (categoryToActualTotal != null ? !categoryToActualTotal.equals(that.categoryToActualTotal) : that.categoryToActualTotal != null)
            return false;
        if (totalForEachMonth != null ? !totalForEachMonth.equals(that.totalForEachMonth) : that.totalForEachMonth != null)
            return false;
        if (totalOfAllActualTotals != null ? !totalOfAllActualTotals.equals(that.totalOfAllActualTotals) : that.totalOfAllActualTotals != null)
            return false;
        return totalOfAllEligibleTotals != null ? totalOfAllEligibleTotals.equals(that.totalOfAllEligibleTotals) : that.totalOfAllEligibleTotals == null;

    }

    @Override
    public int hashCode() {
        int result = objectErrors != null ? objectErrors.hashCode() : 0;
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        result = 31 * result + (organisationId != null ? organisationId.hashCode() : 0);
        result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
        result = 31 * result + (targetProjectStartDate != null ? targetProjectStartDate.hashCode() : 0);
        result = 31 * result + (durationInMonths != null ? durationInMonths.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (markedAsComplete != null ? markedAsComplete.hashCode() : 0);
        result = 31 * result + (categoryToActualTotal != null ? categoryToActualTotal.hashCode() : 0);
        result = 31 * result + (totalForEachMonth != null ? totalForEachMonth.hashCode() : 0);
        result = 31 * result + (totalOfAllActualTotals != null ? totalOfAllActualTotals.hashCode() : 0);
        result = 31 * result + (totalOfAllEligibleTotals != null ? totalOfAllEligibleTotals.hashCode() : 0);
        result = 31 * result + (submitted ? 1 : 0);
        return result;
    }
}