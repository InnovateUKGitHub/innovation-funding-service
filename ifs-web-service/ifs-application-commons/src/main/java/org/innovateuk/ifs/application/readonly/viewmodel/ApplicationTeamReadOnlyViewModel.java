package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.Optional;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {
    private final List<ApplicationTeamOrganisationReadOnlyViewModel> organisations;
    private final Optional<ProcessRoleResource> ktaProcessRole;
    private final boolean internal;
    private final boolean ktpCompetition;

    public ApplicationTeamReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question, List<ApplicationTeamOrganisationReadOnlyViewModel> organisations,
                                            Optional<ProcessRoleResource> ktaProcessRole, boolean internal) {
        super(data, question);
        this.organisations = organisations;
        this.ktpCompetition = data.getCompetition().isKtp();
        this.ktaProcessRole = ktaProcessRole;
        this.internal = internal;
    }

    public List<ApplicationTeamOrganisationReadOnlyViewModel> getOrganisations() {
        return organisations;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isCollaborativeProject() { return organisations.size() != 1; }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public Optional<ProcessRoleResource> getKtaProcessRole() {
        return ktaProcessRole;
    }

    @Override
    public String getFragment() {
        return "application-team";
    }

}
