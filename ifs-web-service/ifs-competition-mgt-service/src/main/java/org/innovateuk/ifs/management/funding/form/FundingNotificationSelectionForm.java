package org.innovateuk.ifs.management.funding.form;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Form used to keep track of application selections as part of Funding Notification cookie
 */
public class FundingNotificationSelectionForm {

    @NotNull
    private List<Long> ids = new ArrayList<>();
    private boolean allSelected;

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

    public boolean anySelectionIsMade() {
        return this.allSelected != false ||
                !this.ids.isEmpty();
    }
}
