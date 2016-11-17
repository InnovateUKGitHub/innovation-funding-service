package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.project.finance.domain.CostCategory;

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO INFUND-5192
 * There is not currently a good way of generating {@link com.worth.ifs.project.finance.domain.CostCategory} for academic partners in an extendable way.
 * This will need to be addressed, but in the meantime this enum hard codes the information.
 */
public enum AcademicCostCategoryGenerator implements CostCategoryGenerator<AcademicCostCategoryGenerator> {
    DIRECTLY_INCURRED_STAFF("Directly incurred", "Staff", "incurred_staff"),
    DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE("Directly incurred", "Travel and subsistence", "incurred_travel_subsistence"),
    DIRECTLY_INCURRED_OTHER_COSTS("Directly incurred", "Other costs", "incurred_other_costs"),
    DIRECTLY_ALLOCATED_INVESTIGATORS("Directly allocated", "Investigations", "allocated_investigators"),
    DIRECTLY_ALLOCATED_ESTATES_COSTS("Directly allocated", "Estates costs", "allocated_estates_costs"),
    DIRECTLY_ALLOCATED_OTHER_COSTS("Directly allocated", "Other costs", "allocated_other_costs"),
    INDIRECT_COSTS("Indirect costs", "Investigations", "indirect_costs"),
    INDIRECT_COSTS_STAFF("Exceptions", "Staff", "exceptions_staff"),
    INDIRECT_COSTS_OTHER_COSTS("Exceptions", "Other costs", "exceptions_other_costs");

    private final String name;
    private final String label;
    private final String financeRowName;
    AcademicCostCategoryGenerator(String label, String name, String financeRowName){
        this.name = name;
        this.label = label;
        this.financeRowName = financeRowName;
    }


    @Override
    public boolean isSpendCostCategory() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public String getFinanceRowName() {
        return financeRowName;
    }

    public static BigDecimal findCost(CostCategory academicCostCategory, List<? extends FinanceRow> rows){
        String financeRowName = from(academicCostCategory).getFinanceRowName();
        return rows.stream().filter(row -> financeRowName.equals(row.getName())).findFirst().map(row -> row.getCost()).orElseGet(() -> BigDecimal.ZERO);
    }

    private static AcademicCostCategoryGenerator from(CostCategory academicCostCategory){
        for (AcademicCostCategoryGenerator value: AcademicCostCategoryGenerator.values()){
            if (value.getLabel().equals(academicCostCategory.getLabel()) && value.getName().equals(academicCostCategory.getName())){
                return value;
            }
        }
        return null;
    }
}
