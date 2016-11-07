package com.worth.ifs.testdata.builders;

import com.worth.ifs.testdata.builders.data.ExternalUserData;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ExternalUserDataBuilder extends BaseUserDataBuilder<ExternalUserData, ExternalUserDataBuilder> {

    public ExternalUserDataBuilder registerUser(String firstName, String lastName, String emailAddress, String organisationName) {
        return with(data -> registerUser(firstName, lastName, emailAddress, organisationName, UserRoleType.APPLICANT, data));
    }

    public static ExternalUserDataBuilder newExternalUserData(ServiceLocator serviceLocator) {

        return new ExternalUserDataBuilder(emptyList(), serviceLocator);
    }

    private ExternalUserDataBuilder(List<BiConsumer<Integer, ExternalUserData>> multiActions,
                                    ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected ExternalUserDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExternalUserData>> actions) {
        return new ExternalUserDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ExternalUserData createInitial() {
        return new ExternalUserData();
    }
}
