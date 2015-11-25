package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.setUser;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Created by dwatson on 08/10/15.
 */
public class ProcessRoleBuilder extends BaseBuilder<ProcessRole, ProcessRoleBuilder> {

    private ProcessRoleBuilder(List<BiConsumer<Integer, ProcessRole>> multiActions) {
        super(multiActions);
    }

    public static ProcessRoleBuilder newProcessRole() {
        return new ProcessRoleBuilder(emptyList()).with(uniqueIds());
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
        return withArray((id, processRole) -> processRole.setId(id), ids);
    }

    public ProcessRoleBuilder withRole(Builder<Role, ?> role) {
        return with(processRole -> processRole.setRole(role.build()));
    }

    public ProcessRoleBuilder withRole(Role... roles) {
        return withArray((role, processRole) -> processRole.setRole(role), roles);
    }

    public ProcessRoleBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public ProcessRoleBuilder withApplication(Application... applications) {
        return withArray((application, processRole) -> processRole.setApplication(application), applications);
    }

    public ProcessRoleBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, processRole) -> setField("organisation", organisation, processRole), organisations);
    }

    public ProcessRoleBuilder withUser(User... users) {
        return withArray((user, processRole) -> setUser(user, processRole), users);
    }

    @Override
    public List<ProcessRole> build(int numberToBuild) {
        List<ProcessRole> built = super.build(numberToBuild);

        // now add back-refs where appropriate
        built.forEach(processRole -> {
            User user = processRole.getUser();
            if (user != null && !user.getProcessRoles().contains(processRole)) {
                user.addUserApplicationRole(processRole);
            }
        });


        return built;
    }
}
