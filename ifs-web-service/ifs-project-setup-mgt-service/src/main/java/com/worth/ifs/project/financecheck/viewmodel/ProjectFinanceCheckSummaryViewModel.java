package com.worth.ifs.project.financecheck.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * View model backing the internal Finance Team members view of the Spend Profile summary page
 */
public class ProjectFinanceCheckSummaryViewModel {

    public enum Viability {
        REVIEW,
        APPROVED,
    }

    public enum Eligibility {
        REVIEW,
        APPROVED,
    }

    public enum QueriesRaised {
        AWAITING_RESPONSE,
        VIEW,
    }

    public enum RagStatus {
        RED,
        AMBER,
        GREEN
    }

    public static class FinanceCheckOrganisationRow {

        private Long id;
        private String name;
        private Viability viability;
        private RagStatus viabilityRagStatus;
        private Eligibility eligibility;
        private RagStatus eligibilityRagStatus;
        private QueriesRaised queriesRaised;

        public FinanceCheckOrganisationRow(Long id, String name, Viability viability, RagStatus viabilityRagStatus, Eligibility eligibility, RagStatus eligibilityRagStatus, QueriesRaised queriesRaised) {
            this.id = id;
            this.name = name;
            this.viability = viability;
            this.viabilityRagStatus = viabilityRagStatus;
            this.eligibility = eligibility;
            this.eligibilityRagStatus = eligibilityRagStatus;
            this.queriesRaised = queriesRaised;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Viability getViability() {
            return viability;
        }

        public RagStatus getViabilityRagStatus() {
            return viabilityRagStatus;
        }

        public Eligibility getEligibility() {
            return eligibility;
        }

        public RagStatus getEligibilityRagStatus() {
            return eligibilityRagStatus;
        }

        public QueriesRaised getQueriesRaised() {
            return queriesRaised;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            FinanceCheckOrganisationRow that = (FinanceCheckOrganisationRow) o;

            return new EqualsBuilder()
                    .append(id, that.id)
                    .append(name, that.name)
                    .append(viability, that.viability)
                    .append(viabilityRagStatus, that.viabilityRagStatus)
                    .append(eligibility, that.eligibility)
                    .append(eligibilityRagStatus, that.eligibilityRagStatus)
                    .append(queriesRaised, that.queriesRaised)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(id)
                    .append(name)
                    .toHashCode();
        }
    }

    private Long projectId;
    private CompetitionSummaryResource competitionSummary;
    private List<FinanceCheckOrganisationRow> partnerOrganisationDetails;
    private LocalDate projectStartDate;
    private int durationInMonths;
    private BigDecimal totalProjectCost;
    private BigDecimal grantAppliedFor;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal totalPercentageGrant;
    private boolean spendProfilesGenerated;
    private boolean financeChecksAllApproved;

    public ProjectFinanceCheckSummaryViewModel(
            Long projectId, CompetitionSummaryResource competitionSummary,
            List<FinanceCheckOrganisationRow> partnerOrganisationDetails,
            LocalDate projectStartDate, int durationInMonths, BigDecimal totalProjectCost, BigDecimal grantAppliedFor,
            BigDecimal otherPublicSectorFunding, BigDecimal totalPercentageGrant, boolean spendProfilesGenerated, boolean financeChecksAllApproved) {

        this.projectId = projectId;
        this.competitionSummary = competitionSummary;
        this.partnerOrganisationDetails = partnerOrganisationDetails;
        this.projectStartDate = projectStartDate;
        this.durationInMonths = durationInMonths;
        this.totalProjectCost = totalProjectCost;
        this.grantAppliedFor = grantAppliedFor;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.totalPercentageGrant = totalPercentageGrant;
        this.spendProfilesGenerated = spendProfilesGenerated;
        this.financeChecksAllApproved = financeChecksAllApproved;
    }

    public Long getProjectId() {
        return projectId;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public List<FinanceCheckOrganisationRow> getPartnerOrganisationDetails() {
        return partnerOrganisationDetails;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public BigDecimal getTotalProjectCost() {
        return totalProjectCost;
    }

    public BigDecimal getGrantAppliedFor() {
        return grantAppliedFor;
    }

    public BigDecimal getOtherPublicSectorFunding() {
        return otherPublicSectorFunding;
    }

    public BigDecimal getTotalPercentageGrant() {
        return totalPercentageGrant;
    }

    public boolean isShowGenerateSpendProfilesButton() {
        return financeChecksAllApproved && !spendProfilesGenerated;
    }

    public boolean isShowSpendProfilesGeneratedMessage() {
        return spendProfilesGenerated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectFinanceCheckSummaryViewModel that = (ProjectFinanceCheckSummaryViewModel) o;

        return new EqualsBuilder()
                .append(durationInMonths, that.durationInMonths)
                .append(spendProfilesGenerated, that.spendProfilesGenerated)
                .append(financeChecksAllApproved, that.financeChecksAllApproved)
                .append(projectId, that.projectId)
                .append(competitionSummary, that.competitionSummary)
                .append(partnerOrganisationDetails, that.partnerOrganisationDetails)
                .append(projectStartDate, that.projectStartDate)
                .append(totalProjectCost, that.totalProjectCost)
                .append(grantAppliedFor, that.grantAppliedFor)
                .append(otherPublicSectorFunding, that.otherPublicSectorFunding)
                .append(totalPercentageGrant, that.totalPercentageGrant)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(competitionSummary)
                .append(partnerOrganisationDetails)
                .append(projectStartDate)
                .append(durationInMonths)
                .append(totalProjectCost)
                .append(grantAppliedFor)
                .append(otherPublicSectorFunding)
                .append(totalPercentageGrant)
                .append(spendProfilesGenerated)
                .append(financeChecksAllApproved)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("projectId", projectId)
                .append("competitionSummary", competitionSummary)
                .append("partnerOrganisationDetails", partnerOrganisationDetails)
                .append("projectStartDate", projectStartDate)
                .append("durationInMonths", durationInMonths)
                .append("totalProjectCost", totalProjectCost)
                .append("grantAppliedFor", grantAppliedFor)
                .append("otherPublicSectorFunding", otherPublicSectorFunding)
                .append("totalPercentageGrant", totalPercentageGrant)
                .append("spendProfilesGenerated", spendProfilesGenerated)
                .append("financeChecksAllApproved", financeChecksAllApproved)
                .toString();
    }
}
