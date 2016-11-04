package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link ProjectUserResource} entities.
 */
public class ProjectUserResourceBuilder extends BaseBuilder<ProjectUserResource, ProjectUserResourceBuilder> {

    private ProjectUserResourceBuilder(List<BiConsumer<Integer, ProjectUserResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectUserResourceBuilder newProjectUserResource() {
        return new ProjectUserResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectUserResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectUserResource>> actions) {
        return new ProjectUserResourceBuilder(actions);
    }

    @Override
    protected ProjectUserResource createInitial() {
        return new ProjectUserResource();
    }

    public ProjectUserResourceBuilder withId(Long... ids) {
        return withArray((id, projectUser) -> projectUser.setId(id), ids);
    }

    public ProjectUserResourceBuilder withRole(Long... role) {
        return withArray((roleId, projectUser) -> projectUser.setRole(roleId), role);
    }

    public ProjectUserResourceBuilder withRoleName(String... roleName) {
        return withArray((name, projectUser) -> projectUser.setRoleName(name), roleName);
    }

    public ProjectUserResourceBuilder withRoleName(UserRoleType... roleName) {
        return withArray((name, projectUser) -> projectUser.setRoleName(name.getName()), roleName);
    }

    public ProjectUserResourceBuilder withProject(Long... project) {
        return withArray((projectId, projectUser) -> projectUser.setProject(projectId), project);
    }

    public ProjectUserResourceBuilder withOrganisation(Long... organisation) {
        return withArray((organisationId, projectUser) -> projectUser.setOrganisation(organisationId), organisation);
    }

    public ProjectUserResourceBuilder withUser(Long... user) {
        return withArray((userId, projectUser) -> projectUser.setUser(userId), user);
    }

    public ProjectUserResourceBuilder withUserName(String... username) {
        return withArray((name, projectUser) -> projectUser.setUserName(name), username);
    }

    public ProjectUserResourceBuilder withEmail(String... email) {
        return withArray((name, projectUser) -> projectUser.setEmail(name), email);
    }

    public ProjectUserResourceBuilder withPhoneNumber(String... phoneNumber) {
        return withArray((number, projectUser) -> projectUser.setPhoneNumber(number), phoneNumber);
    }
        public ProjectUserResourceBuilder withInvite(Long... projectInvites) {
        return withArray((invite, projectUser) -> projectUser.setInvite(invite), projectInvites);
    }
}
