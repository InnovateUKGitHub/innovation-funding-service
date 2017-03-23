package org.innovateuk.ifs.project.bankdetails.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * A resource object wrapping bank detail statuses for all partners
 */
public class ProjectBankDetailsStatusSummary {
    private Long competitionId;
    private String competitionName;
    private Long projectId;
    private Long applicationId;
    private List<BankDetailsStatusResource> bankDetailsStatusResources;

    private String leadOrganisation;

    public ProjectBankDetailsStatusSummary() {
    }

    public ProjectBankDetailsStatusSummary(Long competitionId, String competitionName, Long projectId, Long applicationId,
                                           List<BankDetailsStatusResource> bankDetailsStatusResources, String leadOrganisation) {
        this.competitionId = competitionId;
        this.projectId = projectId;
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.bankDetailsStatusResources = bankDetailsStatusResources;
        this.leadOrganisation = leadOrganisation;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<BankDetailsStatusResource> getBankDetailsStatusResources() {
        return bankDetailsStatusResources;
    }

    public void setBankDetailsStatusResources(List<BankDetailsStatusResource> bankDetailsStatusResources) {
        this.bankDetailsStatusResources = bankDetailsStatusResources;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectBankDetailsStatusSummary that = (ProjectBankDetailsStatusSummary) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(projectId, that.projectId)
                .append(applicationId, that.applicationId)
                .append(bankDetailsStatusResources, that.bankDetailsStatusResources)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(projectId)
                .append(applicationId)
                .append(bankDetailsStatusResources)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionId", competitionId)
                .append("competitionName", competitionName)
                .append("projectId", projectId)
                .append("applicationId", applicationId)
                .append("bankDetailsStatusResources", bankDetailsStatusResources)
                .toString();
    }
}
