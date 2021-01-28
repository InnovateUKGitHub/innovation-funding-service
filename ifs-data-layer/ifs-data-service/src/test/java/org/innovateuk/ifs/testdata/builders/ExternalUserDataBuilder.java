package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.testdata.builders.data.ExternalUserData;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

/**
 * Generates applicant users via registration
 */
public class ExternalUserDataBuilder extends BaseUserDataBuilder<ExternalUserData, ExternalUserDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalUserDataBuilder.class);

    public ExternalUserDataBuilder registerUser(String firstName, String lastName, String emailAddress, String phoneNumber, Role role, String organisation) {
        return with(data -> {
            doAs(ifsAdmin(), () -> {
                String hash = null;
                if (Role.externalRolesToInvite().contains(role)) {
                    UserResource invite = new UserResource();
                    invite.setFirstName(firstName);
                    invite.setLastName(lastName);
                    invite.setEmail(emailAddress);
                    invite.setRoles(newArrayList(role));
                    inviteUserService.saveUserInvite(invite, role, organisation).getSuccess();
                    hash = inviteUserService.findPendingInternalUserInvites(invite.getEmail(), Pageable.unpaged()).getSuccess().getContent().get(0).getHash();
                }
                registerUser(firstName, lastName, emailAddress, phoneNumber, role, hash, data);
            });
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

    @Override
    protected void postProcess(int index, ExternalUserData instance) {
        super.postProcess(index, instance);
        LOG.info("Created External User '{}'", instance.getUser().getEmail());
    }

    public ExternalUserDataBuilder withRole(Role role) {
        return with(data -> data.setRole(role));
    }

    public ExternalUserDataBuilder withOrganisation(String organisation) {
        return with(data -> data.setOrganisation(organisation));
    }
}
