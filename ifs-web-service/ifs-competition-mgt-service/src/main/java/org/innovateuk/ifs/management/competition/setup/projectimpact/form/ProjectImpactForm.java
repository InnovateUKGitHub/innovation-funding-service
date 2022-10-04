package org.innovateuk.ifs.management.competition.setup.projectimpact.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

import static java.util.Objects.isNull;

/**
 * Form for the Project Impact competition setup section.
 */
public class ProjectImpactForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.projectImpactForm.projectImpactSurveyApplicable.required}")
    private Boolean projectImpactSurveyApplicable;



    public Boolean getProjectImpactSurveyApplicable() {
        return projectImpactSurveyApplicable;
    }

    public void setProjectImpactSurveyApplicable(Boolean projectImpactSurveyApplicable) {
        this.projectImpactSurveyApplicable = projectImpactSurveyApplicable;
    }


}