package org.innovateuk.ifs.finance.resource.cost;

import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FinanceRowOptions.COST;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FinanceRowOptions.INCLUDE_IN_SPEND_PROFILE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * FinanceRow types are used to identify the different categories that costs can have
 */
public enum FinanceRowType implements CostCategoryGenerator<FinanceRowType> {
    LABOUR("labour", "Labour", INCLUDE_IN_SPEND_PROFILE, COST),
    OVERHEADS("overheads", "Overheads", INCLUDE_IN_SPEND_PROFILE, COST),
    PROCUREMENT_OVERHEADS("procurement_overheads", "Procurement overheads", INCLUDE_IN_SPEND_PROFILE, COST),
    MATERIALS("materials", "Materials", INCLUDE_IN_SPEND_PROFILE, COST),
    CAPITAL_USAGE("capital_usage", "Capital usage", INCLUDE_IN_SPEND_PROFILE, COST),
    SUBCONTRACTING_COSTS("subcontracting", "Subcontracting", INCLUDE_IN_SPEND_PROFILE, COST),
    TRAVEL("travel", "Travel and subsistence", INCLUDE_IN_SPEND_PROFILE, COST),
    OTHER_COSTS("other_costs", "Other costs", INCLUDE_IN_SPEND_PROFILE, COST),
    YOUR_FINANCE("your_finance"), // Only used for TSB Reference in Je-S finances.
    FINANCE("finance", "Finance"), // Grant claim percentage
    GRANT_CLAIM_AMOUNT("grant_claim_amount", "Finance"),
    OTHER_FUNDING("other_funding", "Other Funding"),
    VAT("vat", "", COST);

    enum FinanceRowOptions {
        INCLUDE_IN_SPEND_PROFILE,
        COST
    }

    private String type;
    private String name;
    private Set<FinanceRowOptions> financeRowOptionsList;

    FinanceRowType(String type) {
        this(type, null);
    }

    FinanceRowType(String type, String name, FinanceRowOptions... options) {
        this.type = type;
        this.name = name;
        this.financeRowOptionsList = asSet(options);
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean isIncludedInSpendProfile() {
        return financeRowOptionsList.contains(INCLUDE_IN_SPEND_PROFILE);
    }

    public boolean isCost() {
        return financeRowOptionsList.contains(COST);
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public static Optional<FinanceRowType> getByName(String name) {
        return simpleFindFirst(
                FinanceRowType.values(),
                frt -> frt.getName().equals(name)
        );
    }
}
