package org.innovateuk.ifs.project.funding.level.viewmodel;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.funding.level.form.ProjectFinanceFundingLevelForm;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectFinanceFundingLevelViewModel {

    private final long projectId;
    private final long applicationId;
    private final String projectName;
    private final List<ProjectFinancePartnerFundingLevelViewModel> partners;
    private final boolean collaborativeProject;
    private final boolean loanCompetition;
    private final boolean ktpCompetition;
    private final BigDecimal fundingAppliedFor;
    private final boolean complete;

    public ProjectFinanceFundingLevelViewModel(ProjectResource project,
                                               List<ProjectFinanceResource> finances,
                                               OrganisationResource lead,
                                               CompetitionResource competition,
                                               BigDecimal fundingAppliedFor) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.complete = project.getProjectState().isComplete();
        BigDecimal totalGrant = finances.stream().map(BaseFinanceResource::getTotalFundingSought)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.partners =  finances.stream()
                .map(pf -> new ProjectFinancePartnerFundingLevelViewModel(pf.getOrganisation(), pf.getOrganisationName(), pf.getOrganisation().equals(lead.getId()),
                        pf.getMaximumFundingLevel(), pf.getOrganisationSize(), pf.getTotal(), pf.getGrantClaimPercentage(),
                        pf.getTotalOtherFunding(), totalGrant, competition.isKtp()))
                .collect(Collectors.toList());
        this.collaborativeProject = project.isCollaborativeProject();
        this.loanCompetition = competition.isLoan();
        this.ktpCompetition = competition.isKtp();
        this.fundingAppliedFor = fundingAppliedFor;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<ProjectFinancePartnerFundingLevelViewModel> getPartners() {
        return partners;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public BigDecimal getFundingAppliedFor() {
        return fundingAppliedFor;
    }

    public boolean isComplete() {
        return complete;
    }

    /* View logic. */
    public BigDecimal getTotalFundingSought() {
        return partners.stream()
                .map(ProjectFinancePartnerFundingLevelViewModel::getFundingSought)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCosts() {
        return partners.stream()
                .map(ProjectFinancePartnerFundingLevelViewModel::getCosts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateFormFundingSought(ProjectFinanceFundingLevelForm form) {
        return form.getPartners()
                .entrySet()
                .stream()
                .map(entry ->
                     partners.stream()
                             .filter(partnerModel -> entry.getKey().equals(partnerModel.getId()))
                             .findFirst()
                             .orElseThrow(ObjectNotFoundException::new)
                             .calculateFundingSought(entry.getValue().getFundingLevel())
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
