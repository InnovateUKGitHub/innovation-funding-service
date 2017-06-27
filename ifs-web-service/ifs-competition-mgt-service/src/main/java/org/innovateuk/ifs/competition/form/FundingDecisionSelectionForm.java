package org.innovateuk.ifs.competition.form;

import java.util.ArrayList;
import java.util.List;

/**
 * Form bean used to encapsulate information needed to make funding decisions.
 */
public class FundingDecisionSelectionForm {
    private List<Long> applicationIds = new ArrayList<>();

    private boolean allSelected;

    public List<Long> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Long> applicationIds) {
        this.applicationIds = applicationIds;
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public boolean anySelectionIsMade() {
        return this.isAllSelected() != false ||
                this.getApplicationIds().size() > 0;
    }

    public boolean containsAll(List<Long> applicationIds) {
        return this.getApplicationIds().containsAll(applicationIds);
    }
}
