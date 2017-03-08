package org.innovateuk.ifs.application.form;

import javax.validation.constraints.NotNull;

/**
 * Beam serves as a container for form parameters.
 */

public class ResearchCategoryForm {
    @NotNull(message = "{validation.application.research.category.required}")
    String researchCategoryChoice;

    public String getResearchCategoryChoice() {
        return researchCategoryChoice;
    }

    public void setResearchCategoryChoice(String researchCategoryChoice) {
        this.researchCategoryChoice = researchCategoryChoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResearchCategoryForm that = (ResearchCategoryForm) o;

        return researchCategoryChoice != null ? researchCategoryChoice.equals(that.researchCategoryChoice) : that.researchCategoryChoice == null;
    }

    @Override
    public int hashCode() {
        return researchCategoryChoice != null ? researchCategoryChoice.hashCode() : 0;
    }
}
