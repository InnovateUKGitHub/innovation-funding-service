package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.security.ProjectFinanceNotePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.finance.builder.NoteResourceBuilder.newNoteResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ProjectFinanceNotePermissionRulesTest extends BasePermissionRulesTest<ProjectFinanceNotePermissionRules> {
    private NoteResource noteResource;
    private UserResource projectFinanceUserOne;
    private UserResource projectFinanceUserTwo;
    private UserResource intruder;
    private ProjectProcess projectProcessInSetup;
    private ProjectProcess projectProcessInLive;
    private ProjectProcess projectProcessInWithdrawn;
    private Competition competition;
    private Project project;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Before
    public void setUp() throws Exception {
        projectFinanceUserOne = projectFinanceUser();
        projectFinanceUserTwo = newUserResource().withId(1993L).withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build();
        intruder = getUserWithRole(FINANCE_CONTACT);
        noteResource = sampleNote();

        competition = newCompetition().build();
        project = newProject().withApplication(newApplication().withCompetition(competition).build()).build();
        ProjectFinance projectFinance = newProjectFinance().withProject(project).build();
        projectProcessInSetup = newProjectProcess().withActivityState(ProjectState.SETUP).build();
        projectProcessInLive = newProjectProcess().withActivityState(ProjectState.LIVE).build();
        projectProcessInWithdrawn = newProjectProcess().withActivityState(ProjectState.WITHDRAWN).build();

        when(projectFinanceRepository.findById(anyLong())).thenReturn(Optional.of(projectFinance));
    }

    private NoteResource sampleNote() {
        return sampleNote(singletonList(new PostResource(null, projectFinanceUserOne, null, null, null)));
    }

    private NoteResource sampleNoteWithoutPosts() {
        return sampleNote(null);
    }

    private NoteResource sampleNote(List<PostResource> posts) {
        return noteResource = new NoteResource(3L, 22L, posts, null, null);
    }

    @Override
    protected ProjectFinanceNotePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinanceNotePermissionRules();
    }

    @Test
    public void thatOnlyProjectFinanceProjectFinanceUsersCanCreateNotes() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, intruder));
    }

    @Test
    public void thatProjectFinanceUsersCannotCreateNotesWhenProjectIsInLive() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInLive);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
    }

    @Test
    public void thatProjectFinanceUsersCannotCreateNotesWhenProjectIsInWithdrawn() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInWithdrawn);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
    }

    @Test
    public void thatNoteCreationRequiresTheInitialPost() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(sampleNoteWithoutPosts(), projectFinanceUserOne));
    }

    @Test
    public void thatNoteCreationRequiresTheInitialPostAuthorToBeTheCurrentUser() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserTwo));
    }

    @Test
    public void thatOnlyProjectFinanceUserCanAddPostsToANote() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, projectFinanceUserOne));
        assertTrue(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, projectFinanceUserTwo));
        assertFalse(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, intruder));
    }

    @Test
    public void thatOnlyProjectFinanceUsersViewNotes() {
        when(projectProcessRepository.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, projectFinanceUserOne));
        assertTrue(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, projectFinanceUserTwo));
        assertFalse(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, intruder));
    }

    @Test
    public void thatOnlyCompetitionFinanceUsersViewNotes() {
        UserResource userResource = competitionFinanceUser();
        NoteResource noteResource1 = newNoteResource().withContextClassPk(project.getId()).build();

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), userResource.getId())).thenReturn(true);

        assertTrue(rules.compFinanceUsersCanViewNotes(noteResource1, userResource));
        assertTrue(rules.compFinanceUsersCanViewNotes(noteResource1, userResource));
        assertFalse(rules.compFinanceUsersCanViewNotes(noteResource1, intruder));
    }
}