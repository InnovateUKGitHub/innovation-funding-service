package org.innovateuk.ifs.finance.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.transactional.QuestionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class AbstractOrganisationFinanceHandler implements OrganisationFinanceHandler {
    private static final Log LOG = LogFactory.getLog(OrganisationFinanceDefaultHandler.class);

    protected ApplicationFinanceRowRepository applicationFinanceRowRepository;

    protected ProjectFinanceRowRepository projectFinanceRowRepository;

    protected FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    protected QuestionService questionService;

    protected ApplicationFinanceRepository applicationFinanceRepository;

    public AbstractOrganisationFinanceHandler(ApplicationFinanceRowRepository applicationFinanceRowRepository,
                                              ProjectFinanceRowRepository projectFinanceRowRepository,
                                              FinanceRowMetaFieldRepository financeRowMetaFieldRepository,
                                              QuestionService questionService,
                                              ApplicationFinanceRepository applicationFinanceRepository) {
        this.applicationFinanceRowRepository = applicationFinanceRowRepository;
        this.projectFinanceRowRepository = projectFinanceRowRepository;
        this.financeRowMetaFieldRepository = financeRowMetaFieldRepository;
        this.questionService = questionService;
        this.applicationFinanceRepository = applicationFinanceRepository;
    }

    @Override
    public Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType) {
        if (initialiseCostTypeSupported(costType)) {
            long competitionId = applicationFinance.getApplication().getCompetition().getId();
            Question question = getQuestionByCostType(competitionId, costType);
            try {
                List<ApplicationFinanceRow> cost = getCostHandler(costType).initializeCost(applicationFinance);
                cost.forEach(c -> {
                    c.setQuestion(question);
                    c.setTarget(applicationFinance);
                });
                return applicationFinanceRowRepository.saveAll(cost);
            } catch (IllegalArgumentException e) {
                LOG.error(String.format("No FinanceRowHandler for type: %s", costType.getType()), e);
            }
        }
        return null;
    }

    protected abstract boolean initialiseCostTypeSupported(FinanceRowType costType);

    protected abstract Map<FinanceRowType, FinanceRowCostCategory> createCostCategories();

    protected abstract Map<FinanceRowType, FinanceRowCostCategory> afterTotalCalculation(Map<FinanceRowType, FinanceRowCostCategory> costCategories);

    private Question getQuestionByCostType(long competitionId, FinanceRowType costType) {
        return questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, costType.getFormInputType()).getSuccess();
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinances(long applicationFinanceId) {
        List<ApplicationFinanceRow> costs = applicationFinanceRowRepository.findByTargetId(applicationFinanceId);
        return updateCostCategoryValuesForTotals(addCostsAndTotalsToCategories(costs));
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinances(long projectFinanceId) {
        List<ProjectFinanceRow> costs = projectFinanceRowRepository.findByTargetId(projectFinanceId);
        return updateCostCategoryValuesForTotals(addCostsAndTotalsToCategories(costs));
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsAndTotalsToCategories(List<? extends FinanceRow> costs) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = createCostCategories();
        costCategories = addCostsToCategories(costCategories, costs);
        costCategories = calculateTotals(costCategories);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> updateCostCategoryValuesForTotals(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories = calculateTotals(costCategories);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateTotals(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(FinanceRowCostCategory::calculateTotal);
        return afterTotalCalculation(costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsToCategories(Map<FinanceRowType, FinanceRowCostCategory> costCategories, List<? extends FinanceRow> costs) {
        costs.stream().forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    private void addCostToCategory(Map<FinanceRowType, FinanceRowCostCategory> costCategories, FinanceRow cost) {
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
    public FinanceRowItem costToCostItem(FinanceRow cost) {
        FinanceRowHandler financeRowHandler = getRowHandler(cost);
        return financeRowHandler.toCostItem(cost);
    }

    private FinanceRowHandler getRowHandler(FinanceRow cost) {
        cost.getQuestion().getFormInputs().size();
        FinanceRowType costType = FinanceRowType.fromType(cost.getQuestion().getFormInputs().get(0).getType());
        return getCostHandler(costType);
    }

    @Override
    public List<FinanceRowItem> costsToCostItems(List<? extends FinanceRow> costs) {
        return costs.stream().map(c -> costToCostItem(c)).collect(Collectors.toList());
    }

    @Override
    public ApplicationFinanceRow updateCost(final ApplicationFinanceRow newCostItem) {
        return find(applicationFinanceRowRepository.findById(newCostItem.getId()), notFoundError(ApplicationFinanceRow.class, newCostItem.getId()))
                .andOnSuccess(costItem -> serviceSuccess(applicationFinanceRowRepository.save(newCostItem))).getSuccess();
    }

    @Override
    public ApplicationFinanceRow addCost(Long applicationFinanceId, Long questionId, ApplicationFinanceRow newCostItem) {
        return applicationFinanceRowRepository.save(newCostItem);
    }
}