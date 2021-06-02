package org.innovateuk.ifs.finance.resource.cost;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FinanceRowOptions.COST;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FinanceRowOptions.INCLUDE_IN_SPEND_PROFILE;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FinanceRowOptions.APPEARS_IN_PROJECT_COSTS_ACCORDION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * FinanceRow types are used to identify the different categories that costs can have
 */
public enum FinanceRowType implements CostCategoryGenerator<FinanceRowType> {
    /* Grant */
    LABOUR("labour", "Labour", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    OVERHEADS("overheads", "Overheads", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    MATERIALS("materials", "Materials", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    CAPITAL_USAGE("capital_usage", "Capital usage", INCLUDE_IN_SPEND_PROFILE, COST,APPEARS_IN_PROJECT_COSTS_ACCORDION),
    SUBCONTRACTING_COSTS("subcontracting", "Subcontracting", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    TRAVEL("travel", "Travel and subsistence", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    OTHER_COSTS("other_costs", "Other costs", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    YOUR_FINANCE("your_finance"), // Only used for TSB Reference in Je-S finances.
    FINANCE("finance", "Finance"), // Grant claim percentage
    OTHER_FUNDING("other_funding", "Other Funding"),

    /* Loans */
    GRANT_CLAIM_AMOUNT("grant_claim_amount", "Finance"),

    /* Procurement */
    VAT("vat", "Total VAT", INCLUDE_IN_SPEND_PROFILE, COST),
    PROCUREMENT_OVERHEADS("procurement_overheads", "Overheads", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),

    /* KTP */
    ASSOCIATE_SALARY_COSTS("associate_salary_costs", "Associate Employment", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    ASSOCIATE_DEVELOPMENT_COSTS("associate_development", "Associate development", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    CONSUMABLES("consumables", "Consumables", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    ASSOCIATE_SUPPORT("associate_support", "Additional associate support", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    KNOWLEDGE_BASE("knowledge_base", "Knowledge base supervisor", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    ESTATE_COSTS("estate_costs", "Estate", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    KTP_TRAVEL("travel", "Travel and subsistence", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    ADDITIONAL_COMPANY_COSTS("additional_company_costs", "Additional company costs"),
    PREVIOUS_FUNDING("previous_funding", "Other funding"),
    ACADEMIC_AND_SECRETARIAL_SUPPORT("academic_and_secretarial_support", "Academic and secretarial support", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION),
    INDIRECT_COSTS("indirect costs", "Indirect costs", INCLUDE_IN_SPEND_PROFILE, COST, APPEARS_IN_PROJECT_COSTS_ACCORDION);

    enum FinanceRowOptions {
        INCLUDE_IN_SPEND_PROFILE,
        COST,
        APPEARS_IN_PROJECT_COSTS_ACCORDION
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
        this.financeRowOptionsList = Sets.newHashSet(options);
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

    public boolean isAppearsInProjectCostsAccordion() {
        return financeRowOptionsList.contains(APPEARS_IN_PROJECT_COSTS_ACCORDION);
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

    public static List<FinanceRowType> getFecSpecificFinanceRowTypes() {
        return Stream.of(values())
                .filter(financeRowType -> (financeRowType == FinanceRowType.KNOWLEDGE_BASE
                        || financeRowType == FinanceRowType.ASSOCIATE_SUPPORT
                        || financeRowType == FinanceRowType.ESTATE_COSTS))
                .collect(Collectors.toList());
    }

    public static List<FinanceRowType> getNonFecSpecificFinanceRowTypes() {
        return Stream.of(values())
                .filter(financeRowType -> (financeRowType == FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT
                        || financeRowType == FinanceRowType.INDIRECT_COSTS))
                .collect(Collectors.toList());
    }

    public static List<FinanceRowType> getKtpFinanceRowTypes() {
        return Stream.of(values())
                .filter(financeRowType -> (financeRowType == FinanceRowType.OTHER_COSTS
                        || financeRowType == FinanceRowType.FINANCE
                        || financeRowType == FinanceRowType.ASSOCIATE_SALARY_COSTS
                        || financeRowType == FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS
                        || financeRowType == FinanceRowType.CONSUMABLES
                        || financeRowType == FinanceRowType.ASSOCIATE_SUPPORT
                        || financeRowType == FinanceRowType.KNOWLEDGE_BASE
                        || financeRowType == FinanceRowType.ESTATE_COSTS
                        || financeRowType == FinanceRowType.KTP_TRAVEL
                        || financeRowType == FinanceRowType.ADDITIONAL_COMPANY_COSTS
                        || financeRowType == FinanceRowType.PREVIOUS_FUNDING
                        || financeRowType == FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT
                        || financeRowType == FinanceRowType.INDIRECT_COSTS))
                .collect(Collectors.toList());
    }
}
