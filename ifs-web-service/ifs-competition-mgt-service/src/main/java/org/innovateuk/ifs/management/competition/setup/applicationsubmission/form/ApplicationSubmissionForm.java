package org.innovateuk.ifs.management.competition.setup.applicationsubmission.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

/**
 * Form to capture the selection of an Application Submission in Competition Setup.
 */
public class ApplicationSubmissionForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.applicationsubmissionform.application.submission.required}")
    private Boolean alwaysOpen;

    public ApplicationSubmissionForm() {
    }

    public ApplicationSubmissionForm(Boolean alwaysOpen) {
        this.alwaysOpen = alwaysOpen;
    }

    public Boolean getAlwaysOpen() {
        return alwaysOpen;
    }

    public void setAlwaysOpen(Boolean alwaysOpen) {
        this.alwaysOpen = alwaysOpen;
    }
}
