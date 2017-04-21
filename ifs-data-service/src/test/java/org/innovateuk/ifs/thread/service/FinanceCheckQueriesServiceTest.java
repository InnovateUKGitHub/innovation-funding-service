package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.queries.service.FinanceCheckQueriesServiceImpl;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.user.builder.RoleBuilder;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FinanceCheckQueriesServiceTest extends BaseUnitTestMocksTest {

    private static final String webBaseUrl = "http://ifs-local-dev";

    @InjectMocks
    private FinanceCheckQueriesServiceImpl service;

    @Before
    public void before() {
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
    }

    @Test
    public void test_findOne() throws Exception {
        Long queryId = 1L;
        Query query = new Query(queryId, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, null, null, null, null, false, null);
        when(queryRepositoryMock.findOne(queryId)).thenReturn(query);
        when(queryMapper.mapToResource(query)).thenReturn(queryResource);

        QueryResource response = service.findOne(queryId).getSuccessObjectOrThrowException();

        assertEquals(queryResource, response);
    }

    @Test
    public void test_findAll() throws Exception {
        Long contextId = 22L;
        Query query1 = new Query(1L, null, null, null, null, null);
        Query query2 = new Query(2L, null, null, null, null, null);
        List<Query> queries = asList(query1, query2);

        QueryResource queryResource1 = new QueryResource(1L, null, null, null,
                null, false, null);
        QueryResource queryResource2 = new QueryResource(2L, null, null, null,
                null, false, null);
        List<QueryResource> queryResources = asList(queryResource1, queryResource2);

        when(queryRepositoryMock.findAllByClassPkAndClassName(contextId, ProjectFinance.class.getName())).thenReturn(queries);
        when(queryMapper.mapToResource(query1)).thenReturn(queryResource1);
        when(queryMapper.mapToResource(query2)).thenReturn(queryResource2);

        List<QueryResource> response = service.findAll(contextId).getSuccessObjectOrThrowException();

        assertEquals(queryResources, response);
    }

    @Test
    public void test_create() throws Exception {
        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

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
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).withUser(u, u2).withOrganisation(o, o2).build(1);
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).build();

        ProjectFinance pf = newProjectFinance().withProject(p).withOrganisation(o).build();

        NotificationTarget target = new ExternalUserNotificationTarget(u.getName(), u.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap("dashboardUrl", "http://ifs-local-dev/project-setup/project/" + p.getId());

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target), FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findOne(22L)).thenReturn(pf);
        when(notificationServiceMock.sendNotification(notification, EMAIL)).thenReturn(serviceSuccess());

        Long result = service.create(queryToCreate).getSuccessObjectOrThrowException();

        assertEquals(result, Long.valueOf(1L));

        verify(notificationServiceMock).sendNotification(notification, EMAIL);
    }

    @Test
    public void test_createNoFinanceContact() throws Exception {
        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

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
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).build();

        ProjectFinance pf = newProjectFinance().withProject(p).withOrganisation(o).build();

        when(projectFinanceRepositoryMock.findOne(22L)).thenReturn(pf);

        Long result = service.create(queryToCreate).getSuccessObjectOrThrowException();

        assertEquals(result, Long.valueOf(1L));

    }

    @Test
    public void test_createNotificationNotSent() throws Exception {
        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

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
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).withUser(u, u2).withOrganisation(o, o2).build(1);
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).build();

        ProjectFinance pf = newProjectFinance().withProject(p).withOrganisation(o).build();

        NotificationTarget target = new ExternalUserNotificationTarget(u.getName(), u.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap("dashboardUrl", "http://ifs-local-dev/project-setup/project/" + p.getId());

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target), FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findOne(22L)).thenReturn(pf);
        when(notificationServiceMock.sendNotification(notification, EMAIL)).thenReturn(serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND));

        assertEquals(false, service.create(queryToCreate).isSuccess());

    }

    @Test
    public void test_createNoProjectFinance() throws Exception {
        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);

        when(projectFinanceRepositoryMock.findOne(22L)).thenReturn(null);

        Long result = service.create(queryToCreate).getSuccessObjectOrThrowException();

        assertEquals(result, Long.valueOf(1L));

    }

    @Test
    public void test_addPost() throws Exception {
        Long queryId = 1L;

        User user = newUser().withId(33L).withRoles(singleton(RoleBuilder.newRole(UserRoleType.PROJECT_FINANCE).build())).build();
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build(), null, null, null);
        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null);

        when(queryRepositoryMock.findOne(queryId)).thenReturn(targetedQuery);

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
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).withUser(u, u2).withOrganisation(o, o2).build(1);
        Application app = newApplication().withName("App1").build();
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation().withOrganisation(o).build(1)).withApplication(app).build();

        ProjectFinance pf = newProjectFinance().withProject(p).withOrganisation(o).build();

        NotificationTarget target = new ExternalUserNotificationTarget(u.getName(), u.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap("dashboardUrl", "http://ifs-local-dev/project-setup/project/" + p.getId(),
                                                                  "applicationName", "App1");

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target),
                FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY_RESPONSE, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findOne(22L)).thenReturn(pf);
        when(notificationServiceMock.sendNotification(notification, EMAIL)).thenReturn(serviceSuccess());

        assertTrue(service.addPost(post, queryId).isSuccess());

        verify(notificationServiceMock).sendNotification(notification, EMAIL);
    }

    @Test
    public void test_addPostNotFinanceTeam() throws Exception {
        Long queryId = 1L;
        User user = newUser().withId(33L).withRoles(singleton(RoleBuilder.newRole(UserRoleType.COMP_ADMIN).build())).build();
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build(), null, null, null);
        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null);

        when(queryRepositoryMock.findOne(queryId)).thenReturn(targetedQuery);

        when(postMapper.mapToDomain(post)).thenReturn(mappedPost);

        when(queryMapper.mapToResource(targetedQuery)).thenReturn(queryResource);

        assertTrue(service.addPost(post, queryId).isSuccess());

    }

    @Test
    public void test_addPostSuperAddPostFails() throws Exception {
        Long queryId = 1L;
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build(), null, null, null);

        when(queryRepositoryMock.findOne(queryId)).thenReturn(null);

        assertTrue(service.addPost(post, queryId).isFailure());
    }

    @Test
    public void test_addPostNoQueryToAddPostTo() throws Exception {
        Long queryId = 1L;
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build(), null, null, null);

        when(queryRepositoryMock.findOne(queryId)).thenReturn(null);

        assertTrue(service.addPost(post, queryId).isFailure());
    }

    @Test
    public void test_addPostNoFinanceContact() throws Exception {
        Long queryId = 1L;
        User user = newUser().withId(33L).withRoles(singleton(RoleBuilder.newRole(UserRoleType.PROJECT_FINANCE).build())).build();
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build(), null, null, null);
        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null);

        when(queryRepositoryMock.findOne(queryId)).thenReturn(targetedQuery);

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

        when(projectFinanceRepositoryMock.findOne(22L)).thenReturn(pf);

        assertTrue(service.addPost(post, queryId).isSuccess());
    }

    @Test
    public void test_addPostNotificationNotSent() throws Exception {
        Long queryId = 1L;
        User user = newUser().withId(33L).withRoles(singleton(RoleBuilder.newRole(UserRoleType.PROJECT_FINANCE).build())).build();
        PostResource post = new PostResource(null, newUserResource().withId(33L).withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build(), null, null, null);
        Post mappedPost = new Post(null, user, null, null, null);
        Query targetedQuery = new Query(queryId, 22L, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, 22L, null, null, null, false, null);

        when(queryRepositoryMock.findOne(queryId)).thenReturn(targetedQuery);

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
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).withUser(u, u2).withOrganisation(o, o2).build(1);
        Application app = newApplication().withName("App1").build();
        Project p = newProject().withProjectUsers(pu).withPartnerOrganisations(newPartnerOrganisation()
                .withOrganisation(o).build(1)).withApplication(app).build();

        ProjectFinance pf = newProjectFinance().withProject(p).withOrganisation(o).build();

        NotificationTarget target = new ExternalUserNotificationTarget(u.getName(), u.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap("dashboardUrl", "http://ifs-local-dev/project-setup/project/" + p.getId(),
                "applicationName", "App1");

        Notification notification = new Notification(systemNotificationSourceMock, singletonList(target),
                FinanceCheckQueriesServiceImpl.Notifications.NEW_FINANCE_CHECK_QUERY_RESPONSE, expectedNotificationArguments);

        when(projectFinanceRepositoryMock.findOne(22L)).thenReturn(pf);
        when(notificationServiceMock.sendNotification(notification, EMAIL)).thenReturn(serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND));

        assertTrue(service.addPost(post, queryId).isFailure());
    }

}
