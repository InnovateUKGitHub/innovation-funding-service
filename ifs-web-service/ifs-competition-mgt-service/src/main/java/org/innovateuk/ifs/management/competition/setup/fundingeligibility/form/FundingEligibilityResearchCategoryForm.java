package org.innovateuk.ifs.management.competition.setup.fundingeligibility.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class FundingEligibilityResearchCategoryForm extends CompetitionSetupForm {
    @NotNull(message = "{validation.eligibilityform.researchCategoriesApplicable.required}")
    private Boolean researchCategoriesApplicable;

    private Set<Long> researchCategoryId;

    public Boolean getResearchCategoriesApplicable() {
        return researchCategoriesApplicable;
    }

    public void setResearchCategoriesApplicable(Boolean researchCategoriesApplicable) {
        this.researchCategoriesApplicable = researchCategoriesApplicable;
    }

    public Set<Long> getResearchCategoryId() {
        return researchCategoryId;
    }

    public void setResearchCategoryId(Set<Long> researchCategoryId) {
        this.researchCategoryId = researchCategoryId;
    }

    public boolean includesResearchCategory(Long id) {
        return researchCategoryId != null && researchCategoryId.contains(id);
    }
}
