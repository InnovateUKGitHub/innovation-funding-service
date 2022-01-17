package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class RoleProfileStatusResourceBuilder extends BaseBuilder<RoleProfileStatusResource, RoleProfileStatusResourceBuilder> {

    private RoleProfileStatusResourceBuilder(List<BiConsumer<Integer, RoleProfileStatusResource>> multiActions) {
        super(multiActions);
    }

    public static RoleProfileStatusResourceBuilder newRoleProfileStatusResource() {
        return new RoleProfileStatusResourceBuilder(emptyList());
    }

    @Override
    protected RoleProfileStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, RoleProfileStatusResource>> actions) {
        return new RoleProfileStatusResourceBuilder(actions);
    }

    public RoleProfileStatusResourceBuilder withUserId(Long... userIds) {
        return withArray((userId, roleProfileStatusResource) -> setField("userId", userId, roleProfileStatusResource), userIds);
    }

    public RoleProfileStatusResourceBuilder withProfileRole(ProfileRole... profileRoles) {
        return withArray((profileRole, roleProfileStatusResource) -> setField("profileRole", profileRole, roleProfileStatusResource), profileRoles);
    }

    public RoleProfileStatusResourceBuilder withRoleProfileState(RoleProfileState... roleProfileStates) {
        return withArray((roleProfileState, roleProfileStatusResource) -> setField("roleProfileState", roleProfileState, roleProfileStatusResource), roleProfileStates);
    }

    public RoleProfileStatusResourceBuilder withDescription(String... descriptions) {
        return withArray((description, roleProfileStatusResource) -> setField("description", description, roleProfileStatusResource), descriptions);
    }

    public RoleProfileStatusResourceBuilder withModifiedBy(Long... ids) {
        return withArray((modifiedBy, roleProfileStatusResource) -> setField("modifiedBy", modifiedBy, roleProfileStatusResource), ids);
    }

    public RoleProfileStatusResourceBuilder withCreatedBy(Long... ids) {
        return withArray((createdBy, roleProfileStatusResource) -> setField("createdBy", createdBy, roleProfileStatusResource), ids);
    }

    public RoleProfileStatusResourceBuilder withCreatedOn(ZonedDateTime... dates) {
        return withArray((createdOn, roleProfileStatusResource) -> setField("createdOn", createdOn, roleProfileStatusResource), dates);
    }

    public RoleProfileStatusResourceBuilder withModifiedOn(ZonedDateTime... dates) {
        return withArray((modifiedOn, roleProfileStatusResource) -> setField("modifiedOn", modifiedOn, roleProfileStatusResource), dates);
    }

    @Override
    protected RoleProfileStatusResource createInitial() {
        return new RoleProfileStatusResource();
    }
}

