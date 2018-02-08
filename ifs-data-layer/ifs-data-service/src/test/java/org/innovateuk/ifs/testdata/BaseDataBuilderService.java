package org.innovateuk.ifs.testdata;

import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.testdata.builders.TestService;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.BaseIntegrationTest.getLoggedInUser;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;

/**
 * TODO DW - document this class
 */
abstract class BaseDataBuilderService {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String PROJECT_FINANCE_EMAIL = "lee.bowman@innovateuk.test";

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private NotificationSender emailNotificationSender;

    @Autowired
    private BankDetailsService bankDetailsService;

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private TestService testService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    protected OrganisationResource retrieveOrganisationById(Long id) {
        return doAs(systemRegistrar(), () -> organisationService.findById(id).getSuccessObjectOrThrowException());
    }

    protected OrganisationResource retrieveOrganisationByUserId(Long id) {
        return doAs(systemRegistrar(), () -> organisationService.getPrimaryForUser(id).getSuccessObjectOrThrowException());
    }

    protected UserResource systemRegistrar() {
        return newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build();
    }

    private <T> T doAs(UserResource user, Supplier<T> action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            return action.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    private void doAs(UserResource user, Runnable action) {
        UserResource currentUser = setLoggedInUser(user);
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            setLoggedInUser(currentUser);
        }
    }

    /**
     * Set a user on the Spring Security ThreadLocals
     *
     * @param user
     */
    public static UserResource setLoggedInUser(UserResource user) {
        UserResource currentUser = getLoggedInUser();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        return currentUser;
    }
}
