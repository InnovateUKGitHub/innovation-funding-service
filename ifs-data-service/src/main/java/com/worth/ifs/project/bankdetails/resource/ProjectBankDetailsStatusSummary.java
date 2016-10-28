package com.worth.ifs.project.bankdetails.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static com.worth.ifs.application.resource.ApplicationResource.formatter;

/**
 * A resource object wrapping bank detail statuses for all partners
 */
public class ProjectBankDetailsStatusSummary {
    private Long competitionId;
    private String competitionName;
    private Long projectId;
    private List<BankDetailsStatusResource> bankDetailsStatusResources;

    public ProjectBankDetailsStatusSummary() {
    }

    public ProjectBankDetailsStatusSummary(Long competitionId, String competitionName, Long projectId, List<BankDetailsStatusResource> bankDetailsStatusResources) {
        this.competitionId = competitionId;
        this.projectId = projectId;
        this.competitionName = competitionName;
        this.bankDetailsStatusResources = bankDetailsStatusResources;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    @JsonIgnore
    public String getFormattedCompetitionId() {
        return formatter.format(competitionId);
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @JsonIgnore
    public String getFormattedProjectId() {
        return formatter.format(projectId);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectBankDetailsStatusSummary that = (ProjectBankDetailsStatusSummary) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(projectId, that.projectId)
                .append(bankDetailsStatusResources, that.bankDetailsStatusResources)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(projectId)
                .append(bankDetailsStatusResources)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionId", competitionId)
                .append("competitionName", competitionName)
                .append("projectId", projectId)
                .append("bankDetailsStatusResources", bankDetailsStatusResources)
                .toString();
    }
}
