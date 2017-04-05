package org.innovateuk.ifs.application.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Bean serves as a container for form parameters.
 */

public class InnovationAreaForm extends BaseBindingResultTarget {
    @NotNull(message = "{validation.application.innovationarea.category.required}")
    private String innovationAreaChoice;

    public String getInnovationAreaChoice() {
        return innovationAreaChoice;
    }

    public void setInnovationAreaChoice(String innovationAreaChoice) {
        this.innovationAreaChoice = innovationAreaChoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InnovationAreaForm that = (InnovationAreaForm) o;

        return innovationAreaChoice != null ? innovationAreaChoice.equals(that.innovationAreaChoice) : that.innovationAreaChoice == null;
    }

    @Override
    public int hashCode() {
        return innovationAreaChoice != null ? innovationAreaChoice.hashCode() : 0;
    }
}
