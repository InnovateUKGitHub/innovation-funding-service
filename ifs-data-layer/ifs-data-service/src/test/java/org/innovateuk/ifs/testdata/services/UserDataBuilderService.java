package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.BaseUserData;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.testdata.builders.ExternalUserDataBuilder.newExternalUserData;
import static org.innovateuk.ifs.testdata.builders.InternalUserDataBuilder.newInternalUserData;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * A service that {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} uses to generate User data.  While
 * {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} is responsible for gathering CSV information and
 * orchestarting the building of it, this service is responsible for taking the CSV data passed to it and using
 * the appropriate builders to generate and update entities.
 */
@Component
@Lazy
public class UserDataBuilderService extends BaseDataBuilderService {

    @Autowired
    private TestService testService;

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    private ExternalUserDataBuilder externalUserBuilder;
    private InternalUserDataBuilder internalUserBuilder;

    @PostConstruct
    public void postConstruct() {

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);

        externalUserBuilder = newExternalUserData(serviceLocator);
        internalUserBuilder = newInternalUserData(serviceLocator);
    }

    public void createExternalUser(CsvUtils.ExternalUserLine line) {
        createUser(externalUserBuilder, line);
    }

    public void createInternalUser(CsvUtils.InternalUserLine line) {

        testService.doWithinTransaction(() -> {

            setDefaultSystemRegistrar();

            List<Role> roles = simpleMap(line.roles, Role::getByName);

            InternalUserDataBuilder baseBuilder = internalUserBuilder.withRoles(roles);

            createUser(baseBuilder, line);
        });
    }

    private <T extends BaseUserData, S extends BaseUserDataBuilder<T, S>> void createUser(S baseBuilder, CsvUtils.UserLine line) {

        UnaryOperator<S> registerUserIfNecessary = builder -> builder.registerUser(line.firstName, line.lastName, line.emailAddress, line.organisationName, line.phoneNumber);

        UnaryOperator<S> verifyEmail = BaseUserDataBuilder::verifyEmail;

        UnaryOperator<S> inactivateUserIfNecessary = builder -> !(line.emailVerified) ? builder.deactivateUser() : builder;

        registerUserIfNecessary.andThen(verifyEmail).andThen(inactivateUserIfNecessary).apply(baseBuilder).build();
    }

    private void setDefaultSystemRegistrar() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.SYSTEM_REGISTRATION_USER)).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(BaseDataBuilder.IFS_SYSTEM_REGISTRAR_USER_EMAIL).getSuccess())
        );
    }


}
