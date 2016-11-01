package com.worth.ifs.testdata;

import com.worth.ifs.invite.transactional.InviteService;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.token.transactional.TokenService;
import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.CompAdminEmailRepository;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserService;

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

    private UserRepository userRepository;
    private UserService userService;
    private RegistrationService registrationService;
    private RoleRepository roleRepository;
    private OrganisationRepository organisationRepository;
    private TokenRepository tokenRepository;
    private TokenService tokenService;
    private InviteService inviteService;
    private CompAdminEmailRepository compAdminEmailRepository;

    public static InternalUserBuilder newInternalUserData(
            UserRepository userRepository,
            UserService userService,
            RegistrationService registrationService,
            RoleRepository roleRepository,
            OrganisationRepository organisationRepository,
            TokenRepository tokenRepository,
            TokenService tokenService,
            InviteService inviteService,
            CompAdminEmailRepository compAdminEmailRepository) {

        return new InternalUserBuilder(emptyList(), userRepository, userService, registrationService, roleRepository, organisationRepository, tokenRepository, tokenService, inviteService, compAdminEmailRepository);
    }

    private InternalUserBuilder(List<BiConsumer<Integer, InternalUserData>> multiActions,
                                UserRepository userRepository,
                                UserService userService,
                                RegistrationService registrationService,
                                RoleRepository roleRepository,
                                OrganisationRepository organisationRepository,
                                TokenRepository tokenRepository,
                                TokenService tokenService,
                                InviteService inviteService,
                                CompAdminEmailRepository compAdminEmailRepository) {
        super(multiActions, userService);
        this.userRepository = userRepository;
        this.userService = userService;
        this.registrationService = registrationService;
        this.roleRepository = roleRepository;
        this.organisationRepository = organisationRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
        this.inviteService = inviteService;
        this.compAdminEmailRepository = compAdminEmailRepository;
    }

    @Override
    protected InternalUserBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InternalUserData>> actions) {
        return new InternalUserBuilder(actions, userRepository, userService, registrationService, roleRepository,
                organisationRepository, tokenRepository, tokenService, inviteService, compAdminEmailRepository);
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
