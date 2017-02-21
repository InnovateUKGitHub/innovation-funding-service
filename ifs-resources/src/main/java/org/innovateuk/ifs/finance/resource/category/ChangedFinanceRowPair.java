package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import static org.innovateuk.ifs.finance.resource.category.TypeOfChange.UNKNOWN;

/**
 * Created by rav on 20/02/2017.
 */
public class ChangedFinanceRowPair<L extends FinanceRowItem, R extends FinanceRowItem> {

    private TypeOfChange typeOfChange;

    private FinanceRowItem applicationFinanceRowItem;
    
    private FinanceRowItem projectFinanceRowItem;

    public ChangedFinanceRowPair(FinanceRowItem applicationFinanceRowItem, FinanceRowItem projectFinanceRowItem) {
        this(UNKNOWN, applicationFinanceRowItem, projectFinanceRowItem);
    }

    public ChangedFinanceRowPair(TypeOfChange typeOfChange, FinanceRowItem applicationFinanceRowItem, FinanceRowItem projectFinanceRowItem) {
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

    public static ChangedFinanceRowPair of(TypeOfChange typeOfChange, FinanceRowItem l, FinanceRowItem r){
        return new ChangedFinanceRowPair(typeOfChange, l, r);
    }

    public static ChangedFinanceRowPair of(FinanceRowItem l, FinanceRowItem r){
        return new ChangedFinanceRowPair(l, r);
    }
}
