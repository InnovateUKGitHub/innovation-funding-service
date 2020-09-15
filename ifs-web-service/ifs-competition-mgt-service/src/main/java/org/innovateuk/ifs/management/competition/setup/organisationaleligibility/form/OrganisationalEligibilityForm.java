package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

import static java.util.Objects.isNull;

/**
 * Form for the organisational eligibility competition setup section.
 */
public class OrganisationalEligibilityForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.organisationaleligibilityform.internationalOrganisationsApplicable.required}")
    private Boolean internationalOrganisationsApplicable;

    private Boolean leadInternationalOrganisationsApplicable;

    public Boolean getInternationalOrganisationsApplicable() {
        return internationalOrganisationsApplicable;
    }

    public void setInternationalOrganisationsApplicable(Boolean internationalOrganisationsApplicable) {
        this.internationalOrganisationsApplicable = internationalOrganisationsApplicable;
    }

    public Boolean getLeadInternationalOrganisationsApplicable() {
        return leadInternationalOrganisationsApplicable;
    }

    public void setLeadInternationalOrganisationsApplicable(Boolean leadInternationalOrganisationsApplicable) {
        this.leadInternationalOrganisationsApplicable = leadInternationalOrganisationsApplicable;
    }

    public boolean hasNullLeadInternationalOrganisation() {
        return Boolean.TRUE.equals(getInternationalOrganisationsApplicable()) && isNull(leadInternationalOrganisationsApplicable);
    }
}