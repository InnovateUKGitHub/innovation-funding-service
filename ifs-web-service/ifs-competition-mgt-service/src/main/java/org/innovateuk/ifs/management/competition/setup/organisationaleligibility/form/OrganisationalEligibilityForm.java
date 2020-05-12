package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

public class OrganisationalEligibilityForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.organisationaleligibilityform.internationalOrganisationsApplicable.required}")
    private Boolean internationalOrganisationsApplicable;

    public OrganisationalEligibilityForm() {
    }

    public Boolean getInternationalOrganisationsApplicable() {
        return internationalOrganisationsApplicable;
    }

    public void setInternationalOrganisationsApplicable(Boolean internationalOrganisationsApplicable) {
        this.internationalOrganisationsApplicable = internationalOrganisationsApplicable;
    }
}
