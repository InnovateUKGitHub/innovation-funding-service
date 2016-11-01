package com.worth.ifs.testdata;

import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;

public class InternalUserBuilder extends BaseDataBuilder<InternalUserData, InternalUserBuilder> {

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

    public InternalUserBuilder registerUser(String firstName, String lastName) {

        return with(data -> {

            doAs(systemRegistrar(), () -> {

                UserResource created = createInternalUserViaRegistration(firstName, lastName, data.getEmailAddress(), data.getRole(),
                        organisationRepository.findOneByName(INNOVATE_UK_ORG_NAME).getId());

                updateUserInInternalUserData(data, created.getId());
            });
        });
    }

    public InternalUserBuilder verifyEmail() {
        return with(data -> {

            doAs(systemRegistrar(), () -> {
                UserResource user = data.getUser();

                Optional<Token> verifyToken = tokenRepository.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), user.getId());

                verifyToken.map(token -> registrationService.activateUser(token.getClassPk()).andOnSuccessReturnVoid(v -> {
                    tokenService.handleExtraAttributes(token);
                    tokenService.removeToken(token);
                })).orElseThrow(() -> new RuntimeException("No Invite Token exists for user " + user.getId()));
            });
        });
    }

    private void updateUserInInternalUserData(InternalUserData data, Long userId) {
        UserResource user = userService.getUserById(userId).getSuccessObjectOrThrowException();
        data.setUser(user);
    }

    private UserResource createInternalUserViaRegistration(String firstName, String lastName, String emailAddress,
                                                           UserRoleType role, Long organisationId) {

        List<Role> roles = roleRepository.findByNameIn(singletonList(role.getName()));

        UserResource created = registrationService.createOrganisationUser(organisationId, newUserResource().
                withFirstName(firstName).
                withLastName(lastName).
                withEmail(emailAddress).
                withRolesGlobal(simpleMap(roles, r -> newRoleResource().withId(r.getId()).build())).
                withPassword("Passw0rd").
                build()
            ).getSuccessObjectOrThrowException();

        registrationService.sendUserVerificationEmail(created, empty());

        return created;
    }
}
