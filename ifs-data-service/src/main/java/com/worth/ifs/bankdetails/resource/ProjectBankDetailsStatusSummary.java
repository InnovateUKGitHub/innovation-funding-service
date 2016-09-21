package com.worth.ifs.bankdetails.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ProjectBankDetailsStatusSummary {
    private Long competitionId;
    private String formattedCompetitionId;
    private Long projectId;
    private String formattedProjectId;
    private List<BankDetailsStatusResource> bankDetailsStatusResources;

    public ProjectBankDetailsStatusSummary() {
    }

    public ProjectBankDetailsStatusSummary(Long competitionId, String formattedCompetitionId, Long projectId, String formattedProjectId, List<BankDetailsStatusResource> bankDetailsStatusResources) {
        this.competitionId = competitionId;
        this.formattedCompetitionId = formattedCompetitionId;
        this.projectId = projectId;
        this.formattedProjectId = formattedProjectId;
        this.bankDetailsStatusResources = bankDetailsStatusResources;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getFormattedCompetitionId() {
        return formattedCompetitionId;
    }

    public void setFormattedCompetitionId(String formattedCompetitionId) {
        this.formattedCompetitionId = formattedCompetitionId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getFormattedProjectId() {
        return formattedProjectId;
    }

    public void setFormattedProjectId(String formattedProjectId) {
        this.formattedProjectId = formattedProjectId;
    }

    public List<BankDetailsStatusResource> getBankDetailsStatusResources() {
        return bankDetailsStatusResources;
    }

    public void setBankDetailsStatusResources(List<BankDetailsStatusResource> bankDetailsStatusResources) {
        this.bankDetailsStatusResources = bankDetailsStatusResources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectBankDetailsStatusSummary that = (ProjectBankDetailsStatusSummary) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(formattedCompetitionId, that.formattedCompetitionId)
                .append(projectId, that.projectId)
                .append(formattedProjectId, that.formattedProjectId)
                .append(bankDetailsStatusResources, that.bankDetailsStatusResources)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(formattedCompetitionId)
                .append(projectId)
                .append(formattedProjectId)
                .append(bankDetailsStatusResources)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionId", competitionId)
                .append("formattedCompetitionId", formattedCompetitionId)
                .append("projectId", projectId)
                .append("formattedProjectId", formattedProjectId)
                .append("bankDetailsStatusResources", bankDetailsStatusResources)
                .toString();
    }
}
