package com.worth.ifs.testdata;

import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;

public class ExternalUserBuilder extends BaseDataBuilder<ExternalUserData, ExternalUserBuilder> {

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

    public ExternalUserBuilder registerUser(String firstName, String lastName, String emailAddress, String organisationName) {
        return with(data -> {
            doAs(systemRegistrar(), () -> {

                OrganisationResource newOrganisation =
                        organisationService.create(newOrganisationResource().
                                withId().
                                withName(organisationName).
                                build()).getSuccessObjectOrThrowException();

                UserResource registeredUser = createExternalUserViaRegistration(firstName, lastName, emailAddress, newOrganisation.getId());
                updateUserInExternalUserData(data, registeredUser.getId());
            });
        });
    }

    public ExternalUserBuilder verifyEmail() {
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

    private void updateUserInExternalUserData(ExternalUserData data, Long userId) {
        UserResource user = userService.getUserById(userId).getSuccessObjectOrThrowException();
        data.setUser(user);
    }

    private UserResource createExternalUserViaRegistration(String firstName, String lastName, String emailAddress, Long organisationId) {

        List<Role> roles = roleRepository.findByNameIn(singletonList(UserRoleType.APPLICANT.getName()));

        UserResource created = registrationService.createOrganisationUser(organisationId, newUserResource().
                withFirstName(firstName).
                withLastName(lastName).
                withEmail(emailAddress).
                withRolesGlobal(simpleMap(roles, r -> newRoleResource().withId(r.getId()).build())).
                withPassword("Passw0rd").
                build()).
                getSuccessObjectOrThrowException();

        registrationService.sendUserVerificationEmail(created, empty());

        return created;
    }
}
