package org.innovateuk.ifs.management.assessment.form;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSelectionForm {

    private boolean allSelected;
    private List<Long> selectedApplications;

    public ApplicationSelectionForm() {
        this.selectedApplications = new ArrayList<>();
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<Long> getSelectedApplications() {
        return selectedApplications;
    }

    public void setSelectedApplications(List<Long> selectedApplications) {
        this.selectedApplications = selectedApplications;
    }
}
