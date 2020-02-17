package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class RoleProfileStatusBuilder extends BaseBuilder<RoleProfileStatus, RoleProfileStatusBuilder> {

    private RoleProfileStatusBuilder(List<BiConsumer<Integer, RoleProfileStatus>> multiActions) {
        super(multiActions);
    }

    public static RoleProfileStatusBuilder newRoleProfileStatus() {
        return new RoleProfileStatusBuilder(emptyList()).
                with(uniqueIds());
    }

    @Override
    protected RoleProfileStatusBuilder createNewBuilderWithActions(List<BiConsumer<Integer, RoleProfileStatus>> actions) {
        return new RoleProfileStatusBuilder(actions);
    }

    public RoleProfileStatusBuilder withUser(User... users) {
        return withArray((user, roleProfileStatusResource) -> setField("user", user, roleProfileStatusResource), users);
    }

    public RoleProfileStatusBuilder withProfileRole(ProfileRole... profileRoles) {
        return withArray((profileRole, roleProfileStatusResource) -> setField("profileRole", profileRole, roleProfileStatusResource), profileRoles);
    }

    public RoleProfileStatusBuilder withRoleProfileState(RoleProfileState... roleProfileStates) {
        return withArray((roleProfileState, roleProfileStatusResource) -> setField("roleProfileState", roleProfileState, roleProfileStatusResource), roleProfileStates);
    }

    public RoleProfileStatusBuilder withDescription(String... descriptions) {
        return withArray((description, roleProfileStatusResource) -> setField("description", description, roleProfileStatusResource), descriptions);
    }

    public RoleProfileStatusBuilder withCreatedBy(User... users) {
        return withArray((user, roleProfileStatusResource) -> setField("createdBy", user, roleProfileStatusResource), users);
    }

    public RoleProfileStatusBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArray((createdOn, roleProfileStatusResource) -> setField("createdOn", createdOn, roleProfileStatusResource), createdOns);
    }

    @Override
    protected RoleProfileStatus createInitial() {
        return new RoleProfileStatus();
    }

}
