package com.worth.ifs.user.domain;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.application.domain.Application;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ProcessRoleBuilder extends BaseBuilder<ProcessRole, ProcessRoleBuilder> {

    private ProcessRoleBuilder() {
        super();
    }

    private ProcessRoleBuilder(List<BiConsumer<Integer, ProcessRole>> multiActions) {
        super(multiActions);
    }

    public static ProcessRoleBuilder newProcessRole() {
        return new ProcessRoleBuilder();
    }

    @Override
    protected ProcessRoleBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcessRole>> actions) {
        return new ProcessRoleBuilder(actions);
    }

    @Override
    protected ProcessRole createInitial() {
        return new ProcessRole();
    }

    public ProcessRoleBuilder withId(Long... ids) {
        return with((id, processRole) -> processRole.setId(id), ids);
    }

    public ProcessRoleBuilder withRole(Builder<Role, ?> role) {
        return with(processRole -> processRole.setRole(role.build()));
    }

    public ProcessRoleBuilder withRole(Role... roles) {
        return with((role, processRole) -> processRole.setRole(role), roles);
    }

    public ProcessRoleBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public ProcessRoleBuilder withApplication(Application... applications) {
        return with((application, processRole) -> processRole.setApplication(application), applications);
    }
}
