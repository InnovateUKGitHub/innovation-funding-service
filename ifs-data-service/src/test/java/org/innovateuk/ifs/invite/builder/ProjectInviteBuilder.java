package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;


// TODO missing test
public class ProjectInviteBuilder extends BaseInviteBuilder<Project, ProjectInvite, ProjectInviteBuilder> {

    private ProjectInviteBuilder(List<BiConsumer<Integer, ProjectInvite>> multiActions) {
        super(multiActions);
    }

    public static ProjectInviteBuilder newProjectInvite() {
        return new ProjectInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectInvite>> actions) {
        return new ProjectInviteBuilder(actions);
    }

    public ProjectInviteBuilder withProject(Builder<Project, ?> project) {
        return withProject(project.build());
    }

    public ProjectInviteBuilder withProject(Project... projects) {
        return withTarget(projects);
    }

    public ProjectInviteBuilder withOrganisation(Builder<Organisation, ?> organisation) {
        return withOrganisation(organisation.build());
    }

    public ProjectInviteBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, invite) -> invite.setOrganisation(organisation), organisations);
    }

    @Override
    protected ProjectInvite createInitial() {
        return new ProjectInvite();
    }
}
