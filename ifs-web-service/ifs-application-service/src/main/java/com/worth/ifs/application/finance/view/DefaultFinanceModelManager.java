package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * Managing all the view attributes for the finances
 */
@Component
public class DefaultFinanceModelManager implements FinanceModelManager {

    @Autowired
    QuestionService questionService;

    @Autowired
    FinanceService financeService;

    @Autowired
    OrganisationService organisationService;

    @Override
    public void addOrganisationFinanceDetails(Model model, Long applicationId, Long userId, Form form) {
        ApplicationFinanceResource applicationFinanceResource = getOrganisationFinances(applicationId, userId);
        Organisation organisation = organisationService.getOrganisationById(applicationFinanceResource.getOrganisation());
        model.addAttribute("organisationFinance", applicationFinanceResource.getFinanceOrganisationDetails());
        model.addAttribute("organisationFinanceSize", applicationFinanceResource.getOrganisationSize());
        model.addAttribute("organisationType", organisation.getOrganisationType());
        model.addAttribute("organisationFinanceId", applicationFinanceResource.getId());
        model.addAttribute("organisationFinanceTotal", applicationFinanceResource.getTotal());
        model.addAttribute("financeView", "finance");
        addGrantClaim(model, form, applicationFinanceResource);
    }

    private void addGrantClaim(Model model, Form form, ApplicationFinanceResource applicationFinanceResource) {
        if(applicationFinanceResource.getGrantClaim()!=null) {
            model.addAttribute("organisationGrantClaimPercentage", applicationFinanceResource.getGrantClaim().getGrantClaimPercentage());
            model.addAttribute("organisationgrantClaimPercentageId", applicationFinanceResource.getGrantClaim().getId());
            String formInputKey = "finance-grantclaim-" + applicationFinanceResource.getGrantClaim();
            String formInputValue = applicationFinanceResource.getGrantClaimPercentage() != null ? applicationFinanceResource.getGrantClaimPercentage().toString() : "";
            form.addFormInput(formInputKey, formInputValue);
        }
    }

    protected ApplicationFinanceResource getOrganisationFinances(Long applicationId, Long userId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        if(applicationFinanceResource == null) {
            financeService.addApplicationFinance(userId, applicationId);
            // ugly fix since the addApplicationFinance method does not return the correct results.
            applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        }

        return applicationFinanceResource;
    }

    @Override
    public void addCost(Model model, CostItem costItem, long applicationId, long userId, Long questionId, String costType) {
        if (CostType.fromString(costType).equals(CostType.LABOUR)) {
            ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
            LabourCostCategory costCategory = (LabourCostCategory) applicationFinanceResource.getFinanceOrganisationDetails(CostType.fromString(costType));
            model.addAttribute("costCategory", costCategory);
        }

        model.addAttribute("type", costType);
        model.addAttribute("question", questionService.getById(questionId));
        model.addAttribute("cost", costItem);
    }
}
