package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import static org.innovateuk.ifs.finance.resource.category.TypeOfChange.UNKNOWN;

/**
 * Used for returning a pair of rows that can be used to display how application finances were changed by project finance updates.
 * INFUND-4837
 */
public class ChangedFinanceRowPair<L extends FinanceRowItem, R extends FinanceRowItem> {

    private TypeOfChange typeOfChange;

    private FinanceRowItem applicationFinanceRowItem;
    
    private FinanceRowItem projectFinanceRowItem;

    private ChangedFinanceRowPair(TypeOfChange typeOfChange, FinanceRowItem applicationFinanceRowItem, FinanceRowItem projectFinanceRowItem) {
        this.typeOfChange = typeOfChange;
        this.applicationFinanceRowItem= applicationFinanceRowItem;
        this.projectFinanceRowItem = projectFinanceRowItem;
    }

    public ChangedFinanceRowPair() {
    }

    public TypeOfChange getTypeOfChange() {
        return typeOfChange;
    }

    public void setTypeOfChange(TypeOfChange typeOfChange) {
        this.typeOfChange = typeOfChange;
    }

    public FinanceRowItem getApplicationFinanceRowItem() {
        return applicationFinanceRowItem;
    }

    public void setApplicationFinanceRowItem(FinanceRowItem applicationFinanceRowItem) {
        this.applicationFinanceRowItem = applicationFinanceRowItem;
    }

    public FinanceRowItem getProjectFinanceRowItem() {
        return projectFinanceRowItem;
    }

    public void setProjectFinanceRowItem(FinanceRowItem projectFinanceRowItem) {
        this.projectFinanceRowItem = projectFinanceRowItem;
    }

    public static ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> of(TypeOfChange typeOfChange, FinanceRowItem applicationFinanceRowItem, FinanceRowItem projectFinanceRowItem){
        return new ChangedFinanceRowPair<>(typeOfChange, applicationFinanceRowItem, projectFinanceRowItem);
    }
}
