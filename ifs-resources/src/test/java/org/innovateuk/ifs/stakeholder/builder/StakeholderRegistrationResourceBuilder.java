package org.innovateuk.ifs.stakeholder.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.resource.Role;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class StakeholderRegistrationResourceBuilder extends BaseBuilder<StakeholderRegistrationResource, StakeholderRegistrationResourceBuilder> {

    private StakeholderRegistrationResourceBuilder(final List<BiConsumer<Integer, StakeholderRegistrationResource>> newActions) {
        super(newActions);
    }

    @Override
    protected StakeholderRegistrationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, StakeholderRegistrationResource>> actions) {
        return new StakeholderRegistrationResourceBuilder(actions);
    }

    @Override
    protected StakeholderRegistrationResource createInitial() { return new StakeholderRegistrationResource(); }

    public static StakeholderRegistrationResourceBuilder newStakeholderRegistrationResource() {
        return new StakeholderRegistrationResourceBuilder(emptyList());
    }

    public StakeholderRegistrationResourceBuilder withFirstName(String... firstNames) {
        return withArray((firstName, stakeholderRegistrationResource) -> setField("firstName", firstName, stakeholderRegistrationResource), firstNames);
    }

    public StakeholderRegistrationResourceBuilder withLastName(String... lastNames) {
        return withArray((lastName, stakeholderRegistrationResource) -> setField("lastName", lastName, stakeholderRegistrationResource), lastNames);
    }

    public StakeholderRegistrationResourceBuilder withEmail(String... emails) {
        return withArray((email, stakeholderRegistrationResource) -> setField("email", email, stakeholderRegistrationResource), emails);
    }

    public StakeholderRegistrationResourceBuilder withPassword(String... passwords) {
        return withArray((password, stakeholderRegistrationResource) -> setField("password", password, stakeholderRegistrationResource), passwords);
    }

    public StakeholderRegistrationResourceBuilder withRoles(List<Role>... roles) {
        return withArray((roleList, stakeholderRegistrationResource) -> setField("roles", roleList, stakeholderRegistrationResource), roles);
    }
}
