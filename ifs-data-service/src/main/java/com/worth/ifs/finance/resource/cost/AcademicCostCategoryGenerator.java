package com.worth.ifs.finance.resource.cost;

/**
 * TODO INFUND-5192
 * There is not currently a good way of generating {@link com.worth.ifs.project.finance.domain.CostCategory} for academic partners in an extendable way.
 * This will need to be addressed, but in the meantime this enum hard codes the information.
 */
public enum AcademicCostCategoryGenerator implements CostCategoryGenerator {
    DIRECTLY_INCURRED_STAFF("Directly Incurred", "Staff"),
    DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE("Directly Incurred", "Staff"),
    DIRECTLY_INCURRED_OTHER_COSTS("Directly Incurred", "Other Costs"),
    DIRECTLY_ALLOCATED_INVESTIGATORS("Directly Allocated", "investigators"),
    DIRECTLY_ALLOCATED_ESTATES_COSTS("Directly Allocated", "Estates Costs"),
    DIRECTLY_ALLOCATED_OTHER_COSTS("directly_allocated", "Other Costs"),
    INDIRECT_COSTS_STAFF("Indirect Costs", "Staff"),
    INDIRECT_COSTS_OTHER_COSTS("Indirect Costs", "Other Costs");


    private final String name;
    private final String label;
    AcademicCostCategoryGenerator(String name, String label){
        this.name = name;
        this.label = label;
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
    public String label() {
        return label;
    }
}
