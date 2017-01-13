package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAvailableAssessorsViewModel that = (ApplicationAvailableAssessorsViewModel) o;

        return new EqualsBuilder()
                .append(availableAssessors, that.availableAssessors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(availableAssessors)
                .toHashCode();
    }
}
