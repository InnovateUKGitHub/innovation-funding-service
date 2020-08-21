package org.innovateuk.ifs.finance.resource.cost;

import org.innovateuk.ifs.project.financechecks.domain.CostCategory;

/**
 * TODO INFUND-5192
 * There is not currently a good way of generating {@link CostCategory} for academic partners in an extendable way.
 * This will need to be addressed, but in the meantime this enum hard codes the information.
 */
public enum SbriPilotCostCategoryGenerator implements CostCategoryGenerator<SbriPilotCostCategoryGenerator> {

    OTHER_COSTS("other_costs", "Other costs"),
    VAT("vat", "VAT");

    private final String label;
    private final String name;

    SbriPilotCostCategoryGenerator(String label, String name) {
        this.label = label;
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
        return label;
    }

}
