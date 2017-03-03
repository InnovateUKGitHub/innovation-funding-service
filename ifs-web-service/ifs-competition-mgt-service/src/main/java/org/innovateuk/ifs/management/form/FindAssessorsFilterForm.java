package org.innovateuk.ifs.management.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.Optional;

public class FindAssessorsFilterForm extends BaseBindingResultTarget {

    private Optional<Long> innovationArea = Optional.empty();

    public Optional<Long> getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(Optional<Long> innovationArea) {
        this.innovationArea = innovationArea;
    }
}
