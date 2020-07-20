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
    /* Grant */
    LABOUR("labour", "Labour", INCLUDE_IN_SPEND_PROFILE, COST),
    OVERHEADS("overheads", "Overheads", INCLUDE_IN_SPEND_PROFILE, COST),
    MATERIALS("materials", "Materials", INCLUDE_IN_SPEND_PROFILE, COST),
    CAPITAL_USAGE("capital_usage", "Capital usage", INCLUDE_IN_SPEND_PROFILE, COST),
    SUBCONTRACTING_COSTS("subcontracting", "Subcontracting", INCLUDE_IN_SPEND_PROFILE, COST),
    TRAVEL("travel", "Travel and subsistence", INCLUDE_IN_SPEND_PROFILE, COST),
    OTHER_COSTS("other_costs", "Other costs", INCLUDE_IN_SPEND_PROFILE, COST),
    YOUR_FINANCE("your_finance"), // Only used for TSB Reference in Je-S finances.
    FINANCE("finance", "Finance"), // Grant claim percentage
    OTHER_FUNDING("other_funding", "Other Funding"),

    /* Loans */
    GRANT_CLAIM_AMOUNT("grant_claim_amount", "Finance"),

    /* Procurement */
    VAT("vat", "", COST),
    PROCUREMENT_OVERHEADS("procurement_overheads", "Procurement overheads", INCLUDE_IN_SPEND_PROFILE, COST),

    /* KTP */
    ASSOCIATE_SALARY_COSTS("associate_salary_costs", "Associate salary costs", INCLUDE_IN_SPEND_PROFILE, COST),
    ASSOCIATE_DEVELOPMENT_COSTS("associate_development", "Associate development costs", INCLUDE_IN_SPEND_PROFILE, COST),
    CONSUMABLES("consumables", "Consumables", INCLUDE_IN_SPEND_PROFILE, COST),
    ASSOCIATE_SUPPORT("associate_support", "Associate support costs", INCLUDE_IN_SPEND_PROFILE, COST),
    KNOWLEDGE_BASE("knowledge_base", "Knowledge base advisor", INCLUDE_IN_SPEND_PROFILE, COST),
    ESTATE_COSTS("estate_costs", "Estate", INCLUDE_IN_SPEND_PROFILE, COST),
    ADDITIONAL_COMPANY_COSTS("additional_company_costs", "Additional company costs");

    enum FinanceRowOptions {
        INCLUDE_IN_SPEND_PROFILE,
        COST
    }

    private String type;
    private String displayName;
    private Set<FinanceRowOptions> financeRowOptionsList;

    FinanceRowType(String type) {
        this(type, null);
    }

    FinanceRowType(String type, String displayName, FinanceRowOptions... options) {
        this.type = type;
        this.displayName = displayName;
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
    public String getDisplayName() {
        return displayName;
    }

    public static Optional<FinanceRowType> getByName(String name) {
        return simpleFindFirst(
                FinanceRowType.values(),
                frt -> frt.getDisplayName().equals(name)
        );
    }
}
