package com.worth.ifs.testdata;

import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InternalUserBuilder extends BaseUserDataBuilder<InternalUserData, InternalUserBuilder> {

    public InternalUserBuilder registerUser(String firstName, String lastName) {
        return with(data -> {

            doAs(systemRegistrar(), () -> {
                registerUserWithExistingOrganisation(firstName, lastName, data.getEmailAddress(), INNOVATE_UK_ORG_NAME, data.getRole(), data);
            });
        });
    }

    public static InternalUserBuilder newInternalUserData(ServiceLocator serviceLocator) {

        return new InternalUserBuilder(emptyList(), serviceLocator);
    }

    private InternalUserBuilder(List<BiConsumer<Integer, InternalUserData>> multiActions,
                                ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected InternalUserBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InternalUserData>> actions) {
        return new InternalUserBuilder(actions, serviceLocator);
    }

    @Override
    protected InternalUserData createInitial() {
        return new InternalUserData();
    }

    public InternalUserBuilder withRole(UserRoleType role) {
        return with(data -> {
           data.setRole(role);
        });
    }

    public InternalUserBuilder createPreRegistrationEntry(String emailAddress) {
        return with(data -> {
            switch (data.getRole()) {
                case COMP_ADMIN: {
                    CompAdminEmail preregistrationEntry = new CompAdminEmail();
                    preregistrationEntry.setEmail(emailAddress);
                    compAdminEmailRepository.save(preregistrationEntry);
                }
            }

            data.setEmailAddress(emailAddress);
        });
    }
}
