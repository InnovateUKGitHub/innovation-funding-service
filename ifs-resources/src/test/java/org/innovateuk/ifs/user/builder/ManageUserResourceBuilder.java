package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.ManageUserResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

public class ManageUserResourceBuilder extends BaseBuilder<ManageUserResource, ManageUserResourceBuilder> {

    private ManageUserResourceBuilder(List<BiConsumer<Integer, ManageUserResource>> multiActions) {
        super(multiActions);
    }

    public static ManageUserResourceBuilder newManageUserResource() {
        return new ManageUserResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ManageUserResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ManageUserResource>> actions) {
        return new ManageUserResourceBuilder(actions);
    }

    @Override
    protected ManageUserResource createInitial() {
        return new ManageUserResource();
    }

    @SafeVarargs
    public final ManageUserResourceBuilder withRolesGlobal(List<Role>... rolesList) {
        return withArraySetFieldByReflection("roles", rolesList);
    }

    @SafeVarargs
    public final ManageUserResourceBuilder withRoleProfileStatuses(Set<RoleProfileStatusResource>... roleProfileStatusResources) {
        return withArraySetFieldByReflection("roleProfileStatusResourceSet", roleProfileStatusResources);
    }

    public final ManageUserResourceBuilder withRoleGlobal(Role role) {
        return withRolesGlobal(singletonList(role));
    }

    public ManageUserResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ManageUserResourceBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public ManageUserResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public ManageUserResourceBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArraySetFieldByReflection("createdOn", createdOns);
    }

    public ManageUserResourceBuilder withCreatedBy(String... createdBys) {
        return withArraySetFieldByReflection("createdBy", createdBys);
    }
}