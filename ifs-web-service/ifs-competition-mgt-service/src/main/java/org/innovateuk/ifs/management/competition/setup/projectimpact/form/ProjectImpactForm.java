package org.innovateuk.ifs.management.competition.setup.projectimpact.form;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

/**
 * Form for the Project Impact competition setup section.
 */
@Getter
@Setter
public class ProjectImpactForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.projectImpactForm.projectImpactSurveyApplicable.required}")
    private Boolean projectImpactSurveyApplicable;

}