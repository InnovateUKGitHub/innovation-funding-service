package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

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

    public ProcessRoleResourceBuilder withApplicationId(final Long... ids) {
        return withArray((id, processRoleResource) -> setField("application", id, processRoleResource), ids);
    }

    public ProcessRoleResourceBuilder withUser(User... users) {
        return withArray(BuilderAmendFunctions::setUser, users);
    }

    public ProcessRoleResourceBuilder withRole(Role... roles) {
        return withArray((role, processRoleResource) -> {
            setField("role", role.getId(), processRoleResource);
            setField("roleName", role.getName(), processRoleResource);
        }, roles);
    }

    public ProcessRoleResourceBuilder withOrganisation(Long... organisations) {
        return withArray((organisation, processRoleResource) -> setField("organisation", organisation, processRoleResource), organisations);
    }
}
