package com.worth.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.handler.ApplicationFinanceHandler;
import com.worth.ifs.finance.handler.item.GrantClaimHandler;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.resource.category.OtherFundingCostCategory;
import com.worth.ifs.finance.resource.cost.OverheadRateType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.ApplicationFinanceRestServiceImpl} and other REST-API users
 * to manage {@link ApplicationFinance} related data.
 */
@RestController
@RequestMapping("/applicationfinance")
public class ApplicationFinanceController {
    public static final String RESEARCH_PARTICIPATION_PERCENTAGE = "researchParticipationPercentage";
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    QuestionService questionService;


    @Autowired
    ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    CostRepository costRepository;

    @RequestMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public ApplicationFinanceResource findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        return new ApplicationFinanceResource(applicationFinance);
    }

    @RequestMapping("/findByApplication/{applicationId}")
    public List<ApplicationFinanceResource> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {

        List<ApplicationFinance> applicationFinances = applicationFinanceRepository.findByApplicationId(applicationId);
        List<ApplicationFinanceResource> applicationFinanceResources = new ArrayList<>();
        if (applicationFinances != null) {
            applicationFinances.stream().forEach(af -> applicationFinanceResources.add(new ApplicationFinanceResource(af)));
        }
        return applicationFinanceResources;
    }

    @RequestMapping("/getResearchParticipationPercentage/{applicationId}")
    public ObjectNode getResearchParticipationPercentage(@PathVariable("applicationId") final Long applicationId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(RESEARCH_PARTICIPATION_PERCENTAGE, applicationFinanceHandler.getResearchParticipationPercentage(applicationId).doubleValue());
        return node;
    }

    @RequestMapping("/add/{applicationId}/{organisationId}")
    public ApplicationFinanceResource add(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {
        Application application = applicationRepository.findOne(applicationId);
        Organisation organisation = organisationRepository.findOne(organisationId);
        ApplicationFinance applicationFinance = new ApplicationFinance(application, organisation);

        applicationFinance = applicationFinanceRepository.save(applicationFinance);

        initialize(applicationFinance);

        return new ApplicationFinanceResource(applicationFinance);
    }

    /**
     * There are some objects that need a default value, and a id to use in the form,
     * so there are some objects that need to be created before loading the form.
     */
    private void initialize(ApplicationFinance applicationFinance) {
        initializeLabour(applicationFinance);
        initializeOverhead(applicationFinance);
        initializeFundingLevel(applicationFinance);
        initializeOtherFunding(applicationFinance);
    }

    private Cost initializeOverhead(ApplicationFinance applicationFinance) {
        String description = "Accept Rate";
        Question question = questionService.getQuestionById(29L);
        String item = OverheadRateType.NONE.name();
        Cost cost = new Cost(item, description, 0, null, applicationFinance, question);
        cost = costRepository.save(cost);
        return cost;
    }

    private Cost initializeFundingLevel(ApplicationFinance applicationFinance) {
        String description = GrantClaimHandler.GRANT_CLAIM;
        Question question = questionService.getQuestionById(38L);
        String item = null;
        Integer quantity = null; // the funding level
        Cost cost = new Cost(item, description, quantity, null, applicationFinance, question);
        cost = costRepository.save(cost);
        return cost;
    }

    private Cost initializeOtherFunding(ApplicationFinance applicationFinance) {
        String description = OtherFundingCostCategory.OTHER_FUNDING;
        Question question = questionService.getQuestionById(35L);
        String item = "";
        Integer quantity = null; // the funding level
        BigDecimal costValue = new BigDecimal(0);
        Cost cost = new Cost(item, description, quantity, costValue, applicationFinance, question);
        cost = costRepository.save(cost);
        return cost;
    }

    private Cost initializeLabour(ApplicationFinance applicationFinance) {
        String description = "Working days per year";
        Integer quantity = 0;
        Question question = questionService.getQuestionById(28L);
        Cost cost = new Cost(null, description, quantity, null, applicationFinance, question);
        cost = costRepository.save(cost);
        return cost;
    }

    @RequestMapping("/getById/{applicationFinanceId}")
    public ApplicationFinanceResource findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return new ApplicationFinanceResource(applicationFinanceRepository.findOne(applicationFinanceId));
    }

    @RequestMapping("/update/{applicationFinanceId}")
    public ApplicationFinanceResource update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance) {
        log.error(String.format("ApplicationFinanceController.update(%d)", applicationFinanceId));
        ApplicationFinance dbFinance = applicationFinanceRepository.findOne(applicationFinance.getId());
        dbFinance.merge(applicationFinance);
        dbFinance = applicationFinanceRepository.save(dbFinance);
        return new ApplicationFinanceResource(dbFinance);
    }

    @RequestMapping("/financeDetails/{applicationId}/{organisationId}")
    public ApplicationFinanceResource financeDetails(@PathVariable("applicationId") final Long applicationId, @PathVariable("organisationId") final Long organisationId) {
        return applicationFinanceHandler.getApplicationOrganisationFinances(new ApplicationFinanceResourceId(applicationId, organisationId));
    }

    @RequestMapping("/financeTotals/{applicationId}")
    public List<ApplicationFinanceResource> financeTotals(@PathVariable("applicationId") final Long applicationId) {
        return applicationFinanceHandler.getApplicationTotals(applicationId);
    }
}
