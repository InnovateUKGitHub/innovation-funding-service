package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;


// TODO missing test
public class ProjectUserInviteBuilder extends BaseInviteBuilder<Project, ProjectUserInvite, ProjectUserInviteBuilder> {

    private ProjectUserInviteBuilder(List<BiConsumer<Integer, ProjectUserInvite>> multiActions) {
        super(multiActions);
    }

    public static ProjectUserInviteBuilder newProjectUserInvite() {
        return new ProjectUserInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectUserInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectUserInvite>> actions) {
        return new ProjectUserInviteBuilder(actions);
    }

    public ProjectUserInviteBuilder withProject(Builder<Project, ?> project) {
        return withProject(project.build());
    }

    public ProjectUserInviteBuilder withProject(Project... projects) {
        return withTarget(projects);
    }

    public ProjectUserInviteBuilder withOrganisation(Builder<Organisation, ?> organisation) {
        return withOrganisation(organisation.build());
    }

    public ProjectUserInviteBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, invite) -> invite.setOrganisation(organisation), organisations);
    }

    @Override
    protected ProjectUserInvite createInitial() {
        return new ProjectUserInvite();
    }
}
