package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.service.OrganisationTypeRestService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

/**
 * Managing all the view attributes for the finances
 */
@Component
public class DefaultFinanceModelManager implements FinanceModelManager {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private OrganisationTypeRestService organisationTypeService;
    
    @Autowired
    private FinanceHandler financeHandler;
    
    @Autowired
    private OrganisationService organisationService;
    
    @Autowired
    private FormInputService formInputService;
    
    @Override
    public void addOrganisationFinanceDetails(Model model, Long applicationId, List<QuestionResource> costsQuestions, Long userId, Form form) {
        ApplicationFinanceResource applicationFinanceResource = getOrganisationFinances(applicationId, costsQuestions, userId);
        OrganisationTypeResource organisationType = organisationTypeService.getForOrganisationId(applicationFinanceResource.getOrganisation()).getSuccessObjectOrThrowException();
        model.addAttribute("organisationFinance", applicationFinanceResource.getFinanceOrganisationDetails());
        model.addAttribute("organisationFinanceSize", applicationFinanceResource.getOrganisationSize());
        model.addAttribute("organisationType", organisationType);
        model.addAttribute("organisationFinanceId", applicationFinanceResource.getId());
        model.addAttribute("organisationFinanceTotal", applicationFinanceResource.getTotal());
        model.addAttribute("financeView", "finance");
        addGrantClaim(model, form, applicationFinanceResource);
    }

    private void addGrantClaim(Model model, Form form, ApplicationFinanceResource applicationFinanceResource) {
        if(applicationFinanceResource.getGrantClaim()!=null) {
            model.addAttribute("organisationGrantClaimPercentage", applicationFinanceResource.getGrantClaim().getGrantClaimPercentage());
            model.addAttribute("organisationgrantClaimPercentageId", applicationFinanceResource.getGrantClaim().getId());
            String formInputKey = "finance-grantclaimpercentage-" + applicationFinanceResource.getGrantClaim();
            String formInputValue = applicationFinanceResource.getGrantClaimPercentage() != null ? applicationFinanceResource.getGrantClaimPercentage().toString() : "";
            form.addFormInput(formInputKey, formInputValue);
        }
    }

    protected ApplicationFinanceResource getOrganisationFinances(Long applicationId, List<QuestionResource> costsQuestions, Long userId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        if(applicationFinanceResource == null) {
            financeService.addApplicationFinance(userId, applicationId);
            // ugly fix since the addApplicationFinance method does not return the correct results.
            applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        }
        
        String organisationType = organisationService.getOrganisationType(userId, applicationId);
        
        // add cost for each cost question
        for(QuestionResource question: costsQuestions) {
        	CostType costType = costTypeForQuestion(question);
        	if(costType != null) {
	        	CostCategory category = applicationFinanceResource.getFinanceOrganisationDetails(costType);
	            CostItem costItem = financeHandler.getFinanceFormHandler(organisationType).addCostWithoutPersisting(applicationId, userId, question.getId());
	        	category.addCost(costItem);
        	}
        }

        return applicationFinanceResource;
    }

    private CostType costTypeForQuestion(QuestionResource question) {
    	List<FormInputResource> formInputs = formInputService.findByQuestion(question.getId());
    	if(formInputs.isEmpty()) {
    		return null;
    	}
    	for(FormInputResource formInput: formInputs) {
    		String formInputTypeName = formInput.getFormInputTypeTitle();
        	if(StringUtils.isEmpty(formInputTypeName)){
        		continue;
        	}
        	try {
        		return CostType.fromString(formInputTypeName);
        	} catch(IllegalArgumentException e) {
        		continue;
        	}
    	}
    	return null;
	}

	@Override
    public void addCost(Model model, CostItem costItem, long applicationId, long userId, Long questionId, String costType) {
        if (CostType.fromString(costType).equals(CostType.LABOUR)) {
            ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
            LabourCostCategory costCategory = (LabourCostCategory) applicationFinanceResource.getFinanceOrganisationDetails(CostType.LABOUR);
            model.addAttribute("costCategory", costCategory);
        }

        model.addAttribute("type", costType);
        model.addAttribute("question", questionService.getById(questionId));
        model.addAttribute("cost", costItem);
    }
}
