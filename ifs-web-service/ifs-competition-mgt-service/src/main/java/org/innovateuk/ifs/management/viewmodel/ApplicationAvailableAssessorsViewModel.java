package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

public class ApplicationAvailableAssessorsViewModel {

    private List<ApplicationAvailableAssessorsRowViewModel> availableAssessors;

    public ApplicationAvailableAssessorsViewModel(List<ApplicationAvailableAssessorsRowViewModel> availableAssessors) {
        this.availableAssessors = availableAssessors;
    }

    public List<ApplicationAvailableAssessorsRowViewModel> getAvailableAssessors() {
        return availableAssessors;
    }

    public void setAvailableAssessors(List<ApplicationAvailableAssessorsRowViewModel> availableAssessors) {
        this.availableAssessors = availableAssessors;
    }
}
