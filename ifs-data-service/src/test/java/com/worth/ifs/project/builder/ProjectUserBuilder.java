package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link ProjectUser} entities.
 */
public class ProjectUserBuilder extends BaseBuilder<ProjectUser, ProjectUserBuilder> {

    private ProjectUserBuilder(List<BiConsumer<Integer, ProjectUser>> multiActions) {
        super(multiActions);
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

    public ProjectUserBuilder withId(Long... ids) {
        return withArray((id, projectUser) -> projectUser.setId(id), ids);
    }

    public ProjectUserBuilder withRole(ProjectParticipantRole... roles) {
        return withArray((role, projectUser) -> setField("role", role, projectUser), roles);
    }

    public ProjectUserBuilder withProject(Project... project) {
        return withArray((proj, projectUser) -> setField("project", proj, projectUser), project);
    }

    public ProjectUserBuilder       withOrganisation(Organisation... organisations) {
        return withArray((organisation, projectUser) -> setField("organisation", organisation, projectUser), organisations);
    }

    public ProjectUserBuilder  withInvite(ProjectInvite... projectInvites) {
        return withArray((projectInvite, projectUser) -> setField("invite", projectInvite, projectUser), projectInvites);
    }

    public ProjectUserBuilder withUser(User... users) {
        return withArray(BuilderAmendFunctions::setUser, users);
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
