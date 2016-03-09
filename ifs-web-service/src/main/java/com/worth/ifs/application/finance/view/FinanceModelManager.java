package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Managing all the view attributes for the finances
 */
@Component
public class FinanceModelManager {

    SectionService sectionService;
    QuestionService questionService;
    FinanceService financeService;
    OrganisationService organisationService;
    ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    public FinanceModelManager(SectionService sectionService, QuestionService questionService, FinanceService financeService,
                               OrganisationService organisationService, ApplicationFinanceRestService applicationFinanceRestService) {
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.financeService = financeService;
        this.organisationService = organisationService;
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    // TODO DW - INFUND-1555 - handle rest results
    public void addFinanceDetails(Model model, ApplicationResource application) {
        addFinanceSections(model);
        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview(financeService, application.getId());
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        model.addAttribute("organisationFinances", organisationFinanceOverview.getApplicationFinances());
        model.addAttribute("totalFundingSought", organisationFinanceOverview.getTotalFundingSought());
        model.addAttribute("totalContribution", organisationFinanceOverview.getTotalContribution());
        model.addAttribute("totalOtherFunding", organisationFinanceOverview.getTotalOtherFunding());
        model.addAttribute("researchParticipationPercentage", applicationFinanceRestService.getResearchParticipationPercentage(application.getId()).getSuccessObjectOrThrowException());
    }

    private void addFinanceSections(Model model) {
        SectionResource section = sectionService.getByName("Your finances");
        sectionService.removeSectionsQuestionsWithType(section, "empty");

        model.addAttribute("financeSection", section);
        List<SectionResource> financeSectionChildren = simpleMap(section.getChildSections(), sectionService::getById);
        model.addAttribute("financeSectionChildren", financeSectionChildren);

        Map<Long, List<Question>> financeSectionChildrenQuestionsMap = financeSectionChildren.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> simpleMap(s.getQuestions(), questionService::getById)
                ));
        model.addAttribute("financeSectionChildrenQuestionsMap", financeSectionChildrenQuestionsMap);
    }

    public void addOrganisationFinanceDetails(Model model, ApplicationResource application, Long userId, Form form) {
        ApplicationFinanceResource applicationFinanceResource = getOrganisationFinances(application.getId(), userId);
        Organisation organisation = organisationService.getOrganisationById(applicationFinanceResource.getOrganisation());
        model.addAttribute("organisationFinance", applicationFinanceResource.getFinanceOrganisationDetails());
        model.addAttribute("organisationFinanceSize", applicationFinanceResource.getOrganisationSize());
        model.addAttribute("organisationType", organisation.getOrganisationType());
        model.addAttribute("organisationFinanceId", applicationFinanceResource.getId());
        model.addAttribute("organisationFinanceTotal", applicationFinanceResource.getTotal());
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
