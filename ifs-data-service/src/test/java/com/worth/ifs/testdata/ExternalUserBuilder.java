package com.worth.ifs.testdata;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static java.util.Collections.emptyList;

public class ExternalUserBuilder extends BaseUserDataBuilder<ExternalUserData, ExternalUserBuilder> {


    public ExternalUserBuilder registerUser(String firstName, String lastName, String emailAddress, String organisationName) {
        return with(data -> {
            registerUserWithNewOrganisation(firstName, lastName, emailAddress, organisationName, APPLICANT, data);
        });
    }

    public static ExternalUserBuilder newExternalUserData(ServiceLocator serviceLocator) {

        return new ExternalUserBuilder(emptyList(), serviceLocator);
    }

    private ExternalUserBuilder(List<BiConsumer<Integer, ExternalUserData>> multiActions,
                                ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected ExternalUserBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExternalUserData>> actions) {
        return new ExternalUserBuilder(actions, serviceLocator);
    }

    @Override
    protected ExternalUserData createInitial() {
        return new ExternalUserData();
    }
}
