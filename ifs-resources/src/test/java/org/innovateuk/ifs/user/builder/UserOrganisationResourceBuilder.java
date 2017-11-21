package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.resource.UserStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class UserOrganisationResourceBuilder extends BaseBuilder<UserOrganisationResource, UserOrganisationResourceBuilder> {
    private UserOrganisationResourceBuilder(List<BiConsumer<Integer, UserOrganisationResource>> newActions) {
        super(newActions);
    }

    @Override
    protected UserOrganisationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UserOrganisationResource>> actions) {
        return new UserOrganisationResourceBuilder(actions);
    }

    @Override
    protected UserOrganisationResource createInitial() {
        return new UserOrganisationResource();
    }

    public static UserOrganisationResourceBuilder newUserOrganisationResource() {
        return new UserOrganisationResourceBuilder(emptyList());
    }

    public UserOrganisationResourceBuilder withName(String... names) {
        return withArray((name, userOrganisation) -> setField("name", name, userOrganisation), names);
    }

    public UserOrganisationResourceBuilder withEmail(String... emails) {
        return withArray((email, userOrganisation) -> setField("email", email, userOrganisation), emails);
    }

    public UserOrganisationResourceBuilder withStatus(UserStatus... statuses) {
        return withArray((status, userOrganisation) -> setField("status", status, userOrganisation), statuses);
    }

    public UserOrganisationResourceBuilder withOrganisationName(String... organisationNames) {
        return withArray((organisationName, userOrganisation) -> setField("organisationName", organisationName, userOrganisation), organisationNames);
    }
}
