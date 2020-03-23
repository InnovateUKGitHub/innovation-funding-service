package org.innovateuk.ifs.management.assessment.form;

import java.util.ArrayList;
import java.util.List;

public class AvailableAssessorForm {

    private boolean allSelected;
    private List<Long> selectedAssessors;

    public AvailableAssessorForm() {
        this.selectedAssessors = new ArrayList<>();
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<Long> getSelectedAssessors() {
        return selectedAssessors;
    }

    public void setSelectedAssessors(List<Long> selectedAssessors) {
        this.selectedAssessors = selectedAssessors;
    }

}
