package org.innovateuk.ifs.registration.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.resource.RoleResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 * Builder to support testing of internal user registration resource
 */
public class InternalUserRegistrationResourceBuilder extends BaseBuilder<InternalUserRegistrationResource, InternalUserRegistrationResourceBuilder> {

    private InternalUserRegistrationResourceBuilder(final List<BiConsumer<Integer, InternalUserRegistrationResource>> newActions) {
        super(newActions);
    }

    @Override
    protected InternalUserRegistrationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InternalUserRegistrationResource>> actions) {
        return new InternalUserRegistrationResourceBuilder(actions);
    }

    @Override
    protected InternalUserRegistrationResource createInitial() {
        return new InternalUserRegistrationResource();
    }

    public static InternalUserRegistrationResourceBuilder newInternalUserRegistrationResource() {
        return new InternalUserRegistrationResourceBuilder(emptyList());
    }

    public InternalUserRegistrationResourceBuilder withFirstName(String... firstNames) {
        return withArray((firstName, userRegistrationResource) -> setField("firstName", firstName, userRegistrationResource), firstNames);
    }

    public InternalUserRegistrationResourceBuilder withLastName(String... lastNames) {
        return withArray((lastName, userRegistrationResource) -> setField("lastName", lastName, userRegistrationResource), lastNames);
    }

    public InternalUserRegistrationResourceBuilder withEmail(String... emails) {
        return withArray((email, userRegistrationResource) -> setField("email", email, userRegistrationResource), emails);
    }

    public InternalUserRegistrationResourceBuilder withPassword(String... passwords) {
        return withArray((password, userRegistrationResource) -> setField("password", password, userRegistrationResource), passwords);
    }

    public InternalUserRegistrationResourceBuilder withRoles(List<RoleResource>... roles) {
        return withArray((roleList, userRegistrationResource) -> setField("roles", roleList, userRegistrationResource), roles);
    }
}
