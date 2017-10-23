package org.innovateuk.ifs.finance.resource.cost;

import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FinanceRowOptions.INCLUDE_IN_SPEND_PROFILE;

/**
 * FinanceRow types are used to identify the different categories that costs can have
 */
public enum FinanceRowType implements CostCategoryGenerator<FinanceRowType> {
    LABOUR("labour", "Labour", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    OVERHEADS("overheads", "Overheads", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    MATERIALS("materials", "Materials", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    CAPITAL_USAGE("capital_usage", "Capital usage", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    SUBCONTRACTING_COSTS("subcontracting", "Subcontracting", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    TRAVEL("travel", "Travel and subsistence", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    OTHER_COSTS("other_costs", "Other costs", singletonList(INCLUDE_IN_SPEND_PROFILE)),
    YOUR_FINANCE("your_finance"),
    FINANCE("finance", "Finance", emptyList()),
    OTHER_FUNDING("other_funding", "Other Funding", emptyList()),
    ACADEMIC("academic");

    enum FinanceRowOptions {
        INCLUDE_IN_SPEND_PROFILE
    }

    private String type;
    private String name;
    private List<FinanceRowOptions> financeRowOptionsList;

    FinanceRowType(String type) {
        this(type, null, emptyList());
    }

    FinanceRowType(String type, String name, List<FinanceRowOptions> financeRowOptionsList) {
        this.type = type;
        this.name = name;
        this.financeRowOptionsList = financeRowOptionsList;
    }

    public static FinanceRowType fromType(FormInputType formInputType) {
        if (formInputType != null) {
            for (FinanceRowType costType : values()) {
                if (formInputType == FormInputType.findByName(costType.getType())) {
                    return costType;
                }
            }
        }
        throw new IllegalArgumentException("No valid FinanceRowType found for FormInputType: " + formInputType);
    }

    public String getType() {
        return type;
    }

    public FormInputType getFormInputType() {
        return FormInputType.findByName(getType());
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

    public static Optional<FinanceRowType> getByTypeName(String typeName) {
        return asList(FinanceRowType.values()).stream().filter(frt -> frt.getType().equals(typeName)).findFirst();
    }
}
