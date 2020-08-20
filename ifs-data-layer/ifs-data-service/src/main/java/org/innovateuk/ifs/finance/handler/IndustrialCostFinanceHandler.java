package org.innovateuk.ifs.finance.handler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_COSTS;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.VAT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * OrganisationFinanceDefaultHandler maintains the finances from
 * an organisation's perspective and calculates the totals
 */
@Component
public class IndustrialCostFinanceHandler extends AbstractOrganisationFinanceHandler implements OrganisationTypeFinanceHandler {
    private static final Log LOG = LogFactory.getLog(IndustrialCostFinanceHandler.class);

    private Map<FinanceRowType, FinanceRowHandler<?>> financeRowHandlers;

    @Autowired
    public void setFinanceRowHandlers(Collection<FinanceRowHandler<?>> autowiredFinanceRowHandlers) {
        this.financeRowHandlers = autowiredFinanceRowHandlers.stream()
                .filter(h -> h.getFinanceRowType().isPresent())
                .collect(Collectors.toMap(h -> h.getFinanceRowType().get(), Function.identity()));
    }

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(long projectFinanceId) {
        ProjectFinance projectFinance = projectFinanceRepository.findById(projectFinanceId).get();
        long applicationId = projectFinance.getProject().getApplication().getId();
        long organisationId = projectFinance.getOrganisation().getId();
        List<ProjectFinanceRow> projectCosts = getProjectCosts(projectFinanceId);
        List<ApplicationFinanceRow> applicationCosts = getApplicationCosts(applicationId, organisationId);
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>>> changesList
                = toChangesList(applicationCosts, projectCosts);
        return getProjectCostChangesByType(changesList);
    }

    @Override
    protected Map<FinanceRowType, FinanceRowCostCategory> createCostCategories(Competition competition) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);
        for (FinanceRowType costType : competition.getFinanceRowTypes()) {
            FinanceRowCostCategory financeRowCostCategory = createCostCategoryByType(costType);
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    @Override
    protected Map<FinanceRowType, FinanceRowCostCategory> afterTotalCalculation(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        FinanceRowCostCategory labourFinanceRowCostCategory = costCategories.get(FinanceRowType.LABOUR);
        OverheadCostCategory overheadCategory = (OverheadCostCategory) costCategories.get(FinanceRowType.OVERHEADS);
        VatCostCategory vatCategory = (VatCostCategory) costCategories.get(FinanceRowType.VAT);
        if (overheadCategory != null && labourFinanceRowCostCategory != null) {
            overheadCategory.setLabourCostTotal(labourFinanceRowCostCategory.getTotal());
            overheadCategory.calculateTotal();
        }
        if (vatCategory != null) {
            vatCategory.setTotalCostsWithoutVat(costCategories.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(VAT) && !entry.getValue().excludeFromTotalCost())
                    .map(Entry::getValue)
                    .map(FinanceRowCostCategory::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            vatCategory.calculateTotal();
        }
        return costCategories;
    }

    @Override
    protected boolean initialiseCostTypeSupported(FinanceRowType costType) {
        return !(FinanceRowType.YOUR_FINANCE.equals(costType));
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowType costType) {
        FinanceRowHandler handler = financeRowHandlers.get(costType);
        if (handler != null) {
            return handler;
        }
        LOG.error("Not a valid FinanceType: " + costType);
        throw new IllegalArgumentException("Not a valid FinanceType: " + costType);
    }

    private FinanceRowCostCategory createCostCategoryByType(FinanceRowType costType) {
        switch (costType) {
            case LABOUR:
                return new LabourCostCategory();
            case OTHER_FUNDING:
                return new OtherFundingCostCategory();
            case OVERHEADS:
                return new OverheadCostCategory();
            case FINANCE:
            case GRANT_CLAIM_AMOUNT:
                return new ExcludedCostCategory();
            case VAT:
                return new VatCostCategory();
            case ADDITIONAL_COMPANY_COSTS:
                return new AdditionalCompanyCostCategory();
            default:
                return new DefaultCostCategory();
        }
    }

    private Map<FinanceRowType, List<ChangedFinanceRowPair>>
    getProjectCostChangesByType(List<ImmutablePair<Optional<ApplicationFinanceRow>,
            Optional<ProjectFinanceRow>>> costs) {

        Map<FinanceRowType, List<ChangedFinanceRowPair>> changedPairs = new LinkedHashMap<>();

        for (ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>> pair : costs) {
            Optional<ApplicationFinanceRow> applicationCost = pair.getLeft();
            Optional<ProjectFinanceRow> projectCost = pair.getRight();
            FinanceRowType costType = getCostType(applicationCost, projectCost);

            ChangedFinanceRowPair updatedPair;
            if (isNew(applicationCost)) {
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.NEW);
            } else if (isRemoved(applicationCost, projectCost)) {
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.REMOVE);
            } else if (isUpdate(applicationCost, projectCost)) {
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.CHANGE);
            } else {
                continue;
            }
            changedPairs.put(costType, addNewOrUpdate(changedPairs, costType, updatedPair));
        }

        return changedPairs;
    }

    private FinanceRowType getCostType(Optional<ApplicationFinanceRow> applicationCost, Optional<ProjectFinanceRow> projectCost) {
        FinanceRow availableRow;
        if (applicationCost.isPresent()) {
            availableRow = applicationCost.get();
        } else if (projectCost.isPresent()) {
            availableRow = projectCost.get();
        } else {
            availableRow = null;
        }

        FinanceRowType costType = OTHER_COSTS;
        if (availableRow != null) {
            costType = availableRow.getType();
        }
        return costType;
    }

    private boolean isNew(Optional<ApplicationFinanceRow> applicationCost) {
        return !applicationCost.isPresent();
    }

    private List<ChangedFinanceRowPair> addNewOrUpdate(Map<FinanceRowType, List<ChangedFinanceRowPair>> changedPairs, FinanceRowType costType, ChangedFinanceRowPair pair) {
        List<ChangedFinanceRowPair> listOfChangedRows = changedPairs.get(costType);
        if (listOfChangedRows == null) {
            listOfChangedRows = new ArrayList<>();
        }
        listOfChangedRows.add(pair);
        return listOfChangedRows;
    }

    private boolean isUpdate(Optional<ApplicationFinanceRow> applicationCost, Optional<ProjectFinanceRow> projectCost) {
        return applicationCost.isPresent() && projectCost.isPresent() && !applicationCost.get().matches(projectCost.get());
    }

    private boolean isRemoved(Optional<ApplicationFinanceRow> applicationCost, Optional<ProjectFinanceRow> projectCost) {
        return (applicationCost.isPresent() && !projectCost.isPresent());
    }

    private ChangedFinanceRowPair buildPairWithTypeOfChange(Optional<ApplicationFinanceRow> applicationCost, Optional<ProjectFinanceRow> projectCost, FinanceRowType costType, TypeOfChange typeOfChange) {
        FinanceRowItem applicationFinanceRowItem = !applicationCost.isPresent() ? null : getApplicationCostItem(costType, applicationCost.get());
        FinanceRowItem projectFinanceRowItem = !projectCost.isPresent() ? null : getApplicationCostItem(costType, projectCost.get());
        return ChangedFinanceRowPair.of(typeOfChange, applicationFinanceRowItem, projectFinanceRowItem);
    }

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>>> toChangesList(List<ApplicationFinanceRow> applicationCosts, List<ProjectFinanceRow> projectCosts) {
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>>> removals = getRemovedList(applicationCosts);
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>>> updates = getUpdateList(projectCosts);
        updates.addAll(removals);
        return updates;
    }

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>>> getUpdateList(List<ProjectFinanceRow> projectCosts) {
        return simpleMap(projectCosts, cost -> {
            Optional<ApplicationFinanceRow> applicationFinanceRow;
            if (cost.getApplicationRowId() != null) {
                applicationFinanceRow = applicationFinanceRowRepository.findById(cost.getApplicationRowId());
            } else {
                applicationFinanceRow = Optional.empty();
            }
            return ImmutablePair.of(applicationFinanceRow, Optional.of(cost));
        });
    }

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>>> getRemovedList(List<ApplicationFinanceRow> applicationCosts) {
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ProjectFinanceRow>>> removals = new ArrayList<>();

        for (ApplicationFinanceRow cost : applicationCosts) {
            Optional<ApplicationFinanceRow> applicationFinanceRow = applicationFinanceRowRepository.findById(cost.getId());
            Optional<ProjectFinanceRow> projectFinanceRow = projectFinanceRowRepository.findOneByApplicationRowId(cost.getId());
            if (!projectFinanceRow.isPresent()) {
                removals.add(ImmutablePair.of(applicationFinanceRow, Optional.empty()));
            }
        }

        return removals;
    }

    private FinanceRowItem getApplicationCostItem(FinanceRowType financeRowType, FinanceRow financeRow) {
        return getCostHandler(financeRowType).toResource(financeRow);
    }

    private List<ProjectFinanceRow> getProjectCosts(long projectFinanceId) {
        return projectFinanceRowRepository.findByTargetId(projectFinanceId);
    }

    private List<ApplicationFinanceRow> getApplicationCosts(long applicationId, long organisationId) {
        return applicationFinanceRowRepository.findByTargetApplicationIdAndTargetOrganisationId(applicationId, organisationId);
    }
}
