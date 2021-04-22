package org.innovateuk.ifs.finance.resource.cost;

public enum ProcurementCostCategoryGenerator implements CostCategoryGenerator<ProcurementCostCategoryGenerator> {

    OTHER_COSTS( "Other costs"),
    VAT( "VAT");

    private final String name;

    ProcurementCostCategoryGenerator(String name) {
        this.name = name;
    }

    public static ProcurementCostCategoryGenerator fromFinanceRowType(FinanceRowType costType) {
        if (costType == FinanceRowType.VAT) {
            return VAT;
        } else {
            return OTHER_COSTS;
        }
    }

    @Override
    public boolean isIncludedInSpendProfile() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getLabel() {
        return null;
    }

}
