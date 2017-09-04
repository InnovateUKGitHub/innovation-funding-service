package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.testdata.builders.data.ExternalUserData;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Generates applicant users via registration
 */
public class ExternalUserDataBuilder extends BaseUserDataBuilder<ExternalUserData, ExternalUserDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalUserDataBuilder.class);

    public ExternalUserDataBuilder registerUser(String firstName, String lastName, String emailAddress, String organisationName, String phoneNumber) {
        return with(data -> registerUser(firstName, lastName, emailAddress, organisationName, phoneNumber, singletonList(UserRoleType.APPLICANT), data));
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

    @Override
    protected void postProcess(int index, ExternalUserData instance) {
        super.postProcess(index, instance);
        LOG.info("Created External User '{}'", instance.getUser().getEmail());
    }
}
