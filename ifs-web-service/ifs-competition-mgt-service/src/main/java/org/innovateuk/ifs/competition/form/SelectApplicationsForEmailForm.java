package org.innovateuk.ifs.competition.form;

import org.innovateuk.ifs.application.resource.FundingDecision;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SelectApplicationsForEmailForm {

    @NotNull
    private List<Long> ids;
    private boolean allSelected;

    public SelectApplicationsForEmailForm() {
        this.ids = new ArrayList<>();
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }
}
