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
                if(!cost.isEmpty()){
                    applicationFinanceRowRepository.save(cost);
                    return cost;
                }else{
                    return new ArrayList<>();
                }

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
        List<ProjectFinanceRow> projectCosts = getProjectCosts(projectFinanceId);
        List<ApplicationFinanceRow> applicationCosts = getApplicationCosts(projectFinance.getProject().getApplication().getId(), projectFinance.getOrganisation().getId());
        List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> asApplicationCosts = toChangesList(applicationCosts, projectCosts);
        return getProjectCostChangesByType(asApplicationCosts);
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

    private Map<FinanceRowType, FinanceRowCostCategory> addCostsToCategories(Map<FinanceRowType, FinanceRowCostCategory> costCategories, List<ApplicationFinanceRow> costs) {
        costs.forEach(c -> addCostToCategory(costCategories, c));
        return costCategories;
    }

    /**
     * The costs are converted to a representation based on its type that can be used in the view and
     * are added to a specific Category (e.g. labour).
     * @param cost ApplicationFinanceRow to be added
     */
    private Map<FinanceRowType, FinanceRowCostCategory> addCostToCategory(Map<FinanceRowType, FinanceRowCostCategory> costCategories, ApplicationFinanceRow cost) {
        cost.getQuestion().getFormInputs().size();
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
        costCategories.values()
                .forEach(cc -> cc.setCosts(new ArrayList<>()));
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
        List<FinanceRowItem> costItems = new ArrayList<>();
        costs.forEach(cost -> costItems.add(costToCostItem(cost)));
        return costItems;
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

    private Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> getProjectCostChangesByType(List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> costs) {

        Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changedPairs = new HashMap<>();

        for(ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow> pair : costs) {
            ApplicationFinanceRow applicationCost = pair.getLeft();
            ApplicationFinanceRow projectCost = pair.getRight();
            FinanceRowType costType = getCostType(applicationCost, projectCost);

            ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> updatedPair;
            if(isNew(applicationCost)){
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.ADD);
            } else if(isRemoved(applicationCost, projectCost)){
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.REMOVE);
            } else if(isUpdate(applicationCost, projectCost)) {
                updatedPair = buildPairWithTypeOfChange(applicationCost, projectCost, costType, TypeOfChange.UPDATE);
            } else {
                continue;
            }
            changedPairs.put(costType, addNewOrUpdate(changedPairs, costType, updatedPair));
        }

        return changedPairs;
    }

    private FinanceRowType getCostType(FinanceRow applicationCost, FinanceRow projectCost){
        FinanceRow availableRow = applicationCost != null ? applicationCost : (projectCost != null ? projectCost : null);
        FinanceRowType costType = OTHER_COSTS;
        if(availableRow != null) {
            List<FormInput> formInputs = availableRow.getQuestion().getFormInputs();
            if (formInputs.size() > 0) {
                costType = FinanceRowType.fromType(formInputs.get(0).getType());
            }
        }
        return costType;
    }

    private boolean isNew(ApplicationFinanceRow applicationCost){
        return applicationCost == null;
    }

    private List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>> addNewOrUpdate(Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changedPairs, FinanceRowType costType, ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> pair){
        List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>> listOfChangedRows = changedPairs.get(costType);
        if(listOfChangedRows == null){
            listOfChangedRows = new ArrayList<>();
        }
        listOfChangedRows.add(pair);
        return listOfChangedRows;
    }

    private boolean isUpdate(ApplicationFinanceRow applicationCost, ApplicationFinanceRow projectCost){
        return (applicationCost != null && projectCost != null && !applicationCost.matches(projectCost));
    }

    private boolean isRemoved(ApplicationFinanceRow applicationCost, ApplicationFinanceRow projectCost){
        return (applicationCost != null && projectCost == null);
    }

    private ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> buildPairWithTypeOfChange(ApplicationFinanceRow applicationCost, ApplicationFinanceRow projectCost, FinanceRowType costType, TypeOfChange typeOfChange){
        FinanceRowItem applicationFinanceRowItem = applicationCost == null ? null : getApplicationCostItem(costType, applicationCost);
        FinanceRowItem projectFinanceRowItem = projectCost == null ? null : getApplicationCostItem(costType, projectCost);
        return ChangedFinanceRowPair.of(typeOfChange, applicationFinanceRowItem, projectFinanceRowItem);
    }

    private List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> toChangesList(List<ApplicationFinanceRow> applicationCosts, List<ProjectFinanceRow> projectCosts) {
        List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> removals = getRemovedList(applicationCosts);
        List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> updates = getUpdateList(projectCosts);
        updates.addAll(removals);
        return updates;
    }

    private List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> getUpdateList(List<ProjectFinanceRow> projectCosts){
        return simpleMap(projectCosts, cost -> {
            Long applicationId = cost.getTarget().getProject().getApplication().getId();
            Long organisationId = cost.getTarget().getOrganisation().getId();
            ApplicationFinance applicationFinance =
                    applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            ApplicationFinanceRow applicationFinanceRow = applicationFinanceRowRepository.findById(cost.getApplicationRowId());
            return ImmutablePair.of(buildApplicationFinanceRow(applicationFinanceRow, applicationFinance), buildProjectFinanceRow(cost, applicationFinance));
        });
    }

    private List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> getRemovedList(List<ApplicationFinanceRow> applicationCosts){
        List<ImmutablePair<ApplicationFinanceRow, ApplicationFinanceRow>> removals = new ArrayList<>();

        for(ApplicationFinanceRow cost : applicationCosts) {
            Long applicationId = cost.getTarget().getApplication().getId();
            Long organisationId = cost.getTarget().getOrganisation().getId();
            ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            ApplicationFinanceRow applicationFinanceRow = applicationFinanceRowRepository.findOne(cost.getId());
            Optional<ProjectFinanceRow> projectFinanceRow = projectFinanceRowRepository.findOneByApplicationRowId(cost.getId());
            if(!projectFinanceRow.isPresent()) {
                removals.add(ImmutablePair.of(buildApplicationFinanceRow(applicationFinanceRow, applicationFinance), null));
            }
        }

        return removals;
    }

    private ApplicationFinanceRow buildApplicationFinanceRow(ApplicationFinanceRow cost, ApplicationFinance applicationFinance){
        if(cost == null) {
            return null;
        }

        ApplicationFinanceRow applicationFinanceRow = new ApplicationFinanceRow(cost.getId(), cost.getName(), cost.getItem(), cost.getDescription(),
                cost.getQuantity(), cost.getCost(), applicationFinance, cost.getQuestion());

        applicationFinanceRow.setFinanceRowMetadata(cost.getFinanceRowMetadata());
        return applicationFinanceRow;
    }

    private ApplicationFinanceRow buildProjectFinanceRow(ProjectFinanceRow cost, ApplicationFinance applicationFinance){
        if(cost == null) {
            return null;
        }

        ApplicationFinanceRow applicationFinanceRow = new ApplicationFinanceRow(cost.getId(), cost.getName(), cost.getItem(), cost.getDescription(),
                cost.getQuantity(), cost.getCost(), applicationFinance, cost.getQuestion());

        applicationFinanceRow.setFinanceRowMetadata(cost.getFinanceRowMetadata());
        return applicationFinanceRow;
    }

    private FinanceRowItem getApplicationCostItem(FinanceRowType financeRowType, ApplicationFinanceRow applicationCost){
        FinanceRowHandler financeRowHandler = getCostHandler(financeRowType);
        return financeRowHandler.toCostItem(applicationCost);
    }

    private List<ProjectFinanceRow> getProjectCosts(Long projectFinanceId){
        List<ProjectFinanceRow> projectCosts = projectFinanceRowRepository.findByTargetId(projectFinanceId);
        return projectCosts;
    }

    private List<ApplicationFinanceRow> getApplicationCosts(Long applicationId, Long organisationId){
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        List<ApplicationFinanceRow> applicationCosts = applicationFinanceRowRepository.findByTargetId(applicationFinance.getId());
        return applicationCosts;
    }
}
