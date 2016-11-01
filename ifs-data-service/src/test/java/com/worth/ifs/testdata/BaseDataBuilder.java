package com.worth.ifs.testdata;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.competition.repository.CompetitionTypeRepository;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import com.worth.ifs.competition.transactional.MilestoneService;
import com.worth.ifs.invite.transactional.InviteService;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.transactional.TokenService;
import com.worth.ifs.user.repository.CompAdminEmailRepository;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.worth.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;

/**
 * TODO DW - document this class
 */
public abstract class BaseDataBuilder<T, S> extends BaseBuilder<T, S> {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String INNOVATE_UK_ORG_NAME = "Innovate UK";

    protected ServiceLocator serviceLocator;
    protected UserService userService;
    protected CompetitionService competitionService;
    protected CompetitionTypeRepository competitionTypeRepository;
    protected CategoryRepository categoryRepository;
    protected CompetitionSetupService competitionSetupService;
    protected OrganisationService organisationService;
    protected UserRepository userRepository;
    protected RegistrationService registrationService;
    protected RoleRepository roleRepository;
    protected OrganisationRepository organisationRepository;
    protected TokenRepository tokenRepository;
    protected TokenService tokenService;
    protected InviteService inviteService;
    protected CompAdminEmailRepository compAdminEmailRepository;
    protected MilestoneService milestoneService;

    public BaseDataBuilder(List<BiConsumer<Integer, T>> newActions, ServiceLocator serviceLocator) {
        super(newActions);
        this.serviceLocator = serviceLocator;
        this.userService = serviceLocator.getBean(UserService.class);
        this.competitionService = serviceLocator.getBean(CompetitionService.class);
        this.competitionTypeRepository = serviceLocator.getBean(CompetitionTypeRepository.class);
        this.categoryRepository = serviceLocator.getBean(CategoryRepository.class);
        this.competitionSetupService = serviceLocator.getBean(CompetitionSetupService.class);
        this.organisationService = serviceLocator.getBean(OrganisationService.class);
        this.userRepository = serviceLocator.getBean(UserRepository.class);
        this.registrationService = serviceLocator.getBean(RegistrationService.class);
        this.roleRepository = serviceLocator.getBean(RoleRepository.class);
        this.organisationRepository = serviceLocator.getBean(OrganisationRepository.class);
        this.tokenRepository = serviceLocator.getBean(TokenRepository.class);
        this.tokenService = serviceLocator.getBean(TokenService.class);
        this.inviteService = serviceLocator.getBean(InviteService.class);
        this.compAdminEmailRepository = serviceLocator.getBean(CompAdminEmailRepository.class);
        this.milestoneService = serviceLocator.getBean(MilestoneService.class);
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    protected UserResource systemRegistrar() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build();
    }

    protected <T> T doAs(UserResource user, Supplier<T> action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    protected void doAs(UserResource user, Runnable action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

}
