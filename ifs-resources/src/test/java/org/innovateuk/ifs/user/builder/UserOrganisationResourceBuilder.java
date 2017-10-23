package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

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
}
