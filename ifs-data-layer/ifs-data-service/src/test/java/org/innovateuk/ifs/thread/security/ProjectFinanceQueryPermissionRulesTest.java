package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
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
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
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
    private UserResource competitionFinanceUser;
    private UserResource partner;
    private UserResource incorrectPartner;
    private ProjectProcess projectProcessInSetup;
    private ProjectProcess projectProcessInLive;
    private ProjectProcess projectProcessInWithdrawn;
    private Competition competition;
    private ProjectFinance projectFinance;
    private Project project;

    private long projectId = 31L;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Before
    public void setUp() throws Exception {
        projectFinanceUser = projectFinanceUser();
        partner = getUserWithRole(PARTNER);
        competitionFinanceUser = competitionFinanceUser();

        queryResource = queryWithoutPosts();
        queryResource.posts.add(new PostResource(1L, projectFinanceUser, "The body", new ArrayList<>(), ZonedDateTime.now()));

        incorrectPartner = newUserResource().withId(1993L).withRolesGlobal(singletonList(PARTNER)).build();
        incorrectPartner.setId(1993L);

        competition = newCompetition().build();
        project = newProject().withId(projectId).withApplication(newApplication().withCompetition(competition).build()).build();
        projectFinance = newProjectFinance().withProject(project).build();
        projectProcessInSetup = newProjectProcess().withProject(project).withActivityState(ProjectState.SETUP).build();
        projectProcessInLive = newProjectProcess().withActivityState(ProjectState.LIVE).build();
        projectProcessInWithdrawn = newProjectProcess().withActivityState(ProjectState.WITHDRAWN).build();

        when(projectFinanceRepository.findById(anyLong())).thenReturn(Optional.of(projectFinance));
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
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, partner));
    }

//    @Test
//    public void testThatOnlyCompetitionFinanceProjectFinanceUsersCanCreateQueries() {

//        AttachmentResource attachmentResource = new AttachmentResource(41L, "Attachment", "Email", 200L, ZonedDateTime.now());
//        PostResource post = new PostResource(51L, competitionFinanceUser, "Body", singletonList(attachmentResource), ZonedDateTime.now());
//        QueryResource queryResource1 = new QueryResource(29L, anyLong(), singletonList(post), FinanceChecksSectionType.ELIGIBILITY,"title", false, ZonedDateTime.now(), competitionFinanceUser, ZonedDateTime.now());

//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
//        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), competitionFinanceUser.getId())).thenReturn(true);
//        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
//
//        assertTrue(rules.competitionFinanceUsersCanCreateQueries(queryResource, competitionFinanceUser));
//        assertFalse(rules.competitionFinanceUsersCanCreateQueries(queryResource, partner));
//    }

    @Test
    public void testThatProjectFinanceUsersCannotCreateQueriesWhenProjectIsInLive() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInLive);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
    }

    @Test
    public void testThatProjectFinanceUsersCannotCreateQueriesWhenProjectIsInWithdrawn() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInWithdrawn);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
    }

    @Test
    public void testThatNewQueryMustContainInitialPost() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryWithoutPosts(), partner));
    }

    @Test
    public void testThatNewQueryInitialPostAuthorMustBeTheCurrentUser() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        UserResource anotherProjectFinanceUser = newUserResource().withId(675L)
                .withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build();
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, anotherProjectFinanceUser));
    }

    @Test
    public void testThatFirstPostMustComeFromTheProjectFinanceUser() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        QueryResource queryWithoutPosts = queryWithoutPosts();
        assertTrue(rules.projectFinanceUsersCanAddPostToTheirQueries(queryWithoutPosts, projectFinanceUser));
        when(projectFinanceRepository.findById(queryWithoutPosts.contextClassPk))
                .thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(partner)));
        assertFalse(rules.projectFinanceUsersCanAddPostToTheirQueries(queryWithoutPosts, partner));
    }

    @Test
    public void testThatOnlyTheProjectFinanceUserOrTheCorrectFinanceContactCanReplyToAQuery() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        when(projectFinanceRepository.findById(queryResource.contextClassPk))
                .thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(partner)));
        assertTrue(rules.projectFinanceUsersCanAddPostToTheirQueries(queryResource, projectFinanceUser));
        assertTrue(rules.projectPartnersCanAddPostToTheirQueries(queryResource, partner));
        assertFalse(rules.projectPartnersCanAddPostToTheirQueries(queryResource, incorrectPartner));
    }

    @Test
    public void testThatOnlyProjectFinanceUsersOrProjectUsersCanViewTheirQueries() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.projectFinanceUsersCanViewQueries(queryResource, projectFinanceUser));
        when(projectFinanceRepository.findById(queryResource.contextClassPk))
                .thenReturn(Optional.of(projectFinanceWithUserAsFinanceContact(partner)));
        assertTrue(rules.projectPartnersCanViewQueries(queryResource, partner));
        assertFalse(rules.projectPartnersCanViewQueries(queryResource, incorrectPartner));
    }
}