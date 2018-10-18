package org.innovateuk.ifs.finance.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.handler.item.GrantClaimHandler;
import org.innovateuk.ifs.finance.handler.item.JESCostHandler;
import org.innovateuk.ifs.finance.handler.item.OtherFundingHandler;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_FUNDING;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Component
public class OrganisationJESFinance implements OrganisationFinanceHandler {
    private static final Log LOG = LogFactory.getLog(OrganisationFinanceDefaultHandler.class);

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Override
    public Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType) {
        if(costTypeSupportedByHandler(costType)) {
            Long competitionId = applicationFinance.getApplication().getCompetition().getId();
            Question question = getQuestionByCostType(competitionId, costType);
            try{
                List<ApplicationFinanceRow> cost = getCostHandler(costType).initializeCost();
                cost.forEach(c -> {
                    c.setQuestion(question);
                    c.setTarget(applicationFinance);
                });
                return applicationFinanceRowRepository.save(cost);
            } catch (IllegalArgumentException e){
                LOG.error(String.format("No FinanceRowHandler for type: %s", costType.getType()), e);
            }
        }
        return null;
    }

    private boolean costTypeSupportedByHandler(FinanceRowType costType) {
        return asList(FINANCE, OTHER_FUNDING).contains(costType);
    }

    private Question getQuestionByCostType(Long competitionId, FinanceRowType costType) {
        return questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, costType.getFormInputType()).getSuccess();
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinances(Long applicationFinanceId, Competition competition) {
        List<ApplicationFinanceRow> costs = applicationFinanceRowRepository.findByTargetId(applicationFinanceId);
        return updateCostCategoryValuesForTotals(competition, addCostsAndTotalsToCategories(costs));
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinances(Long projectFinanceId, Competition competition) {
        List<ProjectFinanceRow> costs = projectFinanceRowRepository.findByTargetId(projectFinanceId);
        List<ApplicationFinanceRow> asApplicationCosts = toApplicationFinanceRows(costs);
        return updateCostCategoryValuesForTotals(competition, addCostsAndTotalsToCategories(asApplicationCosts));
    }

    private List<ApplicationFinanceRow> toApplicationFinanceRows(List<ProjectFinanceRow> costs) {
        return simpleMap(costs, cost -> {
            Long applicationId = cost.getTarget().getProject().getApplication().getId();
            Long organisationId = cost.getTarget().getOrganisation().getId();
            ApplicationFinance applicationFinance =
                    applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            ApplicationFinanceRow applicationFinanceRow = new ApplicationFinanceRow(cost.getId(), cost.getName(), cost.getItem(), cost.getDescription(),
                    cost.getQuantity(), cost.getCost(), applicationFinance, cost.getQuestion());

            applicationFinanceRow.setFinanceRowMetadata(cost.getFinanceRowMetadata());
            return applicationFinanceRow;
        });
    }

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(Long projectFinanceId) {
        return noChangesAsAcademicFinancesAreNotEditable();
    }

    private Map<FinanceRowType, List<ChangedFinanceRowPair>> noChangesAsAcademicFinancesAreNotEditable() {
        return emptyMap();
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsAndTotalsToCategories(List<ApplicationFinanceRow> costs) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = createCostCategories();
        costCategories = addCostsToCategories(costCategories, costs);
        costCategories = calculateTotals(costCategories);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> updateCostCategoryValuesForTotals(Competition competition, Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories = calculateTotals(costCategories);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateTotals(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(FinanceRowCostCategory::calculateTotal);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> createCostCategories() {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);

        for(FinanceRowType costType : FinanceRowType.values()) {
            FinanceRowCostCategory financeRowCostCategory;
            switch (costType) {
                case FINANCE:
                    financeRowCostCategory = new GrantClaimCategory();
                    break;
                case OTHER_FUNDING:
                    financeRowCostCategory = new OtherFundingCostCategory();
                    break;
                default:
                    financeRowCostCategory = new DefaultCostCategory();

            }
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsToCategories(Map<FinanceRowType, FinanceRowCostCategory> costCategories, List<ApplicationFinanceRow> costs) {
        costs.stream().forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    private void addCostToCategory(Map<FinanceRowType, FinanceRowCostCategory> costCategories, ApplicationFinanceRow cost) {
        FinanceRowType costType = FinanceRowType.fromType(cost.getQuestion().getFormInputs().get(0).getType());
        FinanceRowHandler financeRowHandler = getCostHandler(costType);
        FinanceRowItem costItem = financeRowHandler.toCostItem(cost);
        FinanceRowCostCategory financeRowCostCategory = costCategories.get(costType);
        financeRowCostCategory.addCost(costItem);
    }

    @Override
    public ApplicationFinanceRow costItemToCost(FinanceRowItem costItem) {
        return buildFinanceRowHandler(costItem).toCost(costItem);
    }

    @Override
    public ProjectFinanceRow costItemToProjectCost(FinanceRowItem costItem) {
        return buildFinanceRowHandler(costItem).toProjectCost(costItem);
    }

    private FinanceRowHandler buildFinanceRowHandler(FinanceRowItem costItem) {
        FinanceRowHandler financeRowHandler = getCostHandler(costItem.getCostType());
        List<FinanceRowMetaField> financeRowMetaFields = financeRowMetaFieldRepository.findAll();
        financeRowHandler.setCostFields(financeRowMetaFields);
        return financeRowHandler;
    }

    @Override
    public FinanceRowItem costToCostItem(ApplicationFinanceRow cost) {
        FinanceRowHandler financeRowHandler = new JESCostHandler();
        return financeRowHandler.toCostItem(cost);
    }

    @Override
    public FinanceRowItem costToCostItem(ProjectFinanceRow cost) {
        FinanceRowHandler financeRowHandler = new JESCostHandler();
        return financeRowHandler.toCostItem(cost);
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowType costType) {
        FinanceRowHandler handler = null;
        switch(costType) {
            case LABOUR:
            case CAPITAL_USAGE:
            case MATERIALS:
            case OTHER_COSTS:
            case OVERHEADS:
            case SUBCONTRACTING_COSTS:
            case TRAVEL:
            case YOUR_FINANCE:
            case ACADEMIC:
                handler = new JESCostHandler();
                break;
            case FINANCE:
                handler = new GrantClaimHandler();
                break;
            case OTHER_FUNDING:
                handler = new OtherFundingHandler();
                break;
        }
        if(handler != null){
            // some times the handler needs autowired classes
            beanFactory.autowireBean(handler);
            return handler;
        }
        LOG.error("Not a valid FinanceType: " + costType);
        throw new IllegalArgumentException("Not a valid FinanceType: " + costType);
    }

    @Override
    public List<FinanceRowItem> costToCostItem(List<ApplicationFinanceRow> costs) {
        return costs.stream().map(c -> costToCostItem(c)).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationFinanceRow> costItemsToCost(List<FinanceRowItem> costItems) {
        return costItems.stream().map(c -> costItemToCost(c)).collect(Collectors.toList());
    }

    @Override
    public ApplicationFinanceRow updateCost(final ApplicationFinanceRow newCostItem) {
        return find(applicationFinanceRowRepository.findOne(newCostItem.getId()),  notFoundError(ApplicationFinanceRow.class, newCostItem.getId()))
                .andOnSuccess(costItem -> serviceSuccess(applicationFinanceRowRepository.save(newCostItem))).getSuccess();
    }

    @Override
    public ApplicationFinanceRow addCost(Long applicationFinanceId, Long questionId, ApplicationFinanceRow newCostItem) {
        return applicationFinanceRowRepository.save(newCostItem);
    }
}