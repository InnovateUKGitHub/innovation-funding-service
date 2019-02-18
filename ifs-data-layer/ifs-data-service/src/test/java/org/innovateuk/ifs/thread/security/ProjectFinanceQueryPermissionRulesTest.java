package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.thread.security.ProjectFinanceThreadsTestData.projectFinanceWithUserAsFinanceContact;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ProjectFinanceQueryPermissionRulesTest extends BasePermissionRulesTest<ProjectFinanceQueryPermissionRules> {
    private QueryResource queryResource;
    private UserResource projectFinanceUser;
    private UserResource partner;
    private UserResource incorrectPartner;
    private Project project;
    private ProjectFinance projectFinance;
    private ProjectProcess projectProcessInSetup;
    private ProjectProcess projectProcessInLive;
    private ProjectProcess projectProcessInWithdrawn;

    @Mock
    private ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    private ProjectProcessRepository projectProcessRepositoryMock;

    @Before
    public void setUp() throws Exception {
        projectFinanceUser = projectFinanceUser();
        partner = getUserWithRole(PARTNER);

        queryResource = queryWithoutPosts();
        queryResource.posts.add(new PostResource(1L, projectFinanceUser, "The body", new ArrayList<>(), ZonedDateTime.now()));

        incorrectPartner = newUserResource().withId(1993L).withRolesGlobal(singletonList(PARTNER)).build();
        incorrectPartner.setId(1993L);

        project = newProject().build();
        projectFinance = newProjectFinance().withProject(project).build();
        projectProcessInSetup = newProjectProcess().withActivityState(ProjectState.SETUP).build();
        projectProcessInLive = newProjectProcess().withActivityState(ProjectState.LIVE).build();
        projectProcessInWithdrawn = newProjectProcess().withActivityState(ProjectState.WITHDRAWN).build();

        when(projectFinanceRepositoryMock.findById(anyLong())).thenReturn(Optional.of(projectFinance));
    }

    private QueryResource queryWithoutPosts() {
        return new QueryResource(1L, 22L, new ArrayList<>(),
                FinanceChecksSectionType.VIABILITY, "First Query", true, ZonedDateTime.now(), null, null);
    }

    @Override
    protected ProjectFinanceQueryPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinanceQueryPermissionRules();
    }

    @Test
    public void testThatOnlyProjectFinanceProjectFinanceUsersCanCreateQueries() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, partner));
    }

    @Test
    public void testThatProjectFinanceUsersCannotCreateQueriesWhenProjectIsInLive() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInLive);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
    }

    @Test
    public void testThatProjectFinanceUsersCannotCreateQueriesWhenProjectIsInWithdrawn() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInWithdrawn);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
    }

    @Test
    public void testThatNewQueryMustContainInitialPost() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryWithoutPosts(), partner));
    }

    @Test
    public void testThatNewQueryInitialPostAuthorMustBeTheCurrentUser() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        UserResource anotherProjectFinanceUser = newUserResource().withId(675L)
                .withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build();
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, anotherProjectFinanceUser));
    }

    @Test
    public void testThatFirstPostMustComeFromTheProjectFinanceUser() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        QueryResource queryWithoutPosts = queryWithoutPosts();
        assertTrue(rules.projectFinanceUsersCanAddPostToTheirQueries(queryWithoutPosts, projectFinanceUser));
        when(projectFinanceRepositoryMock.findById(queryWithoutPosts.contextClassPk))
                .thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(partner)));
        assertFalse(rules.projectFinanceUsersCanAddPostToTheirQueries(queryWithoutPosts, partner));
    }

    @Test
    public void testThatOnlyTheProjectFinanceUserOrTheCorrectFinanceContactCanReplyToAQuery() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        when(projectFinanceRepositoryMock.findById(queryResource.contextClassPk))
                .thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(partner)));
        assertTrue(rules.projectFinanceUsersCanAddPostToTheirQueries(queryResource, projectFinanceUser));
        assertTrue(rules.projectPartnersCanAddPostToTheirQueries(queryResource, partner));
        assertFalse(rules.projectPartnersCanAddPostToTheirQueries(queryResource, incorrectPartner));
    }

    @Test
    public void testThatOnlyProjectFinanceUsersOrProjectUsersCanViewTheirQueries() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.projectFinanceUsersCanViewQueries(queryResource, projectFinanceUser));
        when(projectFinanceRepositoryMock.findById(queryResource.contextClassPk))
                .thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(partner)));
        assertTrue(rules.projectPartnersCanViewQueries(queryResource, partner));
        assertFalse(rules.projectPartnersCanViewQueries(queryResource, incorrectPartner));
    }
}