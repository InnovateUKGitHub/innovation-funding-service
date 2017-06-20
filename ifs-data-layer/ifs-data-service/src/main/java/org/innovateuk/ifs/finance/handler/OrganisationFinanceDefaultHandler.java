package org.innovateuk.ifs.finance.handler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.item.*;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import static java.util.stream.Collectors.toList;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_COSTS;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * OrganisationFinanceDefaultHandler maintains the finances from
 * an organisation's perspective and calculates the totals
 */
@Component
public class OrganisationFinanceDefaultHandler implements OrganisationFinanceHandler {
    private static final Log LOG = LogFactory.getLog(OrganisationFinanceDefaultHandler.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Override
    public Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType){
        if(costTypeSupportedByHandler(costType)) {
            Long competitionId = applicationFinance.getApplication().getCompetition().getId();
            Question question = getQuestionByCostType(competitionId, costType);
            try{
                List<ApplicationFinanceRow> cost = getCostHandler(costType).initializeCost();
                cost.forEach(c -> {
                    c.setQuestion(question);
                    c.setTarget(applicationFinance);
                });
                return cost.isEmpty() ? new ArrayList<>() : applicationFinanceRowRepository.save(cost);
            }catch (IllegalArgumentException e){
                LOG.error(String.format("No FinanceRowHandler for type: %s", costType.getType()), e);
            }
        }
        return null;
    }

    // TODO DW - INFUND-1555 - handle rest result
    private Question getQuestionByCostType(Long competitionId, FinanceRowType costType) {
        return questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, costType.getFormInputType()).getSuccessObjectOrThrowException();
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinances(Long applicationFinanceId) {
        List<ApplicationFinanceRow> costs = applicationFinanceRowRepository.findByTargetId(applicationFinanceId);
        return addCostsAndTotalsToCategories(costs);
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinances(Long projectFinanceId) {
        List<ProjectFinanceRow> costs = projectFinanceRowRepository.findByTargetId(projectFinanceId);
        List<ApplicationFinanceRow> asApplicationCosts = toApplicationFinanceRows(costs);
        return addCostsAndTotalsToCategories(asApplicationCosts);
    }

    @Override
    public Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> getProjectOrganisationFinanceChanges(Long projectFinanceId) {
        ProjectFinance projectFinance = projectFinanceRepository.findOne(projectFinanceId);
        Long applicationId = projectFinance.getProject().getApplication().getId();
        Long organisationId = projectFinance.getOrganisation().getId();
        List<ProjectFinanceRow> projectCosts = getProjectCosts(projectFinanceId);
        List<ApplicationFinanceRow> applicationCosts = getApplicationCosts(applicationId, organisationId);
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> changesList
                = toChangesList(applicationId, organisationId, applicationCosts, projectCosts);
        return getProjectCostChangesByType(changesList);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsAndTotalsToCategories(List<ApplicationFinanceRow> asApplicationCosts) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = createCostCategories();
        costCategories = addCostsToCategories(costCategories, asApplicationCosts);
        costCategories = calculateTotals(costCategories);
        return costCategories;
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
    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinanceTotals(Long applicationFinanceId, Competition competition) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = getOrganisationFinances(applicationFinanceId);
        return resetCosts(costCategories);
    }

    @Override
    public Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinanceTotals(Long projectFinanceId, Competition competition) {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = getProjectOrganisationFinances(projectFinanceId);
        return resetCosts(costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> createCostCategories() {
        Map<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);
        for(FinanceRowType costType : FinanceRowType.values()) {
            FinanceRowCostCategory financeRowCostCategory = createCostCategoryByType(costType);
            costCategories.put(costType, financeRowCostCategory);
        }
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsToCategories(Map<FinanceRowType,
                                                                             FinanceRowCostCategory> costCategories,
                                                                             List<ApplicationFinanceRow> costs) {
        costs.forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    /**
     * The costs are converted to a representation based on its type that can be used in the view and
     * are added to a specific Category (e.g. labour).
     * @param cost ApplicationFinanceRow to be added
     */
    private Map<FinanceRowType, FinanceRowCostCategory> addCostToCategory(Map<FinanceRowType, FinanceRowCostCategory> costCategories,
                                                                          ApplicationFinanceRow cost) {
        FinanceRowType costType = FinanceRowType.fromType(cost.getQuestion().getFormInputs().get(0).getType());
        FinanceRowHandler financeRowHandler = getCostHandler(costType);
        FinanceRowItem costItem = financeRowHandler.toCostItem(cost);
        FinanceRowCostCategory financeRowCostCategory = costCategories.get(costType);
        financeRowCostCategory.addCost(costItem);
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateTotals(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values().forEach(FinanceRowCostCategory::calculateTotal);
        return calculateOverheadTotal(costCategories);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> calculateOverheadTotal(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        FinanceRowCostCategory labourFinanceRowCostCategory = costCategories.get(FinanceRowType.LABOUR);
        OverheadCostCategory overheadCategory = (OverheadCostCategory) costCategories.get(FinanceRowType.OVERHEADS);
        overheadCategory.setLabourCostTotal(labourFinanceRowCostCategory.getTotal());
        overheadCategory.calculateTotal();
        return costCategories;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> resetCosts(Map<FinanceRowType, FinanceRowCostCategory> costCategories) {
        costCategories.values().forEach(cc -> cc.setCosts(new ArrayList<>()));
        return costCategories;
    }

    @Override
    public ApplicationFinanceRow costItemToCost(FinanceRowItem costItem) {
        return buildFinanceRowHandler(costItem).toCost(costItem);
    }

    @Override
    public ProjectFinanceRow costItemToProjectCost(FinanceRowItem costItem) {
        return buildFinanceRowHandler(costItem).toProjectCost(costItem);
    }

    private FinanceRowHandler buildFinanceRowHandler(FinanceRowItem costItem){
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

    private FinanceRowHandler getRowHandler(FinanceRow cost){
        cost.getQuestion().getFormInputs().size();
        FinanceRowType costType = FinanceRowType.fromType(cost.getQuestion().getFormInputs().get(0).getType());
        return getCostHandler(costType);
    }

    @Override
    public List<FinanceRowItem> costToCostItem(List<ApplicationFinanceRow> costs) {
        return costs.stream().map(cost -> costToCostItem(cost)).collect(toList());
    }

    @Override
    public List<ApplicationFinanceRow> costItemsToCost(List<FinanceRowItem> costItems) {
        return simpleMap(costItems, this::costItemToCost);
    }

    private boolean costTypeSupportedByHandler(FinanceRowType costType) {
        return !(FinanceRowType.YOUR_FINANCE.equals(costType) || FinanceRowType.ACADEMIC.equals(costType));
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowType costType) {
        FinanceRowHandler handler = null;
        switch(costType) {
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
        if(handler != null){
            // some times the handler needs autowired classes
            beanFactory.autowireBean(handler);
            return handler;
        }
        LOG.error("Not a valid FinanceType: " + costType);
        throw new IllegalArgumentException("Not a valid FinanceType: " + costType);
    }

    private FinanceRowCostCategory createCostCategoryByType(FinanceRowType costType) {
        switch(costType) {
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

    private Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>>
                                        getProjectCostChangesByType(List<ImmutablePair<Optional<ApplicationFinanceRow>,
                                                                    Optional<ApplicationFinanceRow>>> costs) {

        Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changedPairs = new LinkedHashMap<>();

        for(ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>> pair : costs) {
            Optional<ApplicationFinanceRow> applicationCost = pair.getLeft();
            Optional<ApplicationFinanceRow> projectCost = pair.getRight();
            FinanceRowType costType = getCostType(applicationCost, projectCost);

            ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> updatedPair;
            if(isNew(applicationCost)){
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.NEW);
            } else if(isRemoved(applicationCost, projectCost)){
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.REMOVE);
            } else if(isUpdate(applicationCost, projectCost)) {
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.CHANGE);
            } else {
                continue;
            }
            changedPairs.put(costType, addNewOrUpdate(changedPairs, costType, updatedPair));
        }

        return changedPairs;
    }

    private FinanceRowType getCostType(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost){
        FinanceRow availableRow = applicationCost.isPresent() ? applicationCost.get() : (projectCost.isPresent() ? projectCost.get() : null);
        FinanceRowType costType = OTHER_COSTS;
        if(availableRow != null) {
            List<FormInput> formInputs = availableRow.getQuestion().getFormInputs();
            if (formInputs.size() > 0) {
                costType = FinanceRowType.fromType(formInputs.get(0).getType());
            }
        }
        return costType;
    }

    private boolean isNew(Optional<ApplicationFinanceRow> applicationCost){
        return !applicationCost.isPresent();
    }

    private List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>> addNewOrUpdate(Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changedPairs, FinanceRowType costType, ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> pair){
        List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>> listOfChangedRows = changedPairs.get(costType);
        if(listOfChangedRows == null){
            listOfChangedRows = new ArrayList<>();
        }
        listOfChangedRows.add(pair);
        return listOfChangedRows;
    }

    private boolean isUpdate(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost){
        return (applicationCost.isPresent() && projectCost.isPresent() && !applicationCost.get().matches(projectCost.get()));
    }

    private boolean isRemoved(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost){
        return (applicationCost.isPresent() && !projectCost.isPresent());
    }

    private ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> buildPairWithTypeOfChange(Optional<ApplicationFinanceRow> applicationCost, Optional<ApplicationFinanceRow> projectCost, FinanceRowType costType, TypeOfChange typeOfChange){
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

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> getUpdateList(Long applicationId, Long organisationId, List<ProjectFinanceRow> projectCosts){
        return simpleMap(projectCosts, cost -> {
            ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            Optional<ApplicationFinanceRow> applicationFinanceRow;
            if(cost.getApplicationRowId() != null) {
                applicationFinanceRow = Optional.ofNullable(applicationFinanceRowRepository.findOne(cost.getApplicationRowId()));
            } else{
                applicationFinanceRow = Optional.empty();
            }
            return ImmutablePair.of(toFinanceRow(applicationFinanceRow, applicationFinance), toFinanceRow(Optional.of(cost), applicationFinance));
        });
    }

    private List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> getRemovedList(Long applicationId, Long organisationId, List<ApplicationFinanceRow> applicationCosts){
        List<ImmutablePair<Optional<ApplicationFinanceRow>, Optional<ApplicationFinanceRow>>> removals = new ArrayList<>();

        for(ApplicationFinanceRow cost : applicationCosts) {
            ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            Optional<ApplicationFinanceRow> applicationFinanceRow = Optional.ofNullable(applicationFinanceRowRepository.findOne(cost.getId()));
            Optional<ProjectFinanceRow> projectFinanceRow = projectFinanceRowRepository.findOneByApplicationRowId(cost.getId());
            if(!projectFinanceRow.isPresent()) {
                removals.add(ImmutablePair.of(toFinanceRow(applicationFinanceRow, applicationFinance), Optional.empty()) );
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

    private FinanceRowItem getApplicationCostItem(FinanceRowType financeRowType, ApplicationFinanceRow applicationCost){
        return getCostHandler(financeRowType).toCostItem(applicationCost);
    }

    private List<ProjectFinanceRow> getProjectCosts(Long projectFinanceId){
        return projectFinanceRowRepository.findByTargetId(projectFinanceId);
    }

    private List<ApplicationFinanceRow> getApplicationCosts(Long applicationId, Long organisationId){
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        return applicationFinanceRowRepository.findByTargetId(applicationFinance.getId());
    }
}
