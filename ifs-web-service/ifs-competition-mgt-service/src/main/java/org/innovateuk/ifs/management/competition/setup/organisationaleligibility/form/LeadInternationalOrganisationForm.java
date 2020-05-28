package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form;

import javax.validation.constraints.NotNull;

/**
 * Form for the organisational eligibility competition setup section where international organisation can lead the competition.
 */
public class LeadInternationalOrganisationForm {

    @NotNull(message = "{validation.leadinternationalorganisationform.leadInternationalOrganisationsApplicable.required}")
    private Boolean leadInternationalOrganisationsApplicable;

    private Boolean internationalOrganisationsApplicable;

    public Boolean getLeadInternationalOrganisationsApplicable() {
        return leadInternationalOrganisationsApplicable;
    }

    public void setLeadInternationalOrganisationsApplicable(Boolean leadInternationalOrganisationsApplicable) {
        this.leadInternationalOrganisationsApplicable = leadInternationalOrganisationsApplicable;
    }

    public Boolean getInternationalOrganisationsApplicable() {
        return internationalOrganisationsApplicable;
    }

    public void setInternationalOrganisationsApplicable(Boolean internationalOrganisationsApplicable) {
        this.internationalOrganisationsApplicable = internationalOrganisationsApplicable;
    }
}
