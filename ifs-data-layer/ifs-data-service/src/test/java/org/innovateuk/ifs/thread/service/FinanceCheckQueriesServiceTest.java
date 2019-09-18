package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesServiceImpl;
import org.innovateuk.ifs.threads.domain.MessageThread;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class FinanceCheckQueriesServiceTest extends BaseUnitTestMocksTest {

    private static final String webBaseUrl = "http://ifs-local-dev";

    @InjectMocks
    private FinanceCheckQueriesServiceImpl service;

    @Mock
    private QueryRepository queryRepositoryMock;

    @Mock
    private QueryMapper queryMapper;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Mock
    private ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    private PostMapper postMapper;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private AuthenticationHelper authenticationHelperMock;

    @Mock
    private ActivityLogService activityLogService;

    @Before
    public void before() {
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
    }

    @Test
    public void findOne() throws Exception {

        Long queryId = 1L;
        Query query = new Query(queryId, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, null, null, null, null, false, null, null, null);

        when(queryRepositoryMock.findById(queryId)).thenReturn(Optional.of(query));
        when(queryMapper.mapToResource(query)).thenReturn(queryResource);

        QueryResource response = service.findOne(queryId).getSuccess();

        assertEquals(queryResource, response);
    }

    @Test
    public void findAll() throws Exception {

        Long contextId = 22L;
        Query query1 = new Query(1L, null, null, null, null, null);
        Query query2 = new Query(2L, null, null, null, null, null);
        List<Query> queries = asList(query1, query2);

        QueryResource queryResource1 = new QueryResource(1L, null, null, null,
                null, false, null, null, null);
        QueryResource queryResource2 = new QueryResource(2L, null, null, null,
                null, false, null, null, null);
        List<QueryResource> queryResources = asList(queryResource1, queryResource2);

        when(queryRepositoryMock.findAllByClassPkAndClassName(contextId, ProjectFinance.class.getName())).thenReturn(queries);
        when(queryMapper.mapToResource(query1)).thenReturn(queryResource1);
        when(queryMapper.mapToResource(query2)).thenReturn(queryResource2);

        List<QueryResource> response = service.findAll(contextId).getSuccess();

        assertEquals(queryResources, response);
    }

    @Test
    public void create() throws Exception {

        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null, null, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null, null, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

        User user = newUser().
                withEmailAddress("a@b.com").
                withFirstName("A").
                withLastName("B").
                build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        User user2 = newUser().
                withEmailAddress("Z@Y.com").
                withFirstName("Z").
                withLastName("Y").
                build();

        Organisation organisation2 = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        List<ProjectUser> projectUsers = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT, PROJECT_FINANCE_CONTACT)
                .withUser(user, user2)
                .withOrganisation(organisation, organisation2)
                .build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withProjectUsers(projectUsers)
                .withPartnerOrganisations(newPartnerOrganisation()
                .withOrganisation(organisation)
                .build(1))
                .withApplication(application)
                .build();

        ProjectFinance projectFinance = newProjectFinance()
                .withProject(project)
                .withOrganisation(organisation)
                .build();

        NotificationTarget target = new UserNotificationTarget(user.getName(), user.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "http://ifs-local-dev/project-setup/project/" + project.getId(),
                "applicationId", project.getApplication().getId(),
                "competitionName", "Competition 1");

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target), FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.of(projectFinance));
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        Long result = service.create(queryToCreate).getSuccess();

        assertEquals(result, Long.valueOf(1L));

        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
        verify(activityLogService).recordQueryActivityByProjectFinanceId(queryToCreateAsDomain.contextClassPk(), ActivityType.FINANCE_QUERY, result);
    }

    @Test
    public void createNoFinanceContact() throws Exception {

        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null, null, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null, null, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

        User user = newUser().
                withEmailAddress("a@b.com").
                withFirstName("A").
                withLastName("B").
                build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        User user2 = newUser().
                withEmailAddress("Z@Y.com").
                withFirstName("Z").
                withLastName("Y").
                build();

        Organisation organisation2 = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        List<ProjectUser> projectUser = newProjectUser()
                .withRole(PROJECT_MANAGER, PROJECT_PARTNER)
                .withUser(user, user2)
                .withOrganisation(organisation, organisation2)
                .build(2);

        Project project = newProject()
                .withProjectUsers(projectUser)
                .withPartnerOrganisations(newPartnerOrganisation()
                .withOrganisation(organisation).build(1))
                .build();

        ProjectFinance projectFinance = newProjectFinance()
                .withProject(project)
                .withOrganisation(organisation)
                .build();

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.of(projectFinance));

        ServiceResult<Long> result = service.create(queryToCreate);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(forbiddenError(QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED)));
    }

    @Test
    public void createNotificationNotSent() throws Exception {

        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null, null, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null, null, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

        User user = newUser()
                .withEmailAddress("a@b.com")
                .withFirstName("A")
                .withLastName("B")
                .build();

        Organisation organisation = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .build();

        User user2 = newUser()
                .withEmailAddress("Z@Y.com")
                .withFirstName("Z")
                .withLastName("Y")
                .build();

        Organisation organisation2 = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        List<ProjectUser> projectUser = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER)
                .withUser(user, user2).withOrganisation(organisation, organisation2)
                .build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withProjectUsers(projectUser)
                .withPartnerOrganisations(newPartnerOrganisation()
                .withOrganisation(organisation)
                .build(1))
                .withApplication(application)
                .build();

        ProjectFinance projectFinance = newProjectFinance()
                .withProject(project)
                .withOrganisation(organisation)
                .build();

        NotificationTarget target = new UserNotificationTarget(user.getName(), user.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "http://ifs-local-dev/project-setup/project/" + project.getId(),
                "applicationId", project.getApplication().getId(),
                "competitionName", "Competition 1");

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target), FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.of(projectFinance));
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND));

        assertEquals(false, service.create(queryToCreate).isSuccess());
    }

    @Test
    public void createNoProjectFinance() throws Exception {

        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null, null, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null, null, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.empty());

        ServiceResult<Long> result = service.create(queryToCreate);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(notFoundError(ProjectFinance.class, 22L)));
    }

    @Test
    public void closeQueryWhenQueryNotFound() throws Exception {

        Long queryId = 1L;

        ServiceResult<Void> result = service.close(queryId);

        assertTrue(result.isFailure());

        List<Object> allArguments = new ArrayList<>();
        allArguments.add(MessageThread.class.getSimpleName());
        assertTrue(result.getFailure().is(new Error(CommonFailureKeys.GENERAL_NOT_FOUND, MessageThread.class.getSimpleName() + " not found", allArguments, NOT_FOUND)));
    }

    @Test
    public void closeQuerySuccess() throws Exception {

        Long queryId = 1L;
        Long loggedInUserId = 18L;

        Query queryInDB = new Query(queryId, 22L, ProjectFinance.class.getName(), null, null, null, null);
        User loggedInUser = UserBuilder.newUser()
                .withFirstName("Lee")
                .withLastName("Bowman")
                .build();

        when(queryRepositoryMock.findById(queryId)).thenReturn(Optional.of(queryInDB));
        when(authenticationHelperMock.getCurrentlyLoggedInUser()).thenReturn(serviceSuccess(loggedInUser));

        setLoggedInUser(newUserResource()
                .withId(loggedInUserId)
                .withRolesGlobal(singletonList(Role.PROJECT_FINANCE))
                .build());

        assertNull(queryInDB.getClosedBy());
        assertNull(queryInDB.getClosedDate());

        ServiceResult<Void> result = service.close(queryId);

        assertTrue(result.isSuccess());
        assertEquals(queryInDB.getClosedBy(), loggedInUser);
        assertNotNull(queryInDB.getClosedDate());
    }

    @Test
    public void addPost() throws Exception {

        Long queryId = 1L;

        User user = newUser().withId(33L).withRoles(singleton(Role.PROJECT_FINANCE)).build();
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build(), null, null, null);
        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null, null, null);

        when(queryRepositoryMock.findById(queryId)).thenReturn(Optional.of(targetedQuery));
        when(postMapper.mapToDomain(post)).thenReturn(mappedPost);
        when(queryMapper.mapToResource(targetedQuery)).thenReturn(queryResource);

        User user1 = newUser()
                .withEmailAddress("a@b.com")
                .withFirstName("A")
                .withLastName("B")
                .build();

        Organisation organisation = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .build();

        User user2 = newUser()
                .withEmailAddress("Z@Y.com")
                .withFirstName("Z")
                .withLastName("Y")
                .build();

        Organisation organisation2 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .build();

        List<ProjectUser> projecetUser = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER)
                .withUser(user1, user2)
                .withOrganisation(organisation, organisation2)
                .build(2);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withProjectUsers(projecetUser)
                .withPartnerOrganisations(newPartnerOrganisation()
                .withOrganisation(organisation)
                .build(1))
                .withApplication(application)
                .build();

        ProjectFinance projectFinance = newProjectFinance()
                .withProject(project)
                .withOrganisation(organisation)
                .build();

        NotificationTarget target = new UserNotificationTarget(user1.getName(), user1.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "http://ifs-local-dev/project-setup/project/" + project.getId(),
                "applicationId", project.getApplication().getId(),
                "competitionName", "Competition 1",
                "applicationName", "Application 1");

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target),
                FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY_RESPONSE, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.of(projectFinance));
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        assertTrue(service.addPost(post, queryId).isSuccess());

        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
    }

    @Test
    public void addPostNotFinanceTeam() throws Exception {

        Long queryId = 1L;
        User user = newUser().withId(33L).withRoles(singleton(Role.COMP_ADMIN)).build();
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(Role.COMP_ADMIN)).build(), null, null, null);
        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null, null, null);
        User u = newUser().withEmailAddress("a@b.com").withFirstName("A").withLastName("B").build();
        User u2 = newUser().withEmailAddress("Z@Y.com").withFirstName("Z").withLastName("Y").build();
        Organisation o = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).withUser(u, u2).withOrganisation(o).build(1);
        Application app = newApplication().withName("App1").build();
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).withApplication(app).build();
        ProjectFinance pf = newProjectFinance().withProject(p).withOrganisation(o).build();

        when(queryRepositoryMock.findById(targetedQuery.id())).thenReturn(Optional.of(targetedQuery));

        when(postMapper.mapToDomain(post)).thenReturn(mappedPost);

        when(queryMapper.mapToResource(targetedQuery)).thenReturn(queryResource);

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.of(pf));

        assertTrue(service.addPost(post, queryId).isSuccess());

    }

    @Test
    public void addPostSuperAddPostFails() throws Exception {
        Long queryId = 1L;
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(Role.COMP_ADMIN)).build(), null, null, null);

        when(queryRepositoryMock.findById(queryId)).thenReturn(Optional.empty());

        assertTrue(service.addPost(post, queryId).isFailure());
    }

    @Test
    public void addPostNoQueryToAddPostTo() throws Exception {
        Long queryId = 1L;
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(Role.COMP_ADMIN)).build(), null, null, null);

        when(queryRepositoryMock.findById(queryId)).thenReturn(Optional.empty());

        assertTrue(service.addPost(post, queryId).isFailure());
    }

    @Test
    public void addPostNoFinanceContact() {
        Long queryId = 1L;
        User user = newUser().withId(33L).withRoles(singleton(Role.PROJECT_FINANCE)).build();
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build(), null, null, null);
        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null, null, null);

        when(queryRepositoryMock.findById(queryId)).thenReturn(Optional.of(targetedQuery));

        when(postMapper.mapToDomain(post)).thenReturn(mappedPost);

        when(queryMapper.mapToResource(targetedQuery)).thenReturn(queryResource);

        User u = newUser().
                withEmailAddress("a@b.com").
                withFirstName("A").
                withLastName("B").
                build();
        Organisation o = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();
        User u2 = newUser().
                withEmailAddress("Z@Y.com").
                withFirstName("Z").
                withLastName("Y").
                build();
        Organisation o2 = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER, PROJECT_PARTNER).withUser(u, u2).withOrganisation(o, o2).build(1);
        Application app = newApplication().withName("App1").build();
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).withApplication(app).build();

        ProjectFinance pf = newProjectFinance().withProject(p).withOrganisation(o).build();

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.of(pf));

        ServiceResult<Void> result = service.addPost(post, queryId);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(forbiddenError(QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED)));
    }

    @Test
    public void addPostNotificationNotSent() throws Exception {
        Long queryId = 1L;

        User user = newUser()
                .withId(33L)
                .withRoles(singleton(Role.PROJECT_FINANCE))
                .build();

        PostResource post = new PostResource(null, newUserResource()
                .withId(33L)
                .withRolesGlobal(singletonList(Role.PROJECT_FINANCE))
                .build(), null, null, null);

        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null, null, null);

        when(queryRepositoryMock.findById(queryId)).thenReturn(Optional.of(targetedQuery));
        when(postMapper.mapToDomain(post)).thenReturn(mappedPost);
        when(queryMapper.mapToResource(targetedQuery)).thenReturn(queryResource);

        User user1 = newUser().
                withEmailAddress("a@b.com").
                withFirstName("A").
                withLastName("B").
                build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        User user2 = newUser().
                withEmailAddress("Z@Y.com").
                withFirstName("Z").
                withLastName("Y").
                build();

        Organisation organisation2 = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        List<ProjectUser> projectUser = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER)
                .withUser(user1, user2).withOrganisation(organisation, organisation2)
                .build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withProjectUsers(projectUser)
                .withPartnerOrganisations(newPartnerOrganisation()
                .withOrganisation(organisation)
                .build(1))
                .withApplication(application)
                .build();

        ProjectFinance projectFinance = newProjectFinance()
                .withProject(project)
                .withOrganisation(organisation)
                .build();

        NotificationTarget target = new UserNotificationTarget(user1.getName(), user1.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "http://ifs-local-dev/project-setup/project/" + project.getId(),
                "applicationId", project.getApplication().getId(),
                "competitionName", "Competition 1",
                "applicationName", "Application 1");

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target),
                FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY_RESPONSE, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findById(22L)).thenReturn(Optional.of(projectFinance));
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND));

        assertTrue(service.addPost(post, queryId).isFailure());
    }

    protected void setLoggedInUser(UserResource loggedInUser) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(loggedInUser));
    }
}
