package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

/**
 * Used for returning a pair of rows that can be used to display how application finances were changed by project finance updates.
 * INFUND-4837
 */
public class ChangedFinanceRowPair<L extends FinanceRowItem, R extends FinanceRowItem> {

    private TypeOfChange typeOfChange;

    private FinanceRowItem submitted;
    
    private FinanceRowItem changed;

    private ChangedFinanceRowPair(TypeOfChange typeOfChange, FinanceRowItem submitted, FinanceRowItem changed) {
        this.typeOfChange = typeOfChange;
        this.submitted = submitted;
        this.changed = changed;
    }

    public ChangedFinanceRowPair() {
    }

    public TypeOfChange getTypeOfChange() {
        return typeOfChange;
    }

    public void setTypeOfChange(TypeOfChange typeOfChange) {
        this.typeOfChange = typeOfChange;
    }

    public FinanceRowItem getSubmitted() {
        return submitted;
    }

    public void setSubmitted(FinanceRowItem submitted) {
        this.submitted = submitted;
    }

    public FinanceRowItem getChanged() {
        return changed;
    }

    public void setChanged(FinanceRowItem changed) {
        this.changed = changed;
    }

    public static ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem> of(TypeOfChange typeOfChange,
                                                                           FinanceRowItem applicationFinanceRowItem,
                                                                           FinanceRowItem projectFinanceRowItem) {
        return new ChangedFinanceRowPair<>(typeOfChange, applicationFinanceRowItem, projectFinanceRowItem);
    }
}
