package com.worth.ifs.testdata.builders;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.transactional.ApplicationFundingService;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.application.transactional.SectionService;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.CompetitionTypeRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import com.worth.ifs.competition.transactional.MilestoneService;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.form.transactional.FormInputService;
import com.worth.ifs.invite.repository.ApplicationInviteRepository;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.transactional.InviteService;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.transactional.TokenService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.*;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.RoleService;
import com.worth.ifs.user.transactional.UserService;
import com.worth.ifs.user.transactional.UsersRolesService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.worth.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

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
    protected ApplicationService applicationService;
    protected QuestionService questionService;
    protected FormInputService formInputService;
    protected FormInputResponseRepository formInputResponseRepository;
    protected ApplicationRepository applicationRepository;
    protected ApplicationFundingService applicationFundingService;
    protected ProjectService projectService;
    protected FinanceRowService financeRowService;
    protected SectionService sectionService;
    protected ProjectFinanceEmailRepository projectFinanceEmailRepository;
    protected UsersRolesService usersRolesService;
    protected ApplicationInviteRepository applicationInviteRepository;
    protected EthnicityRepository ethnicityRepository;
    protected RoleService roleService;
    protected CompetitionInviteRepository competitionInviteRepository;
    protected CompetitionRepository competitionRepository;
    protected AssessorService assessorService;

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
        this.applicationService = serviceLocator.getBean(ApplicationService.class);
        this.questionService = serviceLocator.getBean(QuestionService.class);
        this.formInputService = serviceLocator.getBean(FormInputService.class);
        this.formInputResponseRepository = serviceLocator.getBean(FormInputResponseRepository.class);
        this.applicationRepository = serviceLocator.getBean(ApplicationRepository.class);
        this.applicationFundingService = serviceLocator.getBean(ApplicationFundingService.class);
        this.projectService = serviceLocator.getBean(ProjectService.class);
        this.financeRowService = serviceLocator.getBean(FinanceRowService.class);
        this.sectionService = serviceLocator.getBean(SectionService.class);
        this.projectFinanceEmailRepository = serviceLocator.getBean(ProjectFinanceEmailRepository.class);
        this.usersRolesService = serviceLocator.getBean(UsersRolesService.class);
        this.applicationInviteRepository = serviceLocator.getBean(ApplicationInviteRepository.class);
        this.ethnicityRepository = serviceLocator.getBean(EthnicityRepository.class);
        this.roleService = serviceLocator.getBean(RoleService.class);
        this.competitionInviteRepository = serviceLocator.getBean(CompetitionInviteRepository.class);
        this.competitionRepository = serviceLocator.getBean(CompetitionRepository.class);
        this.assessorService = serviceLocator.getBean(AssessorService.class);
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    protected UserResource retrieveUserById(Long id) {
        return doAs(systemRegistrar(), () -> userService.getUserById(id).getSuccessObjectOrThrowException());
    }

    protected ProcessRoleResource retrieveApplicantByEmail(String emailAddress, Long applicationId) {
        return doAs(systemRegistrar(), () -> {
            UserResource user = retrieveUserByEmail(emailAddress);
            return usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).
                    getSuccessObjectOrThrowException();
        });
    }

    protected ProcessRoleResource retrieveLeadApplicant(Long applicationId) {
        return doAs(systemRegistrar(), () ->
                simpleFindFirst(usersRolesService.getProcessRolesByApplicationId(applicationId).
                    getSuccessObjectOrThrowException(), pr -> pr.getRoleName().equals(LEADAPPLICANT.getName())).get());
    }

    protected Organisation retrieveOrganisationByName(String organisationName) {
        return organisationRepository.findOneByName(organisationName);
    }

    protected Competition retrieveCompetitionByName(String competitionName) {
        return competitionRepository.findByName(competitionName).get(0);
    }

    protected Organisation retrieveOrganisationById(Long id) {
        return organisationRepository.findOne(id);
    }

    protected QuestionResource retrieveQuestionByCompetitionAndName(String questionName, Long competitionId) {
        return doAs(compAdmin(), () -> {
            List<QuestionResource> questions = questionService.findByCompetition(competitionId).getSuccessObjectOrThrowException();
            return simpleFindFirst(questions, q -> questionName.equals(q.getName())).get();
        });
    }

    protected QuestionResource retrieveQuestionByCompetitionSectionAndName(String questionName, String sectionName, CompetitionResource competition) {
        return doAs(compAdmin(), () -> {
            List<SectionResource> sections = sectionService.getByCompetitionId(competition.getId()).getSuccessObjectOrThrowException();
            SectionResource section = simpleFindFirst(sections, s -> sectionName.equals(s.getName())).get();

            List<QuestionResource> questions = questionService.findByCompetition(competition.getId()).getSuccessObjectOrThrowException();
            return simpleFindFirst(questions, q -> questionName.equals(q.getName()) && section.getId().equals(q.getSection())).get();
        });
    }

    protected OrganisationResource retrieveOrganisationResourceByName(String organisationName) {
        return doAs(systemRegistrar(), () -> {
            Organisation organisation = retrieveOrganisationByName(organisationName);
            return organisationService.findById(organisation.getId()).getSuccessObjectOrThrowException();
        });
    }

    protected UserResource systemRegistrar() {
        User user = userRepository.findByEmail("ifs_system_maintenance_user@innovateuk.org").get();
        return newUserResource().
                withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).
                withId(user.getId()).
                build();
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
