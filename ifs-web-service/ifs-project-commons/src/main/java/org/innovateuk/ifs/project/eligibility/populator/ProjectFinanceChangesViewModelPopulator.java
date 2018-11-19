package org.innovateuk.ifs.project.eligibility.populator;

import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ProjectFinanceChangesViewModelPopulator {

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;


    public ProjectFinanceChangesViewModel getProjectFinanceChangesViewModel(boolean isInternal, ProjectResource project,
                                                                          OrganisationResource organisation, Long userId) {
    FinanceCheckEligibilityResource eligibilityOverview = financeCheckRestService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId()).getSuccess();
    ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(project.getId(), organisation.getId()).getSuccess();
    ApplicationFinanceResource appFinanceResource = applicationFinanceRestService.getFinanceDetails(project.getApplication(), organisation.getId()).getSuccess();
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
