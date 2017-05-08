package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

/**
 * Created by luke.harper on 08/05/2017.
 */
public abstract class AbstractCostViewModel extends AbstractFormInputViewModel {

    private FinanceRowCostCategory costCategory;
    private String viewmode;

    public abstract FinanceRowType getFinanceRowType();

    public FinanceRowCostCategory getCostCategory() {
        return costCategory;
    }

    public void setCostCategory(FinanceRowCostCategory costCategory) {
        this.costCategory = costCategory;
    }

    public String getViewmode() {
        return viewmode;
    }

    public void setViewmode(String viewmode) {
        this.viewmode = viewmode;
    }
}
