package com.worth.ifs.testdata;

import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static java.util.Collections.emptyList;

public class ExternalUserDataBuilder extends BaseUserDataBuilder<ExternalUserData, ExternalUserDataBuilder> {


    public ExternalUserDataBuilder registerUser(String firstName, String lastName, String emailAddress) {
        return with(data -> {
            data.setFirstName(firstName);
            data.setLastName(lastName);
            data.setEmailAddress(emailAddress);
        });
    }

    public ExternalUserDataBuilder withNewOrganisation(OrganisationDataBuilder organisationBuilder) {
        return with(data -> {
            OrganisationResource newOrganisation = organisationBuilder.build().getOrganisation();
            registerUserWithExistingOrganisation(data.getFirstName(), data.getLastName(), data.getEmailAddress(), newOrganisation.getName(), APPLICANT, data);
        });
    }

    public ExternalUserDataBuilder registerUserWithExistingOrganisation(String firstName, String lastName, String emailAddress, String organisationName) {
        return with(data -> {
            registerUserWithExistingOrganisation(firstName, lastName, emailAddress, organisationName, APPLICANT, data);
        });
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
