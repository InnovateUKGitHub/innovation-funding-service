package org.innovateuk.ifs.project.core.builder;

import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link ProjectUser} entities.
 */
public class ProjectUserBuilder extends ProjectParticipantBuilder<ProjectUser, ProjectUserBuilder> {

    private ProjectUserBuilder(List<BiConsumer<Integer, ProjectUser>> multiActions) {
        super(multiActions, ProjectParticipantRole.PROJECT_USER_ROLES);
    }

    public static ProjectUserBuilder newProjectUser() {
        return new ProjectUserBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectUserBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectUser>> actions) {
        return new ProjectUserBuilder(actions);
    }

    @Override
    protected ProjectUser createInitial() {
        return new ProjectUser();
    }

    public ProjectUserBuilder withRole(ProjectParticipantRole... roles) {

        return super.withRole(roles);
    }

    public ProjectUserBuilder withOrganisation(Organisation... organisations) {
        return super.withOrganisation(organisations);
    }

    public ProjectUserBuilder withInvite(ProjectUserInvite... projectUserInvites) {
        return withArraySetFieldByReflection("invite", projectUserInvites);
    }

    @Override
    public void postProcess(int index, ProjectUser projectUser) {
        Project project = projectUser.getProcess();

        if (project != null) {

            if (project.getProjectUsers() == null) {
                project.setProjectUsers(new ArrayList<>());
            }
            if (!project.getProjectUsers().contains(projectUser)) {
                project.addProjectUser(projectUser);
            }
        }
    }
}