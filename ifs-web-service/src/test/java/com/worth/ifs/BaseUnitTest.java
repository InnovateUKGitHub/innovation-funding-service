package com.worth.ifs;

import com.worth.ifs.application.builder.QuestionBuilder;
import com.worth.ifs.application.builder.SectionBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.model.UserApplicationRole;
import com.worth.ifs.application.model.UserRole;
import com.worth.ifs.application.service.*;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.service.CompetitionsRestService;
import com.worth.ifs.exception.ErrorController;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.security.UserAuthentication;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BaseUnitTest {
    public MockMvc mockMvc;
    public User loggedInUser;
    public User assessor;
    public User applicant;

    public UserAuthentication loggedInUserAuthentication;

    protected final Log log = LogFactory.getLog(getClass());

    @Mock
    public UserAuthenticationService userAuthenticationService;
    @Mock
    public ResponseService responseService;
    @Mock
    public FormInputResponseService formInputResponseService;
    @Mock
    public ApplicationService applicationService;
    @Mock
    public CompetitionsRestService competitionRestService;
    @Mock
    public AssessmentRestService assessmentRestService;
    @Mock
    public ProcessRoleService processRoleService;
    @Mock
    public UserService userService;
    @Mock
    public FinanceService financeService;
    @Mock
    public CostService costService;
    @Mock
    public CostRestService costRestService;
    @Mock
    public ApplicationRestService applicationRestService;
    @Mock
    public QuestionService questionService;
    @Mock
    public OrganisationService organisationService;
    @Mock
    public SectionService sectionService;

    public List<com.worth.ifs.application.domain.Application> applications;
    public List<Section> sections;
    public Map<Long, Question> questions;
    public List<Competition> competitions;
    public Competition competition;
    public List<User> users;
    public List<Organisation> organisations;
    TreeSet<Organisation> organisationSet;
    public List<Assessment> assessments;
    public List<Assessment> submittedAssessments;
    public ApplicationStatus submittedApplicationStatus;
    public ApplicationStatus createdApplicationStatus;
    public ApplicationStatus approvedApplicationStatus;
    public ApplicationStatus rejectedApplicationStatus;
    public ApplicationFinance applicationFinance;

    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    public void setup(){
        loggedInUser = new User(1L, "Nico Bijl", "email@email.nl", "test", "tokenABC", "image", new ArrayList());
        applicant = loggedInUser;
        User user2 = new User(2L, "Brent de Kok", "email@email.nl", "test", "tokenBCD", "image", new ArrayList());
        assessor = new User(3L, "Assessor", "email@assessor.nl", "test", "tokenDEF", "image", new ArrayList<>());
        users = asList(loggedInUser, user2);

        loggedInUserAuthentication = new UserAuthentication(loggedInUser);

        applications = new ArrayList<>();
        sections = new ArrayList<>();
        questions = new HashMap<>();
        organisations = new ArrayList<>();
    }

    public void loginDefaultUser(){
        when(userAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(loggedInUserAuthentication);
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(loggedInUser);
    }
    public void loginUser(User user){
        UserAuthentication userAuthentication = new UserAuthentication(user);
        when(userAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(userAuthentication);
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
    }

    public void setupCompetition(){

        competition = newCompetition().with(id(1L)).with(name("Competition x")).with(description("Description afds")).
                withStartDate(LocalDateTime.now().minusDays(2)).withEndDate(LocalDateTime.now().plusDays(5)).
                build();

        QuestionBuilder questionBuilder = newQuestion().with(competition(competition));
        SectionBuilder sectionBuilder = newSection().with(competition(competition));

        Question q01 = questionBuilder.with(id(1L)).with(name("Application details")).build();

        Section section1 = sectionBuilder.
                with(id(1L)).
                with(name("Application details")).
                withQuestions(asList(q01)).
                build();

        Question q10 = questionBuilder.with(id(10L)).with(name("How does your project align with the scope of this competition?")).build();

        Section section2 = sectionBuilder.
                with(id(2L)).
                with(name("Scope (Gateway question)")).
                withQuestions(asList(q10)).
                build();

        Question q20 = questionBuilder.with(id(20L)).with(name("1. What is the business opportunity that this project addresses?")).build();
        Question q21 = questionBuilder.with(id(21L)).with(name("2. What is the size of the market opportunity that this project might open up?")).build();
        Question q22 = questionBuilder.with(id(22L)).with(name("3. How will the results of the project be exploited and disseminated?")).build();
        Question q23 = questionBuilder.with(id(23L)).with(name("4. What economic, social and environmental benefits is the project expected to deliver?")).build();

        Section section3 = sectionBuilder.
                with(id(3L)).
                with(name("Business proposition (Q1 - Q4)")).
                withQuestions(asList(q20, q21, q22, q23)).
                build();

        Question q30 = questionBuilder.with(id(30L)).with(name("5. What technical approach will be adopted and how will the project be managed?")).build();
        Question q31 = questionBuilder.with(id(31L)).with(name("6. What is innovative about this project?")).build();
        Question q32 = questionBuilder.with(id(32L)).with(name("7. What are the risks (technical, commercial and environmental) to project success? What is the project's risk management strategy?")).build();
        Question q33 = questionBuilder.with(id(33L)).with(name("8. Does the project team have the right skills and experience and access to facilities to deliver the identified benefits?")).build();

        Section section4 = sectionBuilder.
                with(id(4L)).
                with(name("Project approach (Q5 - Q8)")).
                withQuestions(asList(q30, q31, q32, q33)).
                build();

        Section section5 = sectionBuilder.with(id(5L)).with(name("Funding (Q9 - Q10)")).build();
        Section section6 = sectionBuilder.with(id(6L)).with(name("Finances")).build();

        sections = asList(section1, section2, section3, section4, section5, section6);

        ArrayList<Question> questionList = new ArrayList<>();
        for (Section section : sections) {
            List<Question> sectionQuestions = section.getQuestions();
            if(sectionQuestions != null){
                Map<Long, Question> questionsMap =
                        sectionQuestions.stream().collect(Collectors.toMap(Question::getId,
                                Function.identity()));
                questionList.addAll(sectionQuestions);
                questions.putAll(questionsMap);
            }
        }

        competitions = asList(competition);
        when(questionService.findByCompetition(competition.getId())).thenReturn(questionList);
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(competition);
        when(competitionRestService.getAll()).thenReturn(competitions);
    }

    public void setupUserRoles() {
        Role assessorRole = new Role(3L, UserRole.ASSESSOR.getRoleName(), null);
        Role applicantRole = new Role(4L, UserRole.APPLICANT.getRoleName(), null);
        loggedInUser.setRoles(asList(applicantRole));
        assessor.setRoles(asList(assessorRole));
    }

    public void setupApplicationWithRoles(){
        createdApplicationStatus = new ApplicationStatus(ApplicationStatusConstants.CREATED.getId(), ApplicationStatusConstants.CREATED.getName());
        submittedApplicationStatus = new ApplicationStatus(ApplicationStatusConstants.SUBMITTED.getId(), ApplicationStatusConstants.SUBMITTED.getName());
        approvedApplicationStatus = new ApplicationStatus(ApplicationStatusConstants.APPROVED.getId(), ApplicationStatusConstants.APPROVED.getName());
        rejectedApplicationStatus = new ApplicationStatus(ApplicationStatusConstants.REJECTED.getId(), ApplicationStatusConstants.REJECTED.getName());

        com.worth.ifs.application.domain.Application app1 = new com.worth.ifs.application.domain.Application(1L, "Rovel Additive Manufacturing Process", createdApplicationStatus);
        com.worth.ifs.application.domain.Application app2 = new com.worth.ifs.application.domain.Application(2L, "Providing sustainable childcare", submittedApplicationStatus);
        com.worth.ifs.application.domain.Application app3 = new com.worth.ifs.application.domain.Application(3L, "Mobile Phone Data for Logistics Analytics", approvedApplicationStatus);
        com.worth.ifs.application.domain.Application app4 = new com.worth.ifs.application.domain.Application(4L, "Using natural gas to heat homes", rejectedApplicationStatus);
        Role role1 = new Role(1L, UserApplicationRole.LEAD_APPLICANT.getRoleName(), null);
        Role role2 = new Role(2L, UserApplicationRole.COLLABORATOR.getRoleName(), null);
        Role assessorRole = new Role(3L, UserRole.ASSESSOR.getRoleName(), null);

        Organisation organisation1 = new Organisation(1L, "Empire Ltd");
        Organisation organisation2 = new Organisation(2L, "Ludlow");
        organisations = asList(organisation1, organisation2);
        Comparator<Organisation> compareById = Comparator.comparingLong(Organisation::getId);
        organisationSet = new TreeSet<>(compareById);
        organisationSet.addAll(organisations);

        ProcessRole processRole1 = new ProcessRole(1L, loggedInUser, app1, role1, organisation1);
        ProcessRole processRole2 = new ProcessRole(2L, loggedInUser, app2, role1, organisation1);
        ProcessRole processRole3 = new ProcessRole(3L, loggedInUser, app3, role1, organisation1);
        ProcessRole processRole4 = new ProcessRole(4L, loggedInUser, app4, role1, organisation1);
        ProcessRole processRole5 = new ProcessRole(5L, users.get(1), app1, role2, organisation2);
        ProcessRole processRole6 = new ProcessRole(6L, assessor, app2, assessorRole, organisation1);
        ProcessRole processRole7 = new ProcessRole(7L, assessor, app3, assessorRole, organisation1);
        ProcessRole processRole8 = new ProcessRole(8L, assessor, app1, assessorRole, organisation1);


        organisation1.setProcessRoles(asList(processRole1, processRole2, processRole3, processRole4, processRole7, processRole8));
        organisation2.setProcessRoles(asList(processRole5));

        competition.addApplication(app1, app2, app3, app4);

        app1.setCompetition(competition);
        app1.setProcessRoles(asList(processRole1, processRole5));
        app2.setCompetition(competition);
        app2.setProcessRoles(asList(processRole2));
        app3.setCompetition(competition);
        app3.setProcessRoles(asList(processRole3, processRole7, processRole8));
        app4.setCompetition(competition);
        app4.setProcessRoles(asList(processRole4));

        loggedInUser.addUserApplicationRole(processRole1, processRole2, processRole3, processRole4);
        users.get(1).addUserApplicationRole(processRole5);
        applications = asList(app1, app2, app3, app4);

        when(sectionService.getParentSections(competition.getSections())).thenReturn(sections);
        when(sectionService.getCompleted(app1.getId(), organisation1.getId())).thenReturn(asList(1L, 2L));
        when(sectionService.getInCompleted(app1.getId())).thenReturn(asList(3L, 4L));
        when(processRoleService.findProcessRole(loggedInUser.getId(), app1.getId())).thenReturn(processRole1);
        when(processRoleService.findProcessRole(assessor.getId(), app2.getId())).thenReturn(processRole6);
        when(processRoleService.findProcessRole(assessor.getId(), app3.getId())).thenReturn(processRole7);
        when(processRoleService.findProcessRole(assessor.getId(), app1.getId())).thenReturn(processRole8);
        when(applicationRestService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app1.getId())).thenReturn(app1);
        when(applicationService.getById(app2.getId())).thenReturn(app2);
        when(applicationService.getById(app3.getId())).thenReturn(app3);
        when(applicationService.getById(app4.getId())).thenReturn(app4);
        when(organisationService.getUserOrganisation(app1, loggedInUser.getId())).thenReturn(Optional.of(organisation1));
        when(organisationService.getApplicationLeadOrganisation(app1)).thenReturn(Optional.of(organisation1));
        when(organisationService.getApplicationOrganisations(app1)).thenReturn(organisationSet);
        when(organisationService.getApplicationOrganisations(app2)).thenReturn(organisationSet);
        when(organisationService.getApplicationOrganisations(app3)).thenReturn(organisationSet);
        when(organisationService.getApplicationOrganisations(app4)).thenReturn(organisationSet);
        when(userService.isLeadApplicant(loggedInUser.getId(),app1)).thenReturn(true);
        when(organisationService.getApplicationLeadOrganisation(app1)).thenReturn(Optional.of(organisation1));
        when(organisationService.getApplicationLeadOrganisation(app2)).thenReturn(Optional.of(organisation1));
        when(organisationService.getApplicationLeadOrganisation(app3)).thenReturn(Optional.of(organisation1));
        //when(organisationService.getOrganisationsByApplicationId(app1.getId())).thenReturn(organisations);

    }

    public void setupApplicationResponses(){
        Application application = applications.get(0);

        Boolean markAsComplete = false;
        ProcessRole userApplicationRole = loggedInUser.getProcessRoles().get(0);

        Response response = new Response(1L, LocalDateTime.now(), "value 1", userApplicationRole, questions.get(20L), application);
        Response response2 = new Response(2L, LocalDateTime.now(), "value 1", userApplicationRole, questions.get(21L), application);

        List<Response> responses = asList(response, response2);
        userApplicationRole.setResponses(responses);

        questions.get(20L).setResponses(asList(response));
        questions.get(21L).setResponses(asList(response2));

        when(responseService.getByApplication(application.getId())).thenReturn(responses);
    }

    public void setupFinances() {
        Application application = applications.get(0);
        applicationFinance = new ApplicationFinance(1L, application, organisations.get(0));
        when(financeService.getApplicationFinances(application.getId())).thenReturn(asList(applicationFinance));
        when(financeService.getApplicationFinance(loggedInUser.getId(), application.getId())).thenReturn(applicationFinance);
    }

    public void setupAssessment(){
        Assessment assessment1 = new Assessment(assessor, applications.get(0));
        assessment1.setId(1L);
        Assessment assessment2 = new Assessment(assessor, applications.get(1));
        assessment2.setId(2L);
        Assessment assessment3 = new Assessment(assessor, applications.get(2));
        assessment3.setId(3L);
        assessment3.submit();

        when(assessmentRestService.getTotalAssignedByAssessorAndCompetition(assessor.getId(), competition.getId())).thenReturn(3);
        when(assessmentRestService.getTotalSubmittedByAssessorAndCompetition(assessor.getId(), competition.getId())).thenReturn(1);

        assessment1.setProcessStatus(AssessmentStates.REJECTED.getState());
        assessment2.setProcessStatus(AssessmentStates.PENDING.getState());
        assessment3.setProcessStatus(AssessmentStates.SUBMITTED.getState());

        submittedAssessments = asList(assessment3);
        assessments = asList(assessment1, assessment2, assessment3);
        when(assessmentRestService.getAllByAssessorAndCompetition(assessor.getId(), competition.getId())).thenReturn(assessments);
        when(assessmentRestService.getOneByAssessorAndApplication(assessor.getId(), applications.get(0).getId())).thenReturn(assessment1);
        when(assessmentRestService.getOneByAssessorAndApplication(assessor.getId(), applications.get(1).getId())).thenReturn(assessment2);
        when(assessmentRestService.getOneByAssessorAndApplication(assessor.getId(), applications.get(2).getId())).thenReturn(assessment3);




        when(organisationService.getUserOrganisation(applications.get(0), assessor.getId())).thenReturn(Optional.of(organisations.get(0)));
        when(organisationService.getUserOrganisation(applications.get(1), assessor.getId())).thenReturn(Optional.of(organisations.get(0)));
        when(organisationService.getUserOrganisation(applications.get(2), assessor.getId())).thenReturn(Optional.of(organisations.get(0)));
    }

    public ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(ErrorController.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new ErrorController(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }
}
