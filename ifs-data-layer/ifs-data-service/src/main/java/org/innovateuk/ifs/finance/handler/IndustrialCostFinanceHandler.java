package org.innovateuk.ifs.finance.handler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map.Entry;

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
        this.financeRowHandlers = autowiredFinanceRowHandlers.stream().collect(Collectors.toMap(FinanceRowHandler::getFinanceRowType, Function.identity()));
    }

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(Long projectFinanceId) {
        ProjectFinance projectFinance = projectFinanceRepository.findById(projectFinanceId).get();
        Long applicationId = projectFinance.getProject().getApplication().getId();
        Long organisationId = projectFinance.getOrganisation().getId();
        List<ProjectFinanceRow> projectCosts = getProjectCosts(projectFinanceId);
        List<ApplicationFinanceRow> applicationCosts = getApplicationCosts(applicationId, organisationId);
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> changesList
                = toChangesList(applicationId, organisationId, applicationCosts, projectCosts);
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
        return !(FinanceRowType.YOUR_FINANCE.equals(costType) || FinanceRowType.ACADEMIC.equals(costType));
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
            default:
                return new DefaultCostCategory();
        }
    }

    private Map<FinanceRowType, List<ChangedFinanceRowPair>>
    getProjectCostChangesByType(List<ImmutablePair<Optional<ApplicationFinanceRow>,
            Optional<ApplicationFinanceRow>>> costs) {

        Map<FinanceRowType, List<ChangedFinanceRowPair>> changedPairs = new LinkedHashMap<>();

        for (ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>> pair : costs) {
            Optional<ApplicationFinanceRow> applicationCost = pair.getLeft();
            Optional<ApplicationFinanceRow> projectCost = pair.getRight();
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

    private FinanceRowType getCostType(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost) {
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

    private boolean isUpdate(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost) {
        return (applicationCost.isPresent() && projectCost.isPresent() && !applicationCost.get().matches(projectCost.get()));
    }

    private boolean isRemoved(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost) {
        return (applicationCost.isPresent() && !projectCost.isPresent());
    }

    private ChangedFinanceRowPair buildPairWithTypeOfChange(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost, FinanceRowType costType, TypeOfChange typeOfChange) {
        FinanceRowItem applicationFinanceRowItem = !applicationCost.isPresent() ? null : getApplicationCostItem(costType, applicationCost.get());
        FinanceRowItem projectFinanceRowItem = !projectCost.isPresent() ? null : getApplicationCostItem(costType, projectCost.get());
        return ChangedFinanceRowPair.of(typeOfChange, applicationFinanceRowItem, projectFinanceRowItem);
    }

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> toChangesList(Long applicationId, Long organisationId, List<ApplicationFinanceRow> applicationCosts, List<ProjectFinanceRow> projectCosts) {
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> removals = getRemovedList(applicationId, organisationId, applicationCosts);
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> updates = getUpdateList(applicationId, organisationId, projectCosts);
        updates.addAll(removals);
        return updates;
    }

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> getUpdateList(Long applicationId, Long organisationId, List<ProjectFinanceRow> projectCosts) {
        return simpleMap(projectCosts, cost -> {
            ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            Optional<ApplicationFinanceRow> applicationFinanceRow;
            if (cost.getApplicationRowId() != null) {
                applicationFinanceRow = applicationFinanceRowRepository.findById(cost.getApplicationRowId());
            } else {
                applicationFinanceRow = Optional.empty();
            }
            return ImmutablePair.of(toFinanceRow(applicationFinanceRow, applicationFinance), toFinanceRow(Optional.of(cost), applicationFinance));
        });
    }

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> getRemovedList(Long applicationId, Long organisationId, List<ApplicationFinanceRow> applicationCosts) {
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> removals = new ArrayList<>();

        for (ApplicationFinanceRow cost : applicationCosts) {
            ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            Optional<ApplicationFinanceRow> applicationFinanceRow = applicationFinanceRowRepository.findById(cost.getId());
            Optional<ProjectFinanceRow> projectFinanceRow = projectFinanceRowRepository.findOneByApplicationRowId(cost.getId());
            if (!projectFinanceRow.isPresent()) {
                removals.add(ImmutablePair.of(toFinanceRow(applicationFinanceRow, applicationFinance), Optional.empty()));
            }
        }

        return removals;
    }

    private Optional<ApplicationFinanceRow> toFinanceRow(Optional<? extends FinanceRow> optionalCost,
                                                         ApplicationFinance applicationFinance) {
        return optionalCost.map(cost -> {
            ApplicationFinanceRow applicationFinanceRow = new ApplicationFinanceRow(cost.getId(), cost.getName(),
                    cost.getItem(), cost.getDescription(), cost.getQuantity(), cost.getCost(), applicationFinance, cost.getType());
            applicationFinanceRow.setFinanceRowMetadata(cost.getFinanceRowMetadata());
            return applicationFinanceRow;
        });
    }

    private FinanceRowItem getApplicationCostItem(FinanceRowType financeRowType, ApplicationFinanceRow applicationCost) {
        return getCostHandler(financeRowType).toResource(applicationCost);
    }

    private List<ProjectFinanceRow> getProjectCosts(Long projectFinanceId) {
        return projectFinanceRowRepository.findByTargetId(projectFinanceId);
    }

    private List<ApplicationFinanceRow> getApplicationCosts(Long applicationId, Long organisationId) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        return applicationFinanceRowRepository.findByTargetId(applicationFinance.getId());
    }
}
