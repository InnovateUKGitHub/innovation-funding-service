package org.innovateuk.ifs.finance.resource.cost;

import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * FinanceRow types are used to identify the different categories that costs can have
 */
public enum FinanceRowType implements CostCategoryGenerator<FinanceRowType> {
    LABOUR("labour", "Labour", IncludeInSpendProfile.YES),
    OVERHEADS("overheads", "Overheads", IncludeInSpendProfile.YES),
    MATERIALS("materials", "Materials", IncludeInSpendProfile.YES),
    CAPITAL_USAGE("capital_usage", "Capital usage", IncludeInSpendProfile.YES),
    SUBCONTRACTING_COSTS("subcontracting", "Subcontracting", IncludeInSpendProfile.YES),
    TRAVEL("travel", "Travel and subsistence", IncludeInSpendProfile.YES),
    OTHER_COSTS("other_costs", "Other costs", IncludeInSpendProfile.YES),
    YOUR_FINANCE("your_finance"),
    FINANCE("finance", "Finance", IncludeInSpendProfile.NO),
    OTHER_FUNDING("other_funding", "Other Funding", IncludeInSpendProfile.NO),
    ACADEMIC("academic");

    enum IncludeInSpendProfile {
        NO,
        YES
    }

    private String type;
    private String name;
    private IncludeInSpendProfile includeInSpendProfile;

    FinanceRowType(String type) {
        this(type, null, IncludeInSpendProfile.NO);
    }

    FinanceRowType(String type, String name, IncludeInSpendProfile includeInSpendProfile) {
        this.type = type;
        this.name = name;
        this.includeInSpendProfile = includeInSpendProfile;
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

    public boolean isIncludedInSpendProfile() {
        return includeInSpendProfile.equals(IncludeInSpendProfile.YES);
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
