package org.innovateuk.ifs.finance.handler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.item.*;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.FormInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_COSTS;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * OrganisationFinanceDefaultHandler maintains the finances from
 * an organisation's perspective and calculates the totals
 */
@Component
public class OrganisationFinanceDefaultHandler extends AbstractOrganisationFinanceHandler implements OrganisationFinanceHandler {
    private static final Log LOG = LogFactory.getLog(OrganisationFinanceDefaultHandler.class);

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(Long projectFinanceId) {
        ProjectFinance projectFinance = projectFinanceRepository.findOne(projectFinanceId);
        Long applicationId = projectFinance.getProject().getApplication().getId();
        Long organisationId = projectFinance.getOrganisation().getId();
        List<ProjectFinanceRow> projectCosts = getProjectCosts(projectFinanceId);
        List<ApplicationFinanceRow> applicationCosts = getApplicationCosts(applicationId, organisationId);
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> changesList
                = toChangesList(applicationId, organisationId, applicationCosts, projectCosts);
        return getProjectCostChangesByType(changesList);
    }

    @Override
    protected Map<FinanceRowType, FinanceRowCostCategory> createCostCategories() {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);
        for (FinanceRowType costType : FinanceRowType.values()) {
            FinanceRowCostCategory financeRowCostCategory = createCostCategoryByType(costType);
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    @Override
    protected Map<FinanceRowType, FinanceRowCostCategory> afterTotalCalculation(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        FinanceRowCostCategory labourFinanceRowCostCategory = costCategories.get(FinanceRowType.LABOUR);
        OverheadCostCategory overheadCategory = (OverheadCostCategory) costCategories.get(FinanceRowType.OVERHEADS);
        overheadCategory.setLabourCostTotal(labourFinanceRowCostCategory.getTotal());
        overheadCategory.calculateTotal();
        return costCategories;
    }

    @Override
    protected boolean initialiseCostTypeSupported(FinanceRowType costType) {
        return !(FinanceRowType.YOUR_FINANCE.equals(costType) || FinanceRowType.ACADEMIC.equals(costType));
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowType costType) {
        FinanceRowHandler handler = null;
        switch (costType) {
            case LABOUR:
                handler = new LabourCostHandler();
                break;
            case CAPITAL_USAGE:
                handler = new CapitalUsageHandler();
                break;
            case MATERIALS:
                handler = new MaterialsHandler();
                break;
            case OTHER_COSTS:
                handler = new OtherCostHandler();
                break;
            case OVERHEADS:
                handler = new OverheadsHandler();
                break;
            case SUBCONTRACTING_COSTS:
                handler = new SubContractingCostHandler();
                break;
            case TRAVEL:
                handler = new TravelCostHandler();
                break;
            case FINANCE:
                handler = new GrantClaimHandler();
                break;
            case OTHER_FUNDING:
                handler = new OtherFundingHandler();
                break;
        }
        if (handler != null) {
            beanFactory.autowireBean(handler);
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
                return new GrantClaimCategory();
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
            List<FormInput> formInputs = availableRow.getQuestion().getFormInputs();
            if (!formInputs.isEmpty()) {
                costType = FinanceRowType.fromType(formInputs.get(0).getType());
            }
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
                applicationFinanceRow = Optional.ofNullable(applicationFinanceRowRepository.findOne(cost.getApplicationRowId()));
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
            Optional<ApplicationFinanceRow> applicationFinanceRow = Optional.ofNullable(applicationFinanceRowRepository.findOne(cost.getId()));
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
                    cost.getItem(), cost.getDescription(), cost.getQuantity(), cost.getCost(), applicationFinance, cost.getQuestion());
            applicationFinanceRow.setFinanceRowMetadata(cost.getFinanceRowMetadata());
            return applicationFinanceRow;
        });
    }

    private FinanceRowItem getApplicationCostItem(FinanceRowType financeRowType, ApplicationFinanceRow applicationCost) {
        return getCostHandler(financeRowType).toCostItem(applicationCost);
    }

    private List<ProjectFinanceRow> getProjectCosts(Long projectFinanceId) {
        return projectFinanceRowRepository.findByTargetId(projectFinanceId);
    }

    private List<ApplicationFinanceRow> getApplicationCosts(Long applicationId, Long organisationId) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        return applicationFinanceRowRepository.findByTargetId(applicationFinance.getId());
    }

    public ApplicationFinanceRow updateCost(ApplicationFinanceRow newCostItem) {
        return applicationFinanceRowRepository.save(newCostItem);
    }

    public ApplicationFinanceRow addCost(Long applicationFinanceId, Long questionId, ApplicationFinanceRow newCostItem) {
        return applicationFinanceRowRepository.save(newCostItem);
    }
}
