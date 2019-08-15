package org.innovateuk.ifs.finance.resource.cost;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FinanceRowOptions.INCLUDE_IN_SPEND_PROFILE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * FinanceRow types are used to identify the different categories that costs can have
 */
public enum FinanceRowType implements CostCategoryGenerator<FinanceRowType> {
    LABOUR("labour", "Labour", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    OVERHEADS("overheads", "Overheads", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    PROCUREMENT_OVERHEADS("procurement_overheads", "Procurement overheads", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    MATERIALS("materials", "Materials", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    CAPITAL_USAGE("capital_usage", "Capital usage", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    SUBCONTRACTING_COSTS("subcontracting", "Subcontracting", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    TRAVEL("travel", "Travel and subsistence", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    OTHER_COSTS("other_costs", "Other costs", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    YOUR_FINANCE("your_finance"),// Should this be a finance row type? its TSB reference for academics.
    FINANCE("finance", "Finance"), // Grant claim percentage I WANT TO RENAME THIS
    GRANT_CLAIM_AMOUNT("grant_claim_amount", "Finance"),
    OTHER_FUNDING("other_funding", "Other Funding"),
    ACADEMIC("academic"); // Should this be a finance row type?

    enum FinanceRowOptions {
        INCLUDE_IN_SPEND_PROFILE
    }

    private String type;
    private String name;
    private List<FinanceRowOptions> financeRowOptionsList;

    FinanceRowType(String type) {
        this(type, null);
    }

    FinanceRowType(String type, String name) {
        this(type, name, emptyList());
    }

    FinanceRowType(String type, String name, List<FinanceRowOptions> financeRowOptionsList) {
        this.type = type;
        this.name = name;
        this.financeRowOptionsList = financeRowOptionsList;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean isIncludedInSpendProfile() {
        return financeRowOptionsList.stream().anyMatch(financeRowOptions -> financeRowOptions.equals(INCLUDE_IN_SPEND_PROFILE));
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
