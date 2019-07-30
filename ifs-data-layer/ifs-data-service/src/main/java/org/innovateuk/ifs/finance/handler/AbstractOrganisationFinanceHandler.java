package org.innovateuk.ifs.finance.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.transactional.QuestionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class AbstractOrganisationFinanceHandler implements OrganisationTypeFinanceHandler {
    private static final Log LOG = LogFactory.getLog(IndustrialCostFinanceHandler.class);

    protected QuestionService questionService;

    protected ApplicationFinanceRepository applicationFinanceRepository;

    protected ProjectFinanceRepository projectFinanceRepository;

    protected ApplicationFinanceRowRepository applicationFinanceRowRepository;

    protected ProjectFinanceRowRepository projectFinanceRowRepository;

    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;


    public AbstractOrganisationFinanceHandler(ApplicationFinanceRowRepository applicationFinanceRowRepository,
                                              ProjectFinanceRowRepository projectFinanceRowRepository,
                                              FinanceRowMetaFieldRepository financeRowMetaFieldRepository,
                                              QuestionService questionService,
                                              ApplicationFinanceRepository applicationFinanceRepository,
                                              ProjectFinanceRepository projectFinanceRepository) {
        this.applicationFinanceRowRepository = applicationFinanceRowRepository;
        this.projectFinanceRowRepository = projectFinanceRowRepository;
        this.financeRowMetaFieldRepository = financeRowMetaFieldRepository;
        this.questionService = questionService;
        this.applicationFinanceRepository = applicationFinanceRepository;
        this.projectFinanceRepository = projectFinanceRepository;
    }

    @Override
    public Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType) {
        if (initialiseCostTypeSupported(costType)) {
            try {
                List<ApplicationFinanceRow> cost = getCostHandler(costType).initializeCost(applicationFinance);
                cost.forEach(c -> {
                    c.setType(costType);
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

    protected abstract Map<FinanceRowType, FinanceRowCostCategory> createCostCategories(Competition competition);

    protected abstract Map<FinanceRowType, FinanceRowCostCategory> afterTotalCalculation(Map<FinanceRowType, FinanceRowCostCategory> costCategories);

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinances(long applicationFinanceId) {
        return find(applicationFinanceRepository.findById(applicationFinanceId), notFoundError(ApplicationFinance.class, applicationFinanceId)).andOnSuccessReturn(finance -> {
            List<ApplicationFinanceRow> costs = applicationFinanceRowRepository.findByTargetId(applicationFinanceId);
            return updateCostCategoryValuesForTotals(addCostsAndTotalsToCategories(costs, finance.getApplication().getCompetition()));
        }).getSuccess();
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinances(long projectFinanceId) {
        return find(projectFinanceRepository.findById(projectFinanceId), notFoundError(ProjectFinance.class, projectFinanceId)).andOnSuccessReturn(finance -> {
            List<ProjectFinanceRow> costs = projectFinanceRowRepository.findByTargetId(projectFinanceId);
            return updateCostCategoryValuesForTotals(addCostsAndTotalsToCategories(costs, finance.getProject().getApplication().getCompetition()));
        }).getSuccess();
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsAndTotalsToCategories(List<? extends FinanceRow> costs, Competition competition) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = createCostCategories(competition);
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
        FinanceRowType costType = cost.getType();
        FinanceRowHandler financeRowHandler = getCostHandler(costType);
        FinanceRowItem costItem = financeRowHandler.toResource(cost);
        FinanceRowCostCategory financeRowCostCategory = costCategories.get(costType);
        financeRowCostCategory.addCost(costItem);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(FinanceRowItem costItem) {
        return buildFinanceRowHandler(costItem).toApplicationDomain(costItem);
    }

    @Override
    public ProjectFinanceRow toProjectDomain(FinanceRowItem costItem) {
        return buildFinanceRowHandler(costItem).toProjectDomain(costItem);
    }

    private FinanceRowHandler buildFinanceRowHandler(FinanceRowItem costItem) {
        FinanceRowHandler financeRowHandler = getCostHandler(costItem.getCostType());
        List<FinanceRowMetaField> financeRowMetaFields = financeRowMetaFieldRepository.findAll();
        financeRowHandler.setCostFields(financeRowMetaFields);
        return financeRowHandler;
    }

    @Override
    public FinanceRowItem toResource(FinanceRow cost) {
        FinanceRowHandler financeRowHandler = getRowHandler(cost);
        return financeRowHandler.toResource(cost);
    }

    private FinanceRowHandler getRowHandler(FinanceRow cost) {
        return getCostHandler(cost.getType());
    }

    @Override
    public List<FinanceRowItem> toResources(List<? extends FinanceRow> costs) {
        return costs.stream().map(c -> toResource(c)).collect(Collectors.toList());
    }

    @Override
    public ApplicationFinanceRow updateCost(final ApplicationFinanceRow financeRow) {
        return find(applicationFinanceRowRepository.findById(financeRow.getId()), notFoundError(ApplicationFinanceRow.class, financeRow.getId()))
                .andOnSuccess(existing -> serviceSuccess(applicationFinanceRowRepository.save(financeRow))).getSuccess();
    }

    @Override
    public ApplicationFinanceRow addCost(ApplicationFinanceRow financeRow) {
        return applicationFinanceRowRepository.save(financeRow);
    }
}