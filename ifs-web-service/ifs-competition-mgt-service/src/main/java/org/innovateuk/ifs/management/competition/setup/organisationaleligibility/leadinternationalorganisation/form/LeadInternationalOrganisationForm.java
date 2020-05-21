package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

/**
 * Form for the organisational eligibility competition setup section where international organisation can lead the competition.
 */
public class LeadInternationalOrganisationForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.organisationaleligibilityform.leadInternationalOrganisationsApplicable.required}")
    private Boolean leadInternationalOrganisationsApplicable;

    public Boolean getLeadInternationalOrganisationsApplicable() {
        return leadInternationalOrganisationsApplicable;
    }

    public void setLeadInternationalOrganisationsApplicable(Boolean leadInternationalOrganisationsApplicable) {
        this.leadInternationalOrganisationsApplicable = leadInternationalOrganisationsApplicable;
    }
}
