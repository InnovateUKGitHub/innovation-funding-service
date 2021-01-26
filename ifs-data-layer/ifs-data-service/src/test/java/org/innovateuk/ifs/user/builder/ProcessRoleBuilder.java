package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.BuilderAmendFunctions;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link ProcessRole} entities.
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

    public ProcessRoleBuilder withRole(Builder<ProcessRoleType, ?> role) {
        return with(processRole -> processRole.setRole(role.build()));
    }

    public ProcessRoleBuilder withRole(ProcessRoleType... roles) {
        return withArray((role, processRole) -> processRole.setRole(role), roles);
    }

    public ProcessRoleBuilder withApplication(Builder<Application, ?> application) {
        return withApplication(application.build());
    }

    public ProcessRoleBuilder withApplication(Application... applications) {
        return withArray((application, processRole) -> {
                setField("applicationId", application.getId(), processRole);
                application.addUserApplicationRole(processRole);
           }, applications);
    }
    public ProcessRoleBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, processRole) -> {
            setField("organisationId", organisation.getId(), processRole);
        }, organisations);
    }

    public ProcessRoleBuilder withOrganisationId(Long... organisationIds) {
        return withArraySetFieldByReflection("organisationId", organisationIds);
    }

    public ProcessRoleBuilder withUser(User... users) {
        return withArray(BuilderAmendFunctions::setUser, users);
    }
}