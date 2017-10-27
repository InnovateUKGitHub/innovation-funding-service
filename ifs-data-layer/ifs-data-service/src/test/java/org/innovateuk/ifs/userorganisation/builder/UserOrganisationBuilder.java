package org.innovateuk.ifs.userorganisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for UserOrganisation entities.
 */
public class UserOrganisationBuilder extends BaseBuilder<UserOrganisation, UserOrganisationBuilder> {

    private UserOrganisationBuilder(List<BiConsumer<Integer, UserOrganisation>> multiActions) {
        super(multiActions);
    }

    public static UserOrganisationBuilder newUserOrganisation() {
        return new UserOrganisationBuilder(emptyList());
    }

    @Override
    protected UserOrganisationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UserOrganisation>> actions) {
        return new UserOrganisationBuilder(actions);
    }

    public UserOrganisationBuilder withUser(User... users) {
        return withArraySetFieldByReflection("user", users);
    }

    public UserOrganisationBuilder withOrganisation(Organisation... organisations) {
        return withArraySetFieldByReflection("organisation", organisations);
    }

    @Override
    protected UserOrganisation createInitial() {
        return new UserOrganisation();
    }
}
