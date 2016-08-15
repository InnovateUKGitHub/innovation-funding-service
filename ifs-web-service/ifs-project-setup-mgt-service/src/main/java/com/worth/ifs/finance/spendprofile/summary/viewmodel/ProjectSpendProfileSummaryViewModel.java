package com.worth.ifs.finance.spendprofile.summary.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * View model backing the internal Finance Team members view of the Spend Profile summary page
 */
public class ProjectSpendProfileSummaryViewModel {

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

    public static class SpendProfileOrganisationRow {

        private Long id;
        private String name;
        private Viability viability;
        private RagStatus viabilityRagStatus;
        private Eligibility eligibility;
        private RagStatus eligibilityRagStatus;
        private QueriesRaised queriesRaised;

        public SpendProfileOrganisationRow(Long id, String name, Viability viability, RagStatus viabilityRagStatus, Eligibility eligibility, RagStatus eligibilityRagStatus, QueriesRaised queriesRaised) {
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
    }

    private Long projectId;
    private CompetitionSummaryResource competitionSummary;
    private List<SpendProfileOrganisationRow> partnerOrganisationDetails;
    private LocalDate projectStartDate;
    private int durationInMonths;
    private BigDecimal totalProjectCost;
    private BigDecimal grantAppliedFor;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal totalPercentageGrant;
    private boolean spendProfilesGenerated;

    public ProjectSpendProfileSummaryViewModel(
            Long projectId, CompetitionSummaryResource competitionSummary,
            List<SpendProfileOrganisationRow> partnerOrganisationDetails,
            LocalDate projectStartDate, int durationInMonths, BigDecimal totalProjectCost, BigDecimal grantAppliedFor,
            BigDecimal otherPublicSectorFunding, BigDecimal totalPercentageGrant, boolean spendProfilesGenerated) {

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
    }

    public Long getProjectId() {
        return projectId;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public List<SpendProfileOrganisationRow> getPartnerOrganisationDetails() {
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
        return !spendProfilesGenerated;
    }

    public boolean isShowSpendProfilesGeneratedMessage() {
        return spendProfilesGenerated;
    }
}
