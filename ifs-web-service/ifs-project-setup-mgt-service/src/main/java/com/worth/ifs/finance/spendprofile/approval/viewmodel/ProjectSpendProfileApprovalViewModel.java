package com.worth.ifs.finance.spendprofile.approval.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;

/**
 * TODO
 */
public class ProjectSpendProfileApprovalViewModel {

    private CompetitionSummaryResource competitionSummary;
    private String leadTechnologist;
    private Boolean isApproved;
    private Boolean isRejected;

    public ProjectSpendProfileApprovalViewModel(CompetitionSummaryResource competitionSummary, String leadTechnologist, Boolean isApproved, Boolean isRejected) {
        this.competitionSummary = competitionSummary;
        this.leadTechnologist = leadTechnologist;
        this.isApproved = isApproved;
        this.isRejected = isRejected;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public String getLeadTechnologist() {
        return leadTechnologist;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public Boolean getRejected() {
        return isRejected;
    }
}
