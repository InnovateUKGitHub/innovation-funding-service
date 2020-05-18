package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

/**
 * Form for the organisational eligibility competition setup section where international organisation can lead the competition.
 */
public class LeadInternationalOrganisationForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.organisationaleligibilityform.leadInternationalOrganisationApplicable.required}")
    private Boolean leadInternationalOrganisationApplicable;

    public Boolean getLeadInternationalOrganisationApplicable() {
        return leadInternationalOrganisationApplicable;
    }

    public void setLeadInternationalOrganisationApplicable(Boolean leadInternationalOrganisationApplicable) {
        this.leadInternationalOrganisationApplicable = leadInternationalOrganisationApplicable;
    }
}
