package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Builder for Organisation entities.
 */
public class UserBuilder extends BaseBuilder<User, UserBuilder> {

    private UserBuilder(List<BiConsumer<Integer, User>> multiActions) {
        super(multiActions);
    }

    public static UserBuilder newUser() {
        return new UserBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("User "));
    }

    @Override
    protected UserBuilder createNewBuilderWithActions(List<BiConsumer<Integer, User>> actions) {
        return new UserBuilder(actions);
    }

    public UserBuilder withRolesGlobal(Role... globalRoles) {
        return with(user -> user.setRoles(asList(globalRoles)));
    }

    @Override
    protected User createInitial() {
        return new User();
    }
}
