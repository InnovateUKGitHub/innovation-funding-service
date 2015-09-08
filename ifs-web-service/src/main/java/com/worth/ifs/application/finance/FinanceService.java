package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.UserApplicationRole;
import com.worth.ifs.service.ApplicationFinanceService;
import com.worth.ifs.service.CostService;
import com.worth.ifs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;

@Service
public class FinanceService {

    @Autowired
    UserService userService;

    @Autowired
    ApplicationFinanceService applicationFinanceService;

    @Autowired
    CostService costService;

    EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);
    CostItemFactory costItemFactory = new CostItemFactory();

    public Double getTotal() {
        return costCategories.entrySet().stream().mapToDouble(cat -> cat.getValue().getTotal()).sum();
    }

    public EnumMap<CostType, CostCategory> getCostCategories() {
        return costCategories;
    }

    public void setCosts(Long applicationId, Long userId) {
        ApplicationFinance applicationFinance = getApplicationFinance(applicationId, userId);
        createCostCategories();
        addCostsToCategories(applicationFinance.getId());
    }

    private ApplicationFinance getApplicationFinance(Long applicationId, Long userId) {
        UserApplicationRole userApplicationRole = userService.findUserApplicationRole(applicationId, userId);
        return applicationFinanceService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    private void createCostCategories() {
        for(CostType costType : CostType.values()) {
            CostCategory costCategory = createCostCategoryByType(costType);
            costCategories.put(costType, costCategory);
        }
    }

    private void addCostsToCategories(Long applicationFinanceId) {
        List<Cost> costs = costService.getCosts(applicationFinanceId);
        costs.stream().forEach(c -> addCostToCategory(c));
    }

    private void addCostToCategory(Cost cost) {
        CostType costType = CostType.fromString(cost.getQuestion().getQuestionType().getTitle());
        CostCategory costCategory = costCategories.get(costType);
        CostItem costItem = costItemFactory.createCostItem(costType, cost);
        costCategory.addCost(costItem);
    }

    private CostCategory createCostCategoryByType(CostType costType) {
        switch(costType) {
            case LABOUR:
                return new LabourCostCategory();
            default:
                return new DefaultCostCategory();
        }
    }

}
