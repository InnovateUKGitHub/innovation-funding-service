package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {
    private final ApplicationTeamResource team;


    public ApplicationTeamReadOnlyViewModel(ApplicationTeamResource team, ApplicationReadOnlyData data, QuestionResource question) {
        super(data, question);
        this.team = team;
    }

    public ApplicationTeamResource getTeam() {
        return team;
    }

    @Override
    public String getFragment() {
        return "application-team";
    }

}
