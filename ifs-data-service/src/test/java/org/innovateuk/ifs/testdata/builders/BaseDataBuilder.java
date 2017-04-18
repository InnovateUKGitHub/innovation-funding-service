package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
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
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileService;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.publiccontent.repository.ContentEventRepository;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
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
import org.innovateuk.ifs.user.transactional.*;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Base Builder for persistent data builders.  Wraps each build step in a transaction.  Provides a location for
 * service lookup.
 */
public abstract class BaseDataBuilder<T, S> extends BaseBuilder<T, S> {

    public static final String COMP_ADMIN_EMAIL = "john.doe@innovateuk.test";
    public static final String INNOVATE_UK_ORG_NAME = "Innovate UK";

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
    protected ContentEventRepository contentEventRepository;
    protected OrganisationService organisationService;
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
    protected UserProfileService userProfileService;
    protected ApplicationInnovationAreaService applicationInnovationAreaService;
    protected AssessorFormInputResponseService assessorFormInputResponseService;

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
        this.competitionParticipantRepository = serviceLocator.getBean(CompetitionParticipantRepository.class);
        this.competitionInviteService = serviceLocator.getBean(CompetitionInviteService.class);
        this.testService = serviceLocator.getBean(TestService.class);
        this.assessmentRepository = serviceLocator.getBean(AssessmentRepository.class);
        this.assessmentService = serviceLocator.getBean(AssessmentService.class);
        this.assessmentWorkflowHandler = serviceLocator.getBean(AssessmentWorkflowHandler.class);
        this.processRoleRepository = serviceLocator.getBean(ProcessRoleRepository.class);
        this.activityStateRepository = serviceLocator.getBean(ActivityStateRepository.class);
        this.sectionRepository = serviceLocator.getBean(SectionRepository.class);
        this.questionRepository = serviceLocator.getBean(QuestionRepository.class);
        this.formInputRepository = serviceLocator.getBean(FormInputRepository.class);
        this.fileEntryRepository = serviceLocator.getBean(FileEntryRepository.class);
        this.applicationFinanceRepository = serviceLocator.getBean(ApplicationFinanceRepository.class);
        this.projectUserRepository = serviceLocator.getBean(ProjectUserRepository.class);
        this.bankDetailsService = serviceLocator.getBean(BankDetailsService.class);
        this.spendProfileService = serviceLocator.getBean(SpendProfileService.class);
        this.financeCheckService = serviceLocator.getBean(FinanceCheckService.class);
        this.competitionFunderRepository = serviceLocator.getBean(CompetitionFunderRepository.class);
        this.innovationAreaRepository = serviceLocator.getBean(InnovationAreaRepository.class);
        this.innovationSectorRepository = serviceLocator.getBean(InnovationSectorRepository.class);
        this.researchCategoryRepository = serviceLocator.getBean(ResearchCategoryRepository.class);
        this.rejectionReasonService = serviceLocator.getBean(RejectionReasonService.class);
        this.userProfileService = serviceLocator.getBean(UserProfileService.class);
        this.baseUserService = serviceLocator.getBean(BaseUserService.class);
        this.profileRepository = serviceLocator.getBean(ProfileRepository.class);
        this.publicContentService = serviceLocator.getBean(PublicContentService.class);
        this.publicContentRepository = serviceLocator.getBean(PublicContentRepository.class);
        this.contentEventRepository = serviceLocator.getBean(ContentEventRepository.class);
        this.contentGroupRepository = serviceLocator.getBean(ContentGroupRepository.class);
        this.assessorFormInputResponseService = serviceLocator.getBean(AssessorFormInputResponseService.class);
        this.applicationInnovationAreaService = serviceLocator.getBean(ApplicationInnovationAreaService.class);
    }

    @Override
    public S with(Consumer<T> amendFunction) {
        return super.with(data -> testService.doWithinTransaction(() -> amendFunction.accept(data)));
    }

    protected UserResource compAdmin() {
        return retrieveUserByEmail(COMP_ADMIN_EMAIL);
    }

    protected UserResource retrieveUserByEmail(String emailAddress) {
        return doAs(systemRegistrar(), () -> userService.findByEmail(emailAddress).getSuccessObjectOrThrowException());
    }

    protected UserResource retrieveUserById(Long id) {
        return doAs(systemRegistrar(), () -> baseUserService.getUserById(id).getSuccessObjectOrThrowException());
    }

    protected ProcessRoleResource retrieveApplicantByEmail(String emailAddress, Long applicationId) {
        return doAs(systemRegistrar(), () -> {
            UserResource user = retrieveUserByEmail(emailAddress);
            return doAs(user, () -> {
                return usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).
                        getSuccessObjectOrThrowException();
            });
        });
    }

    protected ProcessRoleResource retrieveLeadApplicant(Long applicationId) {
        return doAs(compAdmin(), () ->
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

    protected ProcessRole retrieveAssessorByApplicationNameAndUser(String applicationName, UserResource user) {
        Application application = applicationRepository.findByName(applicationName).get(0);

        return processRoleRepository.findByUserAndApplicationId(userRepository.findOne(user.getId()),
                application.getId())
                .stream()
                .filter(x -> x.getRole().getName().equals(ASSESSOR.getName()))
                .findFirst()
                .get();
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
