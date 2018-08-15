package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceViewModel;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Managing all the view attributes for the finances
 */
@Component
public class DefaultProjectFinanceModelManager implements FinanceModelManager {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private FinanceService applicationFinanceService;

    @Autowired
    private OrganisationTypeRestService organisationTypeService;

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Override
    public void addOrganisationFinanceDetails(Model model, Long projectId, List<QuestionResource> costsQuestions, Long userId, Form form, Long organisationId) {

        ProjectFinanceResource projectFinanceResource = getOrganisationFinances(projectId, userId, organisationId);

        if (projectFinanceResource != null) {
            OrganisationTypeResource organisationType = organisationTypeService.getForOrganisationId(projectFinanceResource.getOrganisation()).getSuccess();
            model.addAttribute("organisationFinance", projectFinanceResource.getFinanceOrganisationDetails());
            model.addAttribute("organisationFinanceSize", projectFinanceResource.getOrganisationSize());
            model.addAttribute("organisationType", organisationType);
            model.addAttribute("organisationFinanceId", projectFinanceResource.getId());
            model.addAttribute("organisationFinanceTotal", projectFinanceResource.getTotal());
            model.addAttribute("financeView", "finance");
            addGrantClaim(model, form, projectFinanceResource);
        }
    }

    private void addGrantClaim(Model model, Form form, ProjectFinanceResource projectFinanceResource) {
        if (projectFinanceResource.getGrantClaim() != null) {
            model.addAttribute("organisationGrantClaimPercentage", projectFinanceResource.getGrantClaim().getGrantClaimPercentage());
            model.addAttribute("organisationgrantClaimPercentageId", projectFinanceResource.getGrantClaim().getId());
            String formInputKey = "finance-grantclaimpercentage-" + projectFinanceResource.getGrantClaim();
            String formInputValue = projectFinanceResource.getGrantClaimPercentage() != null ? projectFinanceResource.getGrantClaimPercentage().toString() : "";
            form.addFormInput(formInputKey, formInputValue);
        }
    }

    @Override
    public FinanceViewModel getFinanceViewModel(Long projectId, List<QuestionResource> costsQuestions, Long userId, Form form, Long organisationId) {
        ProjectFinanceViewModel financeViewModel = new ProjectFinanceViewModel();
        ProjectFinanceResource projectFinanceResource = getOrganisationFinances(projectId, userId, organisationId);

        if (projectFinanceResource != null) {
            OrganisationTypeResource organisationType = organisationTypeService.getForOrganisationId(projectFinanceResource.getOrganisation()).getSuccess();
            financeViewModel.setOrganisationFinance(projectFinanceResource.getFinanceOrganisationDetails());
            financeViewModel.setOrganisationFinanceSize(projectFinanceResource.getOrganisationSize());
            financeViewModel.setOrganisationType(organisationType);
            financeViewModel.setOrganisationFinanceId(projectFinanceResource.getId());
            financeViewModel.setOrganisationFinanceTotal(projectFinanceResource.getTotal());
            financeViewModel.setFinanceView("finance");
            addGrantClaim(financeViewModel, form, projectFinanceResource);
        }
        return financeViewModel;
    }

    private void addGrantClaim(FinanceViewModel financeViewModel, Form form, ProjectFinanceResource projectFinanceResource) {
        if (projectFinanceResource.getGrantClaim() != null) {
            financeViewModel.setOrganisationGrantClaimPercentage(projectFinanceResource.getGrantClaim().getGrantClaimPercentage());
            financeViewModel.setOrganisationGrantClaimPercentageId(projectFinanceResource.getGrantClaim().getId());
            String formInputKey = "finance-grantclaimpercentage-" + projectFinanceResource.getGrantClaim();
            String formInputValue = projectFinanceResource.getGrantClaimPercentage() != null ? projectFinanceResource.getGrantClaimPercentage().toString() : "";
            form.addFormInput(formInputKey, formInputValue);
        }
    }

    protected ProjectFinanceResource getOrganisationFinances(Long projectId, Long userId, Long organisationId) {
        ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        if (projectFinanceResource == null) {
            projectFinanceRestService.addProjectFinanceForOrganisation(userId, projectId).getSuccess();
            // ugly fix since the addProjectFinance method does not return the correct results.
            projectFinanceResource = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        }
        return projectFinanceResource;
    }

    @Override
    public void addCost(Model model, FinanceRowItem costItem, long projectId, long organisationId, long userId, Long questionId, FinanceRowType costType) {
        if (FinanceRowType.LABOUR == costType) {
            ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
            LabourCostCategory costCategory = (LabourCostCategory) projectFinanceResource.getFinanceOrganisationDetails(FinanceRowType.LABOUR);
            model.addAttribute("costCategory", costCategory);
        }
        model.addAttribute("type", costType.getType());
        model.addAttribute("question", questionService.getById(questionId));
        model.addAttribute("cost", costItem);
    }

    public ProjectFinanceChangesViewModel getProjectFinanceChangesViewModel(boolean isInternal, ProjectResource project,
                                                                            OrganisationResource organisation, Long userId) {
        FinanceCheckEligibilityResource eligibilityOverview = financeCheckRestService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId()).getSuccess();
        ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(project.getId(), organisation.getId()).getSuccess();
        ApplicationFinanceResource appFinanceResource = applicationFinanceService.getApplicationFinanceDetails(userId, project.getApplication(), organisation.getId());
        Map<FinanceRowType, BigDecimal> sectionDifferencesMap = buildSectionDifferencesMap(appFinanceResource.getFinanceOrganisationDetails(), projectFinanceResource.getFinanceOrganisationDetails());
        return new ProjectFinanceChangesViewModel(isInternal, organisation.getName(), organisation.getId(), project.getName(), project.getApplication(), project.getId(), eligibilityOverview,
                getWorkingDaysPerYearCostItemFrom(appFinanceResource.getFinanceOrganisationDetails()),
                getWorkingDaysPerYearCostItemFrom(projectFinanceResource.getFinanceOrganisationDetails()),
                sectionDifferencesMap, projectFinanceResource.getCostChanges(), appFinanceResource.getTotal(), projectFinanceResource.getTotal());
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

    private Map<FinanceRowType, BigDecimal> buildSectionDifferencesMap(Map<FinanceRowType, FinanceRowCostCategory> organisationApplicationFinances,
                                                                       Map<FinanceRowType, FinanceRowCostCategory> organisationProjectFinances) {
        Map<FinanceRowType, BigDecimal> sectionDifferencesMap = new LinkedHashMap<>();

        for (Map.Entry<FinanceRowType, FinanceRowCostCategory> entry : organisationProjectFinances.entrySet()) {
            FinanceRowType rowType = entry.getKey();
            FinanceRowCostCategory financeRowProjectCostCategory = entry.getValue();
            FinanceRowCostCategory financeRowAppCostCategory = organisationApplicationFinances.get(rowType);
            sectionDifferencesMap.put(rowType, financeRowProjectCostCategory.getTotal().subtract(financeRowAppCostCategory.getTotal()));
        }
        return sectionDifferencesMap;
    }
}
