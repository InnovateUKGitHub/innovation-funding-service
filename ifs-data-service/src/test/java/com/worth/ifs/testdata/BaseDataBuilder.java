package com.worth.ifs.testdata;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.UserService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.worth.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;

/**
 * TODO DW - document this class
 */
public abstract class BaseDataBuilder<T, S> extends BaseBuilder<T, S> {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String INNOVATE_UK_ORG_NAME = "Innovate UK";

    protected UserService userService;

    public BaseDataBuilder(List<BiConsumer<Integer, T>> newActions, UserService userService) {
        super(newActions);
        this.userService = userService;
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    protected UserResource systemRegistrar() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build();
    }

    protected <T> T doAs(UserResource user, Supplier<T> action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    protected void doAs(UserResource user, Runnable action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

}
