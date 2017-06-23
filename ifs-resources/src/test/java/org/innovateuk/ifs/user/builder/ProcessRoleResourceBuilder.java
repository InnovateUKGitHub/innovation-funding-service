package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link ProcessRoleResource} entities.
 */
public class ProcessRoleResourceBuilder extends BaseBuilder<ProcessRoleResource, ProcessRoleResourceBuilder> {

    private ProcessRoleResourceBuilder(List<BiConsumer<Integer, ProcessRoleResource>> amendActions) {
        super(amendActions);
    }

    public static ProcessRoleResourceBuilder newProcessRoleResource() {
        return new ProcessRoleResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProcessRoleResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcessRoleResource>> actions) {
        return new ProcessRoleResourceBuilder(actions);
    }

    @Override
    protected ProcessRoleResource createInitial() {
        return new ProcessRoleResource();
    }

    public ProcessRoleResourceBuilder withApplication(final Long... applications) {
        return withArray((application, processRoleResource) -> setField("applicationId", application, processRoleResource), applications);
    }

    public ProcessRoleResourceBuilder withUser(UserResource... users) {
        return withArray((user, processRoleResource) -> processRoleResource.setUser(user.getId()), users);
    }

    public ProcessRoleResourceBuilder withUserId(Long...userIds) {
        return withArray((userId, processRoleResource) -> processRoleResource.setUser(userId), userIds);
    }

    public ProcessRoleResourceBuilder withUserName(String... userName) {
        return withArray((name, processRoleResource) -> processRoleResource.setUserName(name), userName);
    }

    public ProcessRoleResourceBuilder withId(Long... ids) {
        return withArray((id, processRoleResource) -> processRoleResource.setId(id), ids);
    }

    public ProcessRoleResourceBuilder withRole(RoleResource... roles) {
        return withArray((role, processRoleResource) -> {
            setField("role", role.getId(), processRoleResource);
            setField("roleName", role.getName(), processRoleResource);
        }, roles);
    }

    public ProcessRoleResourceBuilder withRoleName(String... roleNames) {
        return withArray((roleName, processRoleResource) -> processRoleResource.setRoleName(roleName), roleNames);
    }

    public ProcessRoleResourceBuilder withOrganisation(Long... organisations) {
        return withArray((organisation, processRoleResource) -> setField("organisationId", organisation, processRoleResource), organisations);
    }
}
