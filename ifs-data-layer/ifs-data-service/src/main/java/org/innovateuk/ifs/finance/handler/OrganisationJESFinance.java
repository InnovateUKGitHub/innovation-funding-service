package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.handler.item.JESCostHandler;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.GrantClaimCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Component
public class OrganisationJESFinance implements OrganisationFinanceHandler {

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Override
    public Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType) {
        return null;
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinances(Long applicationFinanceId) {
        List<ApplicationFinanceRow> costs = applicationFinanceRowRepository.findByTargetId(applicationFinanceId);
        return addCostsAndTotalsToCategories(costs);
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinances(Long projectFinanceId) {
        List<ProjectFinanceRow> costs = projectFinanceRowRepository.findByTargetId(projectFinanceId);
        return addCostsAndTotalsToCategories(costs);
    }

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> getProjectOrganisationFinanceChanges(Long projectFinanceId) {
        return noChangesAsAcademicFinancesAreNotEditable();
    }

    private Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> noChangesAsAcademicFinancesAreNotEditable() {
        return emptyMap();
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinanceTotals(Long applicationFinanceId, Competition competition) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = getOrganisationFinances(applicationFinanceId);
        return updateCostCategoryValuesForTotals(competition, costCategories);
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinanceTotals(Long projectFinanceId, Competition competition) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = getProjectOrganisationFinances(projectFinanceId);
        return updateCostCategoryValuesForTotals(competition, costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsAndTotalsToCategories(List<? extends FinanceRow> costs) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = createCostCategories();
        costCategories = addCostsToCategories(costCategories, costs);
        costCategories = calculateTotals(costCategories);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> updateCostCategoryValuesForTotals(Competition competition, Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories = setGrantClaimPercentage(costCategories, competition);
        costCategories = calculateTotals(costCategories);
        return resetCosts(costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> setGrantClaimPercentage(Map<FinanceRowType, FinanceRowCostCategory> costCategories, Competition competition) {
        FinanceRowItem costItem = new GrantClaim(0L, competition.getAcademicGrantPercentage());
        costCategories.get(FinanceRowType.FINANCE).addCost(costItem);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateTotals(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.calculateTotal());
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> resetCosts(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values()
                .forEach(cc -> cc.setCosts(new ArrayList<>()));
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
                default:
                    financeRowCostCategory = new DefaultCostCategory();

            }
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsToCategories(Map<FinanceRowType, FinanceRowCostCategory> costCategories, List<? extends FinanceRow> costs) {
        costs.stream().forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    private void addCostToCategory(Map<FinanceRowType, FinanceRowCostCategory> costCategories, FinanceRow cost) {
        FinanceRowType costType = FinanceRowType.fromType(cost.getQuestion().getFormInputs().get(0).getType());
        FinanceRowItem costItem = toCostItem(cost);
        FinanceRowCostCategory financeRowCostCategory = costCategories.get(costType);
        financeRowCostCategory.addCost(costItem);
    }

    private FinanceRowItem toCostItem(FinanceRow cost) {
        return new AcademicCost(cost.getId(), cost.getName(), cost.getCost(), cost.getItem());
    }

    @Override
    public ApplicationFinanceRow costItemToCost(FinanceRowItem costItem) {
        return buildFinanceRowHandler().toCost(costItem);
    }

    @Override
    public ProjectFinanceRow costItemToProjectCost(FinanceRowItem costItem) {
        return buildFinanceRowHandler().toProjectCost(costItem);
    }

    private FinanceRowHandler buildFinanceRowHandler(){
        FinanceRowHandler financeRowHandler = new JESCostHandler();
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
        return new JESCostHandler();
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
        return find(applicationFinanceRowRepository.findOne(newCostItem.getId()),  notFoundError(ApplicationFinanceRow.class, newCostItem.getId())).andOnSuccess(financeRow ->
                find(applicationFinanceRowRepository.findByTargetIdAndNameAndQuestionId(financeRow.getTarget().getId(), financeRow.getName(), financeRow.getQuestion().getId()),
                        notFoundError(ApplicationFinanceRow.class, newCostItem.getId())).andOnSuccess(foundCostItems -> checkAndUpdateCostItem(newCostItem, foundCostItems))).getSuccessObjectOrThrowException();
    }

    @Override
    public ApplicationFinanceRow addCost(Long applicationFinanceId, Long questionId, ApplicationFinanceRow newCostItem) {
        List<ApplicationFinanceRow> financeRows = applicationFinanceRowRepository.findByTargetIdAndNameAndQuestionId(applicationFinanceId, newCostItem.getName(), questionId);
        return checkAndAddCostItem(newCostItem, financeRows).getSuccessObjectOrThrowException();

    }

    private ServiceResult<ApplicationFinanceRow> checkAndUpdateCostItem(final ApplicationFinanceRow newCostItem, List<ApplicationFinanceRow> foundApplicationFinanceRows) {
        if(foundApplicationFinanceRows.isEmpty() || foundApplicationFinanceRows.size() == 1) {
            return serviceSuccess(applicationFinanceRowRepository.save(newCostItem));
        }

        return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
    }

    private ServiceResult<ApplicationFinanceRow> checkAndAddCostItem(final ApplicationFinanceRow newCostItem, List<ApplicationFinanceRow> foundApplicationFinanceRows) {
        if(foundApplicationFinanceRows.isEmpty()) {
            return serviceSuccess(applicationFinanceRowRepository.save(newCostItem));
        }

        return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
    }
}