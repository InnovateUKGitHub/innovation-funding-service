package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.util.List;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {
    private final List<ApplicationTeamOrganisationReadOnlyViewModel> organisations;

    public ApplicationTeamReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question, List<ApplicationTeamOrganisationReadOnlyViewModel> organisations) {
        super(data, question);
        this.organisations = organisations;
    }

    public List<ApplicationTeamOrganisationReadOnlyViewModel> getOrganisations() {
        return organisations;
    }

    @Override
    public String getFragment() {
        return "application-team";
    }

}
