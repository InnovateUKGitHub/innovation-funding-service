package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.form.AcademicFinance;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.service.ProcessRoleService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.AcademicCost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.EnumMap;

@Component
public class JESFinanceModelManager implements FinanceModelManager {

    @Autowired
    ProcessRoleService processRoleService;

    @Autowired
    FinanceService financeService;

    @Override
    public void addOrganisationFinanceDetails(Model model, Long applicationId, Long userId, Form form) {
        ApplicationFinanceResource applicationFinanceResource = getOrganisationFinances(applicationId, userId);

        ProcessRole processRole = processRoleService.findProcessRole(userId, applicationId);
        String organisationName = processRole.getOrganisation().getName();
        EnumMap<CostType, CostCategory> organisationFinanceDetails = applicationFinanceResource.getFinanceOrganisationDetails();
        AcademicFinance academicFinance = mapFinancesToFields(organisationFinanceDetails, model);
        model.addAttribute("title", organisationName + " finances");
        model.addAttribute("financeView", "academic-finance");
        model.addAttribute("academicFinance", academicFinance);
    }

    @Override
    public void addCost(Model model, CostItem costItem, long applicationId, long userId, Long questionId, String costType) {

    }

    protected ApplicationFinanceResource getOrganisationFinances(Long applicationId, Long userId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        if(applicationFinanceResource == null) {
            financeService.addApplicationFinance(userId, applicationId);
            applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        }
        return applicationFinanceResource;
    }

    protected AcademicFinance mapFinancesToFields(EnumMap<CostType, CostCategory> organisationFinanceDetails, Model model) {
        AcademicFinance academicFinance = new AcademicFinance();
        organisationFinanceDetails.values()
                .stream()
                .flatMap(cc -> cc.getCosts().stream())
                .forEach(c -> mapFinanceToField((AcademicCost)c, academicFinance));
        return academicFinance;
    }

    private void mapFinanceToField(AcademicCost cost, AcademicFinance academicFinance) {
        String description = cost.getDescription();
        BigDecimal total = cost.getTotal();
        switch (description) {
            case "incurred_staff":
                academicFinance.setIncurredStaff(total);
                break;
            case "incurred_travel_subsistence":
                academicFinance.setIncurredTravelAndSubsistence(total);
                break;
            case "incurred_other_costs":
                academicFinance.setIncurredOtherCosts(total);
                break;
            case "allocated_investigators":
                academicFinance.setAllocatedInvestigators(total);
                break;
            case "allocated_estates_costs":
                academicFinance.setAllocatedEstatesCosts(total);
                break;
            case "allocated_other_costs":
                academicFinance.setAllocatedOtherCosts(total);
                break;
            case "indirect_costs":
                academicFinance.setIndirectCosts(total);
                break;
            case "exceptions_staff":
                academicFinance.setExceptionsStaff(total);
                break;
            case "exceptions_other_costs":
                academicFinance.setExceptionsStaff(total);
                break;
        }
    }
 }
