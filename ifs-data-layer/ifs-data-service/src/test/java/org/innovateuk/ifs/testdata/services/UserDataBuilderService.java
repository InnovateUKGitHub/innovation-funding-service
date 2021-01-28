package org.innovateuk.ifs.testdata.services;

import org.assertj.core.util.Lists;
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

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.testdata.builders.ExternalUserDataBuilder.newExternalUserData;
import static org.innovateuk.ifs.testdata.builders.InternalUserDataBuilder.newInternalUserData;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;

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
        testService.doWithinTransaction(() -> {

            externalUserBuilder.withRole(line.role);
            createUser(externalUserBuilder, line, line.role, line.organisationName, line.additionalRoles);
        });
    }

    public void createInternalUser(CsvUtils.InternalUserLine line) {

        testService.doWithinTransaction(() -> {

            setDefaultSystemRegistrar();

            Role role = Role.getByName(line.role);

            InternalUserDataBuilder baseBuilder = internalUserBuilder.withRole(role);

            createUser(baseBuilder, line, role, null, Lists.emptyList());
        });
    }

    private <T extends BaseUserData, S extends BaseUserDataBuilder<T, S>> void createUser(S baseBuilder, CsvUtils.UserLine line, Role role, String organisation, List<Role> additionalRoles) {

        UnaryOperator<S> registerUserIfNecessary = builder -> builder.registerUser(line.firstName, line.lastName, line.emailAddress, line.phoneNumber, role, organisation);

        UnaryOperator<S> verifyEmail = UnaryOperator.identity();
        if (!newArrayList(KNOWLEDGE_TRANSFER_ADVISER, SUPPORTER, MONITORING_OFFICER, STAKEHOLDER).contains(role)) {
            verifyEmail = BaseUserDataBuilder::verifyEmail;
        }

        UnaryOperator<S> activateUser = UnaryOperator.identity();
        if (newArrayList(MONITORING_OFFICER, STAKEHOLDER).contains(role)){
            activateUser = BaseUserDataBuilder::activateUser;
        }

        UnaryOperator<S> addRoles = builder -> builder.addAdditionalRoles(additionalRoles);

        UnaryOperator<S> inactivateUserIfNecessary = builder -> !(line.emailVerified) ? builder.deactivateUser() : builder;

        registerUserIfNecessary.andThen(verifyEmail).andThen(activateUser).andThen(addRoles).andThen(inactivateUserIfNecessary).apply(baseBuilder).build();
    }

    private void setDefaultSystemRegistrar() {
        setLoggedInUser(newUserResource().withRoleGlobal(Role.SYSTEM_REGISTRATION_USER).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(BaseDataBuilder.IFS_SYSTEM_REGISTRAR_USER_EMAIL).getSuccess())
        );
    }


}
