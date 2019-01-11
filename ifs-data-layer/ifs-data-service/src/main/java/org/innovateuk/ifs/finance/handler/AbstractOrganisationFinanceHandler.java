package org.innovateuk.ifs.finance.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.competition.domain.Competition;
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
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
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
            long applicationId = cost.getTarget().getProject().getApplication().getId();
            long organisationId = cost.getTarget().getOrganisation().getId();
            ApplicationFinance applicationFinance =
                    applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            ApplicationFinanceRow applicationFinanceRow = new ApplicationFinanceRow(cost.getId(), cost.getName(), cost.getItem(), cost.getDescription(),
                    cost.getQuantity(), cost.getCost(), applicationFinance, cost.getQuestion());

            applicationFinanceRow.setFinanceRowMetadata(cost.getFinanceRowMetadata());
            return applicationFinanceRow;
        });
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
        return afterTotalCalculation(costCategories);
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
        FinanceRowHandler financeRowHandler = getRowHandler(cost);
        return financeRowHandler.toCostItem(cost);
    }

    @Override
    public FinanceRowItem costToCostItem(ProjectFinanceRow cost) {
        FinanceRowHandler financeRowHandler = getRowHandler(cost);
        return financeRowHandler.toCostItem(cost);
    }

    private FinanceRowHandler getRowHandler(FinanceRow cost) {
        cost.getQuestion().getFormInputs().size();
        FinanceRowType costType = FinanceRowType.fromType(cost.getQuestion().getFormInputs().get(0).getType());
        return getCostHandler(costType);
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
        return find(applicationFinanceRowRepository.findById(newCostItem.getId()), notFoundError(ApplicationFinanceRow.class, newCostItem.getId()))
                .andOnSuccess(costItem -> serviceSuccess(applicationFinanceRowRepository.save(newCostItem))).getSuccess();
    }

    @Override
    public ApplicationFinanceRow addCost(Long applicationFinanceId, Long questionId, ApplicationFinanceRow newCostItem) {
        return applicationFinanceRowRepository.save(newCostItem);
    }
}