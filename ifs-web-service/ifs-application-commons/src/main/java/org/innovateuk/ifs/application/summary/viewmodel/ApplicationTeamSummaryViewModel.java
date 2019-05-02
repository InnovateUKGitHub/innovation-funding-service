package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.form.resource.QuestionResource;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamSummaryViewModel extends AbstractQuestionSummaryViewModel implements NewQuestionSummaryViewModel {
    private final ApplicationTeamResource team;


    public ApplicationTeamSummaryViewModel(ApplicationTeamResource team, ApplicationSummaryData data, QuestionResource question) {
        super(data, question);
        this.team = team;
    }

    public ApplicationTeamResource getTeam() {
        return team;
    }

    @Override
    public String getName() {
        return "Application team";
    }

    @Override
    public String getFragment() {
        return "application-team";
    }

}
