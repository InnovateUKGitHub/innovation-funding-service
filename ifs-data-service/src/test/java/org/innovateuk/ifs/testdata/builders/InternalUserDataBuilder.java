package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.testdata.builders.data.InternalUserData;
import org.innovateuk.ifs.user.domain.CompAdminEmail;
import org.innovateuk.ifs.user.domain.ProjectFinanceEmail;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generates internal users (Comp Admins, Project Finance, Comp Execs and Comp Technologists)
 */
public class InternalUserDataBuilder extends BaseUserDataBuilder<InternalUserData, InternalUserDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(InternalUserDataBuilder.class);

    @Override
    public InternalUserDataBuilder registerUser(String firstName, String lastName, String emailAddress, String organisationName, String phoneNumber) {
        return with(data -> {

            doAs(systemRegistrar(), () ->
                    registerUser(firstName, lastName, data.getEmailAddress(), organisationName, phoneNumber, data.getRoles(), data));
        });
    }

    @Override
    public InternalUserDataBuilder createUserDirectly(String firstName, String lastName, String emailAddress, String organisationName, String phoneNumber, boolean emailVerified){
        return with(data -> {

            User user = userRepository.save(new User(firstName, lastName, emailAddress, null, UUID.randomUUID().toString()));
            List<String> roleNames = simpleMap(data.getRoles(), userRoleType -> userRoleType.getName());
            List<Role> roles = roleRepository.findByNameIn(roleNames);
            user.getRoles().addAll(roles);
            user.setStatus(emailVerified ? UserStatus.ACTIVE : UserStatus.INACTIVE);
            userRepository.save(user);
        });
    }

    public static InternalUserDataBuilder newInternalUserData(ServiceLocator serviceLocator) {

        return new InternalUserDataBuilder(emptyList(), serviceLocator);
    }

    private InternalUserDataBuilder(List<BiConsumer<Integer, InternalUserData>> multiActions,
                                    ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected InternalUserDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InternalUserData>> actions) {
        return new InternalUserDataBuilder(actions, serviceLocator);
    }

    @Override
    protected InternalUserData createInitial() {
        return new InternalUserData();
    }

    public InternalUserDataBuilder withRoles(List<UserRoleType> roles) {
        return with(data -> {
           data.setRoles(roles);
        });
    }

    public InternalUserDataBuilder createPreRegistrationEntry(String emailAddress) {
        return with(data -> {
            if (data.getRoles().contains(COMP_ADMIN)) {
                CompAdminEmail preregistrationEntry = new CompAdminEmail();
                preregistrationEntry.setEmail(emailAddress);
                compAdminEmailRepository.save(preregistrationEntry);
            }

            if (data.getRoles().contains(PROJECT_FINANCE)) {
                ProjectFinanceEmail preregistrationEntry = new ProjectFinanceEmail();
                preregistrationEntry.setEmail(emailAddress);
                projectFinanceEmailRepository.save(preregistrationEntry);
            }

            data.setEmailAddress(emailAddress);
        });
    }

    @Override
    protected void postProcess(int index, InternalUserData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Internal User '{}'", instance.getEmailAddress());
    }
}
