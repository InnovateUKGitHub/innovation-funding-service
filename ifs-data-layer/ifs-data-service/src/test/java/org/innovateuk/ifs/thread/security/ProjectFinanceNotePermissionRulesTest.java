package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectProcess;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.security.ProjectFinanceNotePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class ProjectFinanceNotePermissionRulesTest extends BasePermissionRulesTest<ProjectFinanceNotePermissionRules> {
    private NoteResource noteResource;
    private UserResource projectFinanceUserOne;
    private UserResource projectFinanceUserTwo;
    private UserResource intruder;
    private Project project;
    private ProjectFinance projectFinance;
    private ProjectProcess projectProcessInSetup;
    private ProjectProcess projectProcessInLive;
    private ProjectProcess projectProcessInWithdrawn;

    @Before
    public void setUp() throws Exception {
        projectFinanceUserOne = projectFinanceUser();
        projectFinanceUserTwo = newUserResource().withId(1993L).withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build();
        intruder = getUserWithRole(FINANCE_CONTACT);
        noteResource = sampleNote();

        project = newProject().build();
        projectFinance = newProjectFinance().withProject(project).build();
        projectProcessInSetup = newProjectProcess().withActivityState(new ActivityState(PROJECT_SETUP, ProjectState.SETUP.getBackingState())).build();
        projectProcessInLive = newProjectProcess().withActivityState(new ActivityState(PROJECT_SETUP, ProjectState.LIVE.getBackingState())).build();
        projectProcessInWithdrawn = newProjectProcess().withActivityState(new ActivityState(PROJECT_SETUP, ProjectState.WITHDRAWN.getBackingState())).build();

        when(projectFinanceRepositoryMock.findOne(anyLong())).thenReturn(projectFinance);
    }

    private NoteResource sampleNote() {
        return sampleNote(asList(new PostResource(null, projectFinanceUserOne, null, null, null)));
    }

    private NoteResource sampleNoteWithoutPosts() {
        return sampleNote(null);
    }

    private NoteResource sampleNote(List<PostResource> posts) {
        return noteResource = new NoteResource(3L, 22L, posts,null, null);
    }

    @Override
    protected ProjectFinanceNotePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinanceNotePermissionRules();
    }

    @Test
    public void testThatOnlyProjectFinanceProjectFinanceUsersCanCreateNotes() throws Exception {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, intruder));
    }

    public void testThatProjectFinanceUsersCannotCreateNotesWhenProjectIsInLive() throws Exception {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInLive);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
    }

    public void testThatProjectFinanceUsersCannotCreateNotesWhenProjectIsInWithdrawn() throws Exception {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInWithdrawn);
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
    }

    @Test
    public void testThatNoteCreationRequiresTheInitialPost() throws Exception {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(sampleNoteWithoutPosts(), projectFinanceUserOne));
    }

    @Test
    public void testThatNoteCreationRequiresTheInitialPostAuthorToBeTheCurrentUser() throws Exception {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserTwo));
    }

    @Test
    public void testThatOnlyProjectFinanceUserCanAddPostsToANote() throws Exception {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, projectFinanceUserOne));
        assertTrue(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, projectFinanceUserTwo));
        assertFalse(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, intruder));
    }

    @Test
    public void testThatOnlyProjectFinanceUsersViewNotes() {
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcessInSetup);
        assertTrue(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, projectFinanceUserOne));
        assertTrue(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, projectFinanceUserTwo));
        assertFalse(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, intruder));
    }

}
