package org.innovateuk.ifs.testdata.builders;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.affiliation.transactional.AffiliationService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.IneligibleOutcomeMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.transactional.*;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.assessment.transactional.CompetitionInviteService;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionFunderRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.profile.transactional.ProfileService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.monitoringofficer.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.publiccontent.repository.ContentEventRepository;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.publiccontent.transactional.ContentGroupService;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.*;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.transactional.*;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Base Builder for persistent data builders.  Wraps each build step in a transaction.  Provides a location for
 * service lookup.
 */
public abstract class BaseDataBuilder<T, S> extends BaseBuilder<T, S> {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String IFS_SYSTEM_MAINTENANCE_USER_EMAIL = "ifs_system_maintenance_user@innovateuk.org";
    public static final String PROJECT_FINANCE_EMAIL = "lee.bowman@innovateuk.test";

    protected ServiceLocator serviceLocator;
    protected BaseUserService baseUserService;
    protected UserService userService;
    protected CompetitionService competitionService;
    protected CompetitionTypeRepository competitionTypeRepository;
    protected CategoryRepository categoryRepository;
    protected InnovationAreaRepository innovationAreaRepository;
    protected InnovationSectorRepository innovationSectorRepository;
    protected ResearchCategoryRepository researchCategoryRepository;
    protected CompetitionSetupService competitionSetupService;
    protected PublicContentService publicContentService;
    protected PublicContentRepository publicContentRepository;
    protected ContentGroupRepository contentGroupRepository;
    protected ContentGroupService contentGroupService;
    protected ContentEventRepository contentEventRepository;
    protected OrganisationService organisationService;
    protected OrganisationTypeService organisationTypeService;
    protected UserRepository userRepository;
    protected ProfileRepository profileRepository;
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
    protected ProjectDetailsService projectDetailsService;
    protected MonitoringOfficerService monitoringOfficerService;
    protected FinanceRowService financeRowService;
    protected SectionService sectionService;
    protected ProjectFinanceEmailRepository projectFinanceEmailRepository;
    protected UsersRolesService usersRolesService;
    protected ApplicationInviteRepository applicationInviteRepository;
    protected EthnicityRepository ethnicityRepository;
    protected RoleService roleService;
    protected CompetitionInviteRepository competitionInviteRepository;
    protected CompetitionRepository competitionRepository;
    protected CompetitionFunderRepository competitionFunderRepository;
    protected AssessorService assessorService;
    protected CompetitionParticipantRepository competitionParticipantRepository;
    protected CompetitionInviteService competitionInviteService;
    protected TestService testService;
    protected AssessmentRepository assessmentRepository;
    protected AssessmentService assessmentService;
    protected AssessmentWorkflowHandler assessmentWorkflowHandler;
    protected ProcessRoleRepository processRoleRepository;
    protected ActivityStateRepository activityStateRepository;
    protected SectionRepository sectionRepository;
    protected QuestionRepository questionRepository;
    protected FormInputRepository formInputRepository;
    protected FileEntryRepository fileEntryRepository;
    protected ApplicationFinanceRepository applicationFinanceRepository;
    protected ProjectUserRepository projectUserRepository;
    protected BankDetailsService bankDetailsService;
    protected SpendProfileService spendProfileService;
    protected FinanceCheckService financeCheckService;
    protected RejectionReasonService rejectionReasonService;
    protected ProfileService profileService;
    protected AffiliationService affiliationService;
    protected ApplicationInnovationAreaService applicationInnovationAreaService;
    protected AssessorFormInputResponseService assessorFormInputResponseService;
    protected IneligibleOutcomeMapper ineligibleOutcomeMapper;

    private static Cache<Long, List<QuestionResource>> questionsByCompetitionId = CacheBuilder.newBuilder().build();

    private static Cache<Long, List<FormInputResource>> formInputsByQuestionId = CacheBuilder.newBuilder().build();

    private static Cache<String, UserResource> usersByEmailAddress = CacheBuilder.newBuilder().build();

    private static Cache<String, UserResource> usersByEmailAddressInternal = CacheBuilder.newBuilder().build();

    private static Cache<Long, UserResource> usersById = CacheBuilder.newBuilder().build();

    private static Cache<Pair<Long, String>, ProcessRoleResource> applicantsByApplicationIdAndEmail = CacheBuilder.newBuilder().build();

    private static Cache<Long, ProcessRoleResource> leadApplicantsByApplicationId = CacheBuilder.newBuilder().build();

    private static Cache<String, OrganisationResource> organisationsByName = CacheBuilder.newBuilder().build();

    public BaseDataBuilder(List<BiConsumer<Integer, T>> newActions, ServiceLocator serviceLocator) {

        super(newActions);

        this.serviceLocator = serviceLocator;
        userService = serviceLocator.getBean(UserService.class);
        competitionService = serviceLocator.getBean(CompetitionService.class);
        competitionTypeRepository = serviceLocator.getBean(CompetitionTypeRepository.class);
        categoryRepository = serviceLocator.getBean(CategoryRepository.class);
        competitionSetupService = serviceLocator.getBean(CompetitionSetupService.class);
        organisationService = serviceLocator.getBean(OrganisationService.class);
        organisationTypeService = serviceLocator.getBean(OrganisationTypeService.class);
        userRepository = serviceLocator.getBean(UserRepository.class);
        registrationService = serviceLocator.getBean(RegistrationService.class);
        roleRepository = serviceLocator.getBean(RoleRepository.class);
        organisationRepository = serviceLocator.getBean(OrganisationRepository.class);
        tokenRepository = serviceLocator.getBean(TokenRepository.class);
        tokenService = serviceLocator.getBean(TokenService.class);
        inviteService = serviceLocator.getBean(InviteService.class);
        compAdminEmailRepository = serviceLocator.getBean(CompAdminEmailRepository.class);
        milestoneService = serviceLocator.getBean(MilestoneService.class);
        applicationService = serviceLocator.getBean(ApplicationService.class);
        questionService = serviceLocator.getBean(QuestionService.class);
        formInputService = serviceLocator.getBean(FormInputService.class);
        formInputResponseRepository = serviceLocator.getBean(FormInputResponseRepository.class);
        applicationRepository = serviceLocator.getBean(ApplicationRepository.class);
        applicationFundingService = serviceLocator.getBean(ApplicationFundingService.class);
        projectService = serviceLocator.getBean(ProjectService.class);
        projectDetailsService = serviceLocator.getBean(ProjectDetailsService.class);
        monitoringOfficerService = serviceLocator.getBean(MonitoringOfficerService.class);
        financeRowService = serviceLocator.getBean(FinanceRowService.class);
        sectionService = serviceLocator.getBean(SectionService.class);
        projectFinanceEmailRepository = serviceLocator.getBean(ProjectFinanceEmailRepository.class);
        usersRolesService = serviceLocator.getBean(UsersRolesService.class);
        applicationInviteRepository = serviceLocator.getBean(ApplicationInviteRepository.class);
        ethnicityRepository = serviceLocator.getBean(EthnicityRepository.class);
        roleService = serviceLocator.getBean(RoleService.class);
        competitionInviteRepository = serviceLocator.getBean(CompetitionInviteRepository.class);
        competitionRepository = serviceLocator.getBean(CompetitionRepository.class);
        assessorService = serviceLocator.getBean(AssessorService.class);
        competitionParticipantRepository = serviceLocator.getBean(CompetitionParticipantRepository.class);
        competitionInviteService = serviceLocator.getBean(CompetitionInviteService.class);
        testService = serviceLocator.getBean(TestService.class);
        assessmentRepository = serviceLocator.getBean(AssessmentRepository.class);
        assessmentService = serviceLocator.getBean(AssessmentService.class);
        assessmentWorkflowHandler = serviceLocator.getBean(AssessmentWorkflowHandler.class);
        processRoleRepository = serviceLocator.getBean(ProcessRoleRepository.class);
        activityStateRepository = serviceLocator.getBean(ActivityStateRepository.class);
        sectionRepository = serviceLocator.getBean(SectionRepository.class);
        questionRepository = serviceLocator.getBean(QuestionRepository.class);
        formInputRepository = serviceLocator.getBean(FormInputRepository.class);
        fileEntryRepository = serviceLocator.getBean(FileEntryRepository.class);
        applicationFinanceRepository = serviceLocator.getBean(ApplicationFinanceRepository.class);
        projectUserRepository = serviceLocator.getBean(ProjectUserRepository.class);
        bankDetailsService = serviceLocator.getBean(BankDetailsService.class);
        spendProfileService = serviceLocator.getBean(SpendProfileService.class);
        financeCheckService = serviceLocator.getBean(FinanceCheckService.class);
        competitionFunderRepository = serviceLocator.getBean(CompetitionFunderRepository.class);
        innovationAreaRepository = serviceLocator.getBean(InnovationAreaRepository.class);
        innovationSectorRepository = serviceLocator.getBean(InnovationSectorRepository.class);
        researchCategoryRepository = serviceLocator.getBean(ResearchCategoryRepository.class);
        rejectionReasonService = serviceLocator.getBean(RejectionReasonService.class);
        profileService = serviceLocator.getBean(ProfileService.class);
        affiliationService = serviceLocator.getBean(AffiliationService.class);
        baseUserService = serviceLocator.getBean(BaseUserService.class);
        profileRepository = serviceLocator.getBean(ProfileRepository.class);
        publicContentService = serviceLocator.getBean(PublicContentService.class);
        publicContentRepository = serviceLocator.getBean(PublicContentRepository.class);
        contentEventRepository = serviceLocator.getBean(ContentEventRepository.class);
        contentGroupRepository = serviceLocator.getBean(ContentGroupRepository.class);
        contentGroupService = serviceLocator.getBean(ContentGroupService.class);
        assessorFormInputResponseService = serviceLocator.getBean(AssessorFormInputResponseService.class);
        applicationInnovationAreaService = serviceLocator.getBean(ApplicationInnovationAreaService.class);
        ineligibleOutcomeMapper = serviceLocator.getBean(IneligibleOutcomeMapper.class);
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmailInternal(COMP_ADMIN_EMAIL, UserRoleType.COMP_ADMIN);
    }

    protected UserResource systemRegistrar() {
        return retrieveUserByEmailInternal(IFS_SYSTEM_MAINTENANCE_USER_EMAIL, SYSTEM_REGISTRATION_USER);
    }

    protected UserResource projectFinanceUser() {
        return retrieveUserByEmail(PROJECT_FINANCE_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return fromCache(emailAddress, usersByEmailAddress, () ->
                doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException()));
    }

    protected UserResource retrieveUserById(Long id) {
        return fromCache(id, usersById, () -> doAs(systemRegistrar(), () -> baseUserService.getUserById(id).getSuccessObjectOrThrowException()));
    }

    protected ProcessRoleResource retrieveApplicantByEmail(String emailAddress, Long applicationId) {
        return fromCache(Pair.of(applicationId, emailAddress), applicantsByApplicationIdAndEmail, () -> {
            UserResource user = retrieveUserByEmail(emailAddress);
            return doAs(user, () ->
                    usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).
                            getSuccessObjectOrThrowException());
        });
    }

    protected ProcessRoleResource retrieveLeadApplicant(Long applicationId) {

        return fromCache(applicationId, leadApplicantsByApplicationId, () ->
                doAs(compAdmin(), () ->
                simpleFindFirst(usersRolesService.getProcessRolesByApplicationId(applicationId).
                        getSuccessObjectOrThrowException(), pr -> pr.getRoleName().equals(LEADAPPLICANT.getName())).get()));
    }

    protected Organisation retrieveOrganisationByName(String organisationName) {
        return organisationRepository.findOneByName(organisationName);
    }

    protected Competition retrieveCompetitionByName(String competitionName) {
        return competitionRepository.findByName(competitionName).get(0);
    }

    protected QuestionResource retrieveQuestionByCompetitionAndName(String questionName, Long competitionId) {
        return simpleFindFirst(retrieveQuestionsByCompetitionId(competitionId), q -> questionName.equals(q.getName())).get();
    }

    protected List<QuestionResource> retrieveQuestionsByCompetitionId(Long competitionId) {
        return fromCache(competitionId, questionsByCompetitionId, () ->
                questionService.findByCompetition(competitionId).getSuccessObjectOrThrowException());
    }

    protected List<FormInputResource> retrieveFormInputsByQuestionId(QuestionResource question) {
        return fromCache(question.getId(), formInputsByQuestionId, () ->
                formInputService.findByQuestionId(question.getId()).getSuccessObjectOrThrowException());
    }

    protected OrganisationResource retrieveOrganisationResourceByName(String organisationName) {
        return fromCache(organisationName, organisationsByName, () -> doAs(systemRegistrar(), () -> {
            Organisation organisation = retrieveOrganisationByName(organisationName);
            return organisationService.findById(organisation.getId()).getSuccessObjectOrThrowException();
        }));
    }

    protected ProcessRole retrieveAssessorByApplicationNameAndUser(String applicationName, UserResource user) {

        return testService.doWithinTransaction(() -> {

            Application application = applicationRepository.findByName(applicationName).get(0);

            return processRoleRepository.findByUserAndApplicationId(userRepository.findOne(user.getId()),
                    application.getId())
                    .stream()
                    .filter(x -> x.getRole().getName().equals(ASSESSOR.getName()))
                    .findFirst()
                    .get();
        });
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

    protected S asCompAdmin(Consumer<T> action) {
        return with(data -> doAs(compAdmin(), () -> action.accept(data)));
    }

    protected UserResource retrieveUserByEmailInternal(String email, UserRoleType role) {
        return fromCache(email, usersByEmailAddressInternal, () -> {
            User user = userRepository.findByEmail(email).get();
            return newUserResource().
                    withRolesGlobal(newRoleResource().withType(role).build(1)).
                    withId(user.getId()).
                    build();
        });
    }

    protected<K, V> V fromCache(K key, Cache<K, V> cache, Callable<V> loadingFunction) {
        try {
            return cache.get(key, loadingFunction);
        } catch (ExecutionException e) {
            throw new RuntimeException("Exception encountered whilst reading from Cache", e);
        }
    }
}
