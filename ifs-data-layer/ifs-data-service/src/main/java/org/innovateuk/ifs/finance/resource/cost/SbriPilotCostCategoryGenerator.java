package org.innovateuk.ifs.finance.resource.cost;

public enum SbriPilotCostCategoryGenerator implements CostCategoryGenerator<SbriPilotCostCategoryGenerator> {

    OTHER_COSTS( "Other costs"),
    VAT( "VAT");

    private final String name;

    SbriPilotCostCategoryGenerator(String name) {
        this.name = name;
    }

    public static SbriPilotCostCategoryGenerator fromFinanceRowType(FinanceRowType costType) {
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
