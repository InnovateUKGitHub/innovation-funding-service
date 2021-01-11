package org.innovateuk.ifs.project.eligibility.populator;

import org.innovateuk.ifs.application.finance.viewmodel.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    public ProjectFinanceChangesViewModel getProjectFinanceChangesViewModel(boolean isInternal, ProjectResource project,
                                                                          OrganisationResource organisation) {
        FinanceCheckEligibilityResource eligibilityOverview = financeCheckRestService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId()).getSuccess();
        ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(project.getId(), organisation.getId()).getSuccess();
        ApplicationFinanceResource appFinanceResource = applicationFinanceRestService.getFinanceDetails(project.getApplication(), organisation.getId()).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        ProjectFinanceChangesFinanceSummaryViewModel projectFinanceChangesFinanceSummaryViewModel = new ProjectFinanceChangesFinanceSummaryViewModel(new ArrayList<>());

        List<CostChangeViewModel> sectionDifferences = buildSectionDifferencesMap(appFinanceResource.getFinanceOrganisationDetails(), projectFinanceResource.getFinanceOrganisationDetails());
        ProjectFinanceChangesProjectFinancesViewModel projectFinanceChangesProjectFinancesViewModel = new ProjectFinanceChangesProjectFinancesViewModel(sectionDifferences);

        ProjectFinanceChangesMilestoneDifferencesViewModel projectFinanceChangesMilestoneDifferencesViewModel = getMilestoneDifferencesViewModel(project, competition);

        return new ProjectFinanceChangesViewModel(isInternal, organisation.getName(), organisation.getId(), project.getName(), project.getApplication(), project.getId(),
                projectFinanceChangesFinanceSummaryViewModel,
                projectFinanceChangesProjectFinancesViewModel,
                projectFinanceChangesMilestoneDifferencesViewModel);
    }

    private ProjectFinanceChangesMilestoneDifferencesViewModel getMilestoneDifferencesViewModel(ProjectResource project, CompetitionResource competition) {
        ProjectFinanceChangesMilestoneDifferencesViewModel projectFinanceChangesMilestoneDifferencesViewModel;

        if (competition.isProcurement()) {
            List<ApplicationProcurementMilestoneResource> applicationMilestones = applicationProcurementMilestoneRestService.getByApplicationId(project.getApplication()).getSuccess();
            List<ProjectProcurementMilestoneResource> projectMilestones = projectProcurementMilestoneRestService.getByProjectId(project.getId()).getSuccess();

            List<MilestoneChangeViewModel> milestoneDifferences = buildMilestoneDifferences(applicationMilestones, projectMilestones);

            projectFinanceChangesMilestoneDifferencesViewModel = new ProjectFinanceChangesMilestoneDifferencesViewModel(milestoneDifferences);
        } else {
            projectFinanceChangesMilestoneDifferencesViewModel = null;
        }
        return projectFinanceChangesMilestoneDifferencesViewModel;
    }

    private LabourCost getWorkingDaysPerYearCostItemFrom(Map<FinanceRowType, FinanceRowCostCategory> financeDetails) {
        for (Map.Entry<FinanceRowType, FinanceRowCostCategory> entry : financeDetails.entrySet()) {
            FinanceRowType rowType = entry.getKey();
            if (rowType.getType().equals(FinanceRowType.LABOUR.getType())) {
                return ((LabourCostCategory) entry.getValue()).getWorkingDaysPerYearCostItem();
            }
        }
        throw new UnsupportedOperationException("Finance data is missing labour working days.  This is an unexpected state.");
    }

    private List<CostChangeViewModel> buildSectionDifferencesMap(Map<FinanceRowType, FinanceRowCostCategory> organisationApplicationFinances,
                                                                       Map<FinanceRowType, FinanceRowCostCategory> organisationProjectFinances) {
        List<CostChangeViewModel> sectionDifferences = new ArrayList<>();

        for (Map.Entry<FinanceRowType, FinanceRowCostCategory> entry : organisationProjectFinances.entrySet()) {
            FinanceRowType rowType = entry.getKey();
            FinanceRowCostCategory financeRowProjectCostCategory = entry.getValue();
            FinanceRowCostCategory financeRowAppCostCategory = organisationApplicationFinances.get(rowType);
            CostChangeViewModel costChange = new CostChangeViewModel();
            costChange.setSection(rowType.getDisplayName());
            costChange.setProjectCost(financeRowProjectCostCategory.getTotal());
            costChange.setApplicationCost(financeRowAppCostCategory.getTotal());
            sectionDifferences.add(costChange);
        }
        return sectionDifferences;
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
