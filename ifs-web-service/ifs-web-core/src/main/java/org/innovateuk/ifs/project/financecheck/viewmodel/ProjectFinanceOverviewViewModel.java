package org.innovateuk.ifs.project.financecheck.viewmodel;

import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * View model backing both internal and external views of the project finances overview section.
 */
public class ProjectFinanceOverviewViewModel {
    private Long projectId;
    private String projectName;
    private LocalDate projectStartDate;
    private int durationInMonths;
    private BigDecimal totalProjectCost;
    private BigDecimal grantAppliedFor;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal totalPercentageGrant;
    private BigDecimal researchParticipationPercentage;
    private BigDecimal competitionMaximumResearchPercentage;

    private ProjectFinanceOverviewViewModel() {}

    public ProjectFinanceOverviewViewModel(FinanceCheckOverviewResource overviewResource) {
        this.projectId = overviewResource.getProjectId();
        this.projectName = overviewResource.getProjectName();
        this.projectStartDate = overviewResource.getProjectStartDate();
        this.durationInMonths = overviewResource.getDurationInMonths();
        this.totalProjectCost = overviewResource.getTotalProjectCost();
        this.grantAppliedFor = overviewResource.getGrantAppliedFor();
        this.otherPublicSectorFunding = overviewResource.getOtherPublicSectorFunding();
        this.totalPercentageGrant = overviewResource.getTotalPercentageGrant();
        this.researchParticipationPercentage = overviewResource.getResearchParticipationPercentage();
        this.competitionMaximumResearchPercentage = overviewResource.getCompetitionMaximumResearchPercentage();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(int durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public BigDecimal getTotalProjectCost() {
        return totalProjectCost;
    }

    public void setTotalProjectCost(BigDecimal totalProjectCost) {
        this.totalProjectCost = totalProjectCost;
    }

    public BigDecimal getGrantAppliedFor() {
        return grantAppliedFor;
    }

    public void setGrantAppliedFor(BigDecimal grantAppliedFor) {
        this.grantAppliedFor = grantAppliedFor;
    }

    public BigDecimal getOtherPublicSectorFunding() {
        return otherPublicSectorFunding;
    }

    public void setOtherPublicSectorFunding(BigDecimal otherPublicSectorFunding) {
        this.otherPublicSectorFunding = otherPublicSectorFunding;
    }

    public BigDecimal getTotalPercentageGrant() {
        return totalPercentageGrant;
    }

    public void setTotalPercentageGrant(BigDecimal totalPercentageGrant) {
        this.totalPercentageGrant = totalPercentageGrant;
    }

    public BigDecimal getResearchParticipationPercentage() {
        return researchParticipationPercentage;
    }

    public void setResearchParticipationPercentage(BigDecimal researchParticipationPercentage) {
        this.researchParticipationPercentage = researchParticipationPercentage;
    }

    public BigDecimal getCompetitionMaximumResearchPercentage() {
        return competitionMaximumResearchPercentage;
    }

    public void setCompetitionMaximumResearchPercentage(BigDecimal competitionMaximumResearchPercentage) {
        this.competitionMaximumResearchPercentage = competitionMaximumResearchPercentage;
    }
}
