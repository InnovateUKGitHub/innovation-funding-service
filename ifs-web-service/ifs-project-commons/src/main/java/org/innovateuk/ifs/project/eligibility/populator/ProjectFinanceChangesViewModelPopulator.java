package org.innovateuk.ifs.project.eligibility.populator;

import org.innovateuk.ifs.application.finance.viewmodel.*;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ProjectFinanceChangesViewModelPopulator {

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    @Autowired
    private ApplicationProcurementMilestoneRestService applicationProcurementMilestoneRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;


    public ProjectFinanceChangesViewModel getProjectFinanceChangesViewModel(boolean isInternal, ProjectResource project,
                                                                          OrganisationResource organisation) {
        FinanceCheckEligibilityResource eligibilityOverview = financeCheckRestService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId()).getSuccess();
        List<ProcessRoleResource> applicationProcessRoles = processRoleRestService.findProcessRole(project.getApplication()).getSuccess();
        boolean orgPresentOnApplication = applicationProcessRoles.stream().anyMatch(apr ->
            apr.getOrganisationId() != null && apr.getOrganisationId().equals(organisation.getId())
        );
        ApplicationFinanceResource appFinanceResource;
        if (orgPresentOnApplication) {
            appFinanceResource = applicationFinanceRestService.getFinanceDetails(project.getApplication(), organisation.getId()).getSuccess();
        } else {
            appFinanceResource = new ApplicationFinanceResource();
        }

        boolean isLead = applicationProcessRoles.stream().anyMatch(apr -> organisation.getId().equals(apr.getOrganisationId()) && apr.getRole() == ProcessRoleType.LEADAPPLICANT);

        ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(project.getId(), organisation.getId()).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        ProjectFinanceChangesProjectFinancesViewModel projectFinanceChangesProjectFinancesViewModel = getProjectFinancesViewModel(competition, organisation, appFinanceResource, projectFinanceResource);
        ProjectFinanceChangesFinanceSummaryViewModel projectFinanceChangesFinanceSummaryViewModel = getFinanceSummaryViewModel(competition, appFinanceResource, eligibilityOverview, isLead,
                projectFinanceChangesProjectFinancesViewModel.getTotalProjectCosts());
        ProjectFinanceChangesMilestoneDifferencesViewModel projectFinanceChangesMilestoneDifferencesViewModel = getMilestoneDifferencesViewModel(project, organisation, competition);

        return new ProjectFinanceChangesViewModel(isInternal, organisation.getName(), organisation.getId(), project.getName(), project.getApplication(), project.getId(),
                competition.isProcurement(),
                orgPresentOnApplication,
                projectFinanceChangesFinanceSummaryViewModel,
                projectFinanceChangesProjectFinancesViewModel,
                projectFinanceChangesMilestoneDifferencesViewModel);
    }

    private ProjectFinanceChangesProjectFinancesViewModel getProjectFinancesViewModel(CompetitionResource competition, OrganisationResource organisation, ApplicationFinanceResource appFinance, ProjectFinanceResource projectFinance) {

        List<CostChangeViewModel> sectionDifferences = new ArrayList<>();
        CostChangeViewModel vat = null;

        for (Map.Entry<FinanceRowType, FinanceRowCostCategory> entry : projectFinance.getFinanceOrganisationDetails().entrySet()) {
            FinanceRowType rowType = entry.getKey();
            if (Arrays.asList(FinanceRowType.OTHER_FUNDING, FinanceRowType.GRANT_CLAIM_AMOUNT, FinanceRowType.FINANCE).contains(rowType)) {
                continue;
            }
            if (rowType == FinanceRowType.YOUR_FINANCE && !OrganisationTypeEnum.isResearch(organisation.getOrganisationType())) {
                continue;
            }

            FinanceRowCostCategory financeRowProjectCostCategory = entry.getValue();
            FinanceRowCostCategory financeRowAppCostCategory = appFinance.getFinanceOrganisationDetails().get(rowType);
            BigDecimal financeRowAppCostCategoryTotal;
            if (financeRowAppCostCategory == null) {
                financeRowAppCostCategoryTotal = null;
            } else {
                financeRowAppCostCategoryTotal = financeRowAppCostCategory.getTotal();
            }

            String section = sectionName(competition, rowType);
            CostChangeViewModel costChange = new CostChangeViewModel(section,
                    financeRowAppCostCategoryTotal,
                    financeRowProjectCostCategory.getTotal());

            if (rowType == FinanceRowType.VAT) {
                vat = costChange;
            }

            sectionDifferences.add(costChange);
        }

        boolean vatRegistered = appFinance.isVatRegistered();
        ProjectFinanceChangesProjectFinancesViewModel projectFinanceChangesProjectFinancesViewModel = new ProjectFinanceChangesProjectFinancesViewModel(sectionDifferences, vatRegistered, vat);
        return projectFinanceChangesProjectFinancesViewModel;
    }

    private String sectionName(CompetitionResource competition, FinanceRowType rowType) {
        if (FundingType.GRANT == competition.getFundingType() && FinanceRowType.OVERHEADS == rowType) {
            return "Overhead costs";
        }
        if (FinanceRowType.PROCUREMENT_OVERHEADS == rowType) {
            return "Overhead costs";
        }
        if (FinanceRowType.YOUR_FINANCE == rowType) {
            return "Your finance";
        }
        return rowType.getDisplayName();
    }

    private ProjectFinanceChangesFinanceSummaryViewModel getFinanceSummaryViewModel(CompetitionResource competition, ApplicationFinanceResource appFinanceResource, FinanceCheckEligibilityResource eligibilityOverview, boolean isLead, CostChangeViewModel totalProjectCosts) {
        if (competition.isProcurement()) {
            return null;
        }
        List<CostChangeViewModel> entries = new ArrayList<>();
        entries.add(totalProjectCosts);

        entries.add(new CostChangeViewModel("Funding level (%)", appFinanceResource.getGrantClaimPercentage(), eligibilityOverview.getPercentageGrant()));
        entries.add(new CostChangeViewModel("Funding sought (£)", appFinanceResource.getTotalFundingSought(), eligibilityOverview.getFundingSought()));
        entries.add(new CostChangeViewModel("Other funding (£)", appFinanceResource.getTotalOtherFunding(), eligibilityOverview.getOtherPublicSectorFunding()));
        if (competition.isKtp()) {
            if (isLead) {
                entries.add(new CostChangeViewModel("Company contribution (%)", BigDecimal.ZERO, BigDecimal.ZERO));
                entries.add(new CostChangeViewModel("Company contribution (£)", BigDecimal.ZERO, BigDecimal.ZERO));
            } else {
                BigDecimal contributionPercentage = appFinanceResource.getTotalContribution().multiply(new BigDecimal("100")).divide(appFinanceResource.getTotal(), RoundingMode.HALF_UP);
                entries.add(new CostChangeViewModel("Company contribution (%)", contributionPercentage, eligibilityOverview.getContributionPercentage()));
                entries.add(new CostChangeViewModel("Company contribution (£)", appFinanceResource.getTotalContribution(), eligibilityOverview.getContributionToProject()));
            }
        } else {
            entries.add(new CostChangeViewModel("Contribution to project (£)", appFinanceResource.getTotalContribution(), eligibilityOverview.getContributionToProject()));
        }
        return new ProjectFinanceChangesFinanceSummaryViewModel(entries);
    }

    private ProjectFinanceChangesMilestoneDifferencesViewModel getMilestoneDifferencesViewModel(ProjectResource project, OrganisationResource organisationResource, CompetitionResource competition) {
        if (competition.isProcurementMilestones()) {
            List<ApplicationProcurementMilestoneResource> applicationMilestones = applicationProcurementMilestoneRestService.getByApplicationIdAndOrganisationId(project.getApplication(), organisationResource.getId()).getSuccess();
            List<ProjectProcurementMilestoneResource> projectMilestones = projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(project.getId(), organisationResource.getId()).getSuccess();

            List<MilestoneChangeViewModel> milestoneDifferences = buildMilestoneDifferences(applicationMilestones, projectMilestones);

            BigInteger applicationTotal = applicationMilestones.stream().map(res -> res.getPayment()).reduce(BigInteger::add).get();
            BigInteger projectTotal = projectMilestones.stream().map(res -> res.getPayment()).reduce(BigInteger::add).get();

            return new ProjectFinanceChangesMilestoneDifferencesViewModel(milestoneDifferences, applicationTotal, projectTotal);
        } else {
            return null;
        }
    }

    private List<MilestoneChangeViewModel> buildMilestoneDifferences(List<ApplicationProcurementMilestoneResource> applicationMilestones, List<ProjectProcurementMilestoneResource> projectMilestones) {

        List<MilestoneChangeViewModel> added = projectMilestones.stream()
                .filter(projectMilestone -> !applicationMilestones.stream()
                        .anyMatch(applicationMilestone -> applicationMilestone.getDescription().equals(projectMilestone.getDescription())))
                .map(milestone -> {
                    MilestoneChangeViewModel diff = new MilestoneChangeViewModel();
                    diff.setType(MilestoneChangeViewModel.MilestoneChangeType.ADDED);
                    diff.setDescription(milestone.getDescription());
                    diff.setMonthUpdated(milestone.getMonth());
                    diff.setPaymentUpdated(milestone.getPayment());
                    return diff;
                }).collect(Collectors.toList());

        List<MilestoneChangeViewModel> removed = applicationMilestones.stream()
                .filter(appMilestone -> !projectMilestones.stream()
                        .anyMatch(projectMilestone -> projectMilestone.getDescription().equals(appMilestone.getDescription())))
                .map(milestone -> {
                    MilestoneChangeViewModel diff = new MilestoneChangeViewModel();
                    diff.setType(MilestoneChangeViewModel.MilestoneChangeType.REMOVED);
                    diff.setDescription(milestone.getDescription());
                    diff.setMonthSubmitted(milestone.getMonth());
                    diff.setPaymentSubmitted(milestone.getPayment());
                    return diff;
                }).collect(Collectors.toList());

        List<MilestoneChangeViewModel> updated = applicationMilestones.stream()
                .map(appMilestone -> {
                    Optional<ProjectProcurementMilestoneResource> updatedProjectMilestone = projectMilestones.stream()
                            .filter(projectMilestone -> appMilestone.getDescription().equals(projectMilestone.getDescription())
                                    && (!appMilestone.getMonth().equals(projectMilestone.getMonth()) || !appMilestone.getPayment().equals(projectMilestone.getPayment()))).findAny();

                    if (updatedProjectMilestone.isPresent()) {
                        MilestoneChangeViewModel diff = new MilestoneChangeViewModel();
                        diff.setType(MilestoneChangeViewModel.MilestoneChangeType.UPDATED);
                        diff.setDescription(appMilestone.getDescription());
                        diff.setMonthSubmitted(appMilestone.getMonth());
                        diff.setPaymentSubmitted(appMilestone.getPayment());
                        diff.setMonthUpdated(updatedProjectMilestone.get().getMonth());
                        diff.setPaymentUpdated(updatedProjectMilestone.get().getPayment());
                        return diff;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Stream.concat(Stream.concat(added.stream(), removed.stream()), updated.stream()).collect(Collectors.toList());
    }

}
