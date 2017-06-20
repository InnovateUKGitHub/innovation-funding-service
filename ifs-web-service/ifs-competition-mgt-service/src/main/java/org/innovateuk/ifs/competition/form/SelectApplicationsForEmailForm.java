package org.innovateuk.ifs.competition.form;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SelectApplicationsForEmailForm {

    @NotNull
    private List<String> ids;
    private boolean allSelected;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }
}
