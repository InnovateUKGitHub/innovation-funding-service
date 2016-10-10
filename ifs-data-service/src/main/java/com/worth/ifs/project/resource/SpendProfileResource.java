package com.worth.ifs.project.resource;

import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Calendar;

public class SpendProfileResource {

    private Long id;

    private Long organisation;

    private Long project;

    private Long costCategoryType;

    private CostGroupResource eligibleCosts;

    private CostGroupResource spendProfileFigures;

    private boolean markedAsComplete;

    private UserResource generatedBy;

    private Calendar generatedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public Long getCostCategoryType() {
        return costCategoryType;
    }

    public void setCostCategoryType(Long costCategoryType) {
        this.costCategoryType = costCategoryType;
    }

    public CostGroupResource getEligibleCosts() {
        return eligibleCosts;
    }

    public void setEligibleCosts(CostGroupResource eligibleCosts) {
        this.eligibleCosts = eligibleCosts;
    }

    public CostGroupResource getSpendProfileFigures() {
        return spendProfileFigures;
    }

    public void setSpendProfileFigures(CostGroupResource spendProfileFigures) {
        this.spendProfileFigures = spendProfileFigures;
    }

    public UserResource getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(UserResource generatedBy) {
        this.generatedBy = generatedBy;
    }

    public Calendar getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Calendar generatedDate) {
        this.generatedDate = generatedDate;
    }

    public boolean isMarkedAsComplete() {
        return markedAsComplete;
    }

    public void setMarkedAsComplete(boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpendProfileResource that = (SpendProfileResource) o;

        return new EqualsBuilder()
                .append(markedAsComplete, that.markedAsComplete)
                .append(id, that.id)
                .append(organisation, that.organisation)
                .append(project, that.project)
                .append(costCategoryType, that.costCategoryType)
                .append(eligibleCosts, that.eligibleCosts)
                .append(spendProfileFigures, that.spendProfileFigures)
                .append(generatedBy, that.generatedBy)
                .append(generatedDate, that.generatedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisation)
                .append(project)
                .append(costCategoryType)
                .append(eligibleCosts)
                .append(spendProfileFigures)
                .append(markedAsComplete)
                .append(generatedBy)
                .append(generatedDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("organisation", organisation)
                .append("project", project)
                .append("costCategoryType", costCategoryType)
                .append("eligibleCosts", eligibleCosts)
                .append("spendProfileFigures", spendProfileFigures)
                .append("markedAsComplete", markedAsComplete)
                .append("generatedBy", generatedBy)
                .append("generatedDate", generatedDate)
                .toString();
    }
}
