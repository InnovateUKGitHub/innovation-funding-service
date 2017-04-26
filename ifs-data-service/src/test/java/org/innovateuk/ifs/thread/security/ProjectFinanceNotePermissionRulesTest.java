package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.threads.security.ProjectFinanceNotePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.*;

public class ProjectFinanceNotePermissionRulesTest extends BasePermissionRulesTest<ProjectFinanceNotePermissionRules> {
    private NoteResource noteResource;
    private UserResource projectFinanceUserOne;
    private UserResource projectFinanceUserTwo;
    private UserResource intruder;

    @Before
    public void setUp() throws Exception {
        projectFinanceUserOne = projectFinanceUser();
        projectFinanceUserTwo = newUserResource().withId(1993L).withRolesGlobal(newRoleResource()
                .withType(PROJECT_FINANCE).build(1)).build();
        intruder = getUserWithRole(FINANCE_CONTACT);
        noteResource = sampleNote();
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
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, intruder));
    }

    @Test
    public void testThatNoteCreationRequiresTheInitialPost() throws Exception {
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(sampleNoteWithoutPosts(), projectFinanceUserOne));
    }

    @Test
    public void testThatNoteCreationRequiresTheInitialPostAuthorToBeTheCurrentUser() throws Exception {
        assertTrue(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserOne));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(noteResource, projectFinanceUserTwo));
    }

    @Test
    public void testThatOnlyProjectFinanceUserCanAddPostsToANote() throws Exception {
        assertTrue(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, projectFinanceUserOne));
        assertTrue(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, projectFinanceUserTwo));
        assertFalse(rules.onlyProjectFinanceUsersCanAddPosts(noteResource, intruder));
    }

    @Test
    public void testThatOnlyProjectFinanceUsersViewNotes() {
        assertTrue(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, projectFinanceUserOne));
        assertTrue(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, projectFinanceUserTwo));
        assertFalse(rules.onlyProjectFinanceUsersCanViewNotes(noteResource, intruder));
    }

}
