package com.worth.ifs.finance.resource.cost;

/**
 * TODO INFUND-5192
 * There is not currently a good way of generating {@link com.worth.ifs.project.finance.domain.CostCategory} for academic partners in an extendable way.
 * This will need to be addressed, but in the meantime this enum hard codes the information.
 */
public enum AcademicCostCategoryGenerator implements CostCategoryGenerator {
    DIRECTLY_INCURRED_STAFF("Directly incurred", "Staff"),
    DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE("Directly incurred", "Staff"),
    DIRECTLY_INCURRED_OTHER_COSTS("Directly incurred", "Other costs"),
    DIRECTLY_ALLOCATED_INVESTIGATORS("Directly allocated", "Investigators"),
    DIRECTLY_ALLOCATED_ESTATES_COSTS("Directly allocated", "Estates costs"),
    DIRECTLY_ALLOCATED_OTHER_COSTS("Directly allocated", "Other costs"),
    INDIRECT_COSTS_STAFF("Indirect costs", "Staff"),
    INDIRECT_COSTS_OTHER_COSTS("Indirect Costs", "Other costs");


    private final String name;
    private final String label;
    AcademicCostCategoryGenerator(String label, String name){
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
    public String getLabel() {
        return label;
    }
}
