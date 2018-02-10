package org.innovateuk.ifs.testdata.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.BaseIntegrationTest.getLoggedInUser;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;

/**
 * TODO DW - document this class
 */
public abstract class BaseDataBuilderService {

    private static Cache<Long, List<QuestionResource>> questionsByCompetitionId = CacheBuilder.newBuilder().build();
    private static Cache<Long, List<FormInputResource>> formInputsByQuestionId = CacheBuilder.newBuilder().build();

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String PROJECT_FINANCE_EMAIL = "lee.bowman@innovateuk.test";


    @Autowired
    private UserService userService;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputService formInputService;

    UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccess());
    }

    OrganisationResource retrieveOrganisationByUserId(Long id) {
        return doAs(systemRegistrar(), () -> organisationService.getPrimaryForUser(id).getSuccess());
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

    /**
     * Set a user on the Spring Security ThreadLocals
     */
    public static UserResource setLoggedInUser(UserResource user) {
        UserResource currentUser = getLoggedInUser();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        return currentUser;
    }

    List<QuestionResource> retrieveCachedQuestionsByCompetitionId(Long competitionId) {
        return fromCache(competitionId, questionsByCompetitionId, () ->
                questionService.findByCompetition(competitionId).getSuccess());
    }

    List<FormInputResource> retrieveCachedFormInputsByQuestionId(QuestionResource question) {
        return fromCache(question.getId(), formInputsByQuestionId, () ->
                formInputService.findByQuestionId(question.getId()).getSuccess());
    }

    private <K, V> V fromCache(K key, Cache<K, V> cache, Callable<V> loadingFunction) {
        try {
            return cache.get(key, loadingFunction);
        } catch (ExecutionException e) {
            throw new RuntimeException("Exception encountered whilst reading from Cache", e);
        }
    }
}
