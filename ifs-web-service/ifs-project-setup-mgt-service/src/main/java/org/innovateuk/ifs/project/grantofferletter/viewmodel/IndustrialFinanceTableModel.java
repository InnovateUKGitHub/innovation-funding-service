package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;

import java.util.List;
import java.util.Map;

/*
* Holder of values for the industrial finance table, used on the grant offer letter template page
*/

public class IndustrialFinanceTableModel extends BaseFinanceTableModel {

    private boolean showTotalsColumn;
    private Map<String, ProjectFinanceResource> finances;
    private List<String> organisations;

    public IndustrialFinanceTableModel(boolean showTotalsColumn,
                                       Map<String, ProjectFinanceResource> finances,
                                       List<String> organisations) {
        this.showTotalsColumn = showTotalsColumn;
        this.finances = finances;
        this.organisations = organisations;
    }

    public boolean isShowTotalsColumn() {
        return showTotalsColumn;
    }

    public Map<String, ProjectFinanceResource> getFinances() {
        return finances;
    }

    public List<String> getOrganisations() {
        return organisations;
    }
}
