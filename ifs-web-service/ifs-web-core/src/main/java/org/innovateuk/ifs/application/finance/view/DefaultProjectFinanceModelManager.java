package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Managing all the view attributes for the finances
 */
@Component
public class DefaultProjectFinanceModelManager implements FinanceModelManager {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProjectFinanceService financeService;

    @Autowired
    private OrganisationTypeRestService organisationTypeService;
    
    @Autowired
    private FinanceHandler financeHandler;
    
    @Autowired
    private OrganisationService organisationService;
    
    @Autowired
    private FormInputService formInputService;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private CompetitionService competitionService;
    
    @Override
    public void addOrganisationFinanceDetails(Model model, Long projectId, List<QuestionResource> costsQuestions, Long userId, Form form, Long organisationId) {

        ProjectFinanceResource projectFinanceResource = getOrganisationFinances(projectId, costsQuestions, userId, organisationId);

        if (projectFinanceResource != null) {
            OrganisationTypeResource organisationType = organisationTypeService.getForOrganisationId(projectFinanceResource.getOrganisation()).getSuccessObjectOrThrowException();
            model.addAttribute("organisationFinance", projectFinanceResource.getFinanceOrganisationDetails());
            model.addAttribute("organisationFinanceSize", projectFinanceResource.getOrganisationSize());
            model.addAttribute("organisationType", organisationType);
            model.addAttribute("organisationFinanceId", projectFinanceResource.getId());
            model.addAttribute("organisationFinanceTotal", projectFinanceResource.getTotal());
            model.addAttribute("organisationTotalFundingSought", projectFinanceResource.getTotalFundingSought());
            model.addAttribute("organisationTotalContribution", projectFinanceResource.getTotalContribution());
            model.addAttribute("organisationTotalOtherFunding", projectFinanceResource.getTotalOtherFunding());
            model.addAttribute("financeView", "finance");
            addGrantClaim(model, form, projectFinanceResource);
        }
    }

    private void addGrantClaim(Model model, Form form, ProjectFinanceResource projectFinanceResource) {
        if(projectFinanceResource.getGrantClaim()!=null) {
            model.addAttribute("organisationGrantClaimPercentage", projectFinanceResource.getGrantClaim().getGrantClaimPercentage());
            model.addAttribute("organisationgrantClaimPercentageId", projectFinanceResource.getGrantClaim().getId());
            String formInputKey = "finance-grantclaimpercentage-" + projectFinanceResource.getGrantClaim();
            String formInputValue = projectFinanceResource.getGrantClaimPercentage() != null ? projectFinanceResource.getGrantClaimPercentage().toString() : "";
            form.addFormInput(formInputKey, formInputValue);
        }
    }

    protected ProjectFinanceResource getOrganisationFinances(Long projectId, List<QuestionResource> costsQuestions, Long userId, Long organisationId) {
        ProjectFinanceResource projectFinanceResource = financeService.getProjectFinance(projectId, organisationId);
        if(projectFinanceResource == null) {
            financeService.addProjectFinance(userId, projectId);
            // ugly fix since the addApplicationFinance method does not return the correct results.
            projectFinanceResource = financeService.getProjectFinance(projectId, organisationId);
        }

        String organisationType = organisationService.getOrganisationById(organisationId).getOrganisationTypeName();

        // TODO: INFUND-4834 Check if we need any condition checks here and add them (after merge of approval branch)
        // CompetitionResource competition = competitionService.getById(application.getCompetition());
        //if(!application.hasBeenSubmitted() && competition.isOpen()) {
	        // add cost for each cost question
	        for(QuestionResource question: costsQuestions) {
	        	FinanceRowType costType = costTypeForQuestion(question);
	        	if(costType != null) {
		        	FinanceRowCostCategory category = projectFinanceResource.getFinanceOrganisationDetails(costType);
		            FinanceRowItem costItem = financeHandler.getProjectFinanceFormHandler(organisationType).addCostWithoutPersisting(projectId, organisationId, question.getId());
		        	category.addCost(costItem);
	        	}
	        }
        //}

        return projectFinanceResource;
    }

    private FinanceRowType costTypeForQuestion(QuestionResource question) {
    	List<FormInputResource> formInputs = formInputService.findApplicationInputsByQuestion(question.getId());
    	if(formInputs.isEmpty()) {
    		return null;
    	}
    	for(FormInputResource formInput: formInputs) {
    		FormInputType formInputType = formInput.getType();
        	if(StringUtils.isEmpty(formInputType)){
        		continue;
        	}
        	try {
        		return FinanceRowType.fromType(formInputType);
        	} catch(IllegalArgumentException e) {
        		continue;
        	}
    	}
    	return null;
	}

	@Override
    public void addCost(Model model, FinanceRowItem costItem, long projectId, long organisationId, long userId, Long questionId, FinanceRowType costType) {
        if (FinanceRowType.LABOUR == costType) {
            ProjectFinanceResource projectFinanceResource = financeService.getProjectFinance(projectId, organisationId);
            LabourCostCategory costCategory = (LabourCostCategory) projectFinanceResource.getFinanceOrganisationDetails(FinanceRowType.LABOUR);
            model.addAttribute("costCategory", costCategory);
        }

        model.addAttribute("type", costType.getType());
        model.addAttribute("question", questionService.getById(questionId));
        model.addAttribute("cost", costItem);
    }
}
