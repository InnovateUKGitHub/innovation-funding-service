package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.notes.service.FinanceCheckNotesService;
import org.innovateuk.ifs.project.notes.service.FinanceCheckNotesServiceImpl;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.security.NoteLookupStrategy;
import org.innovateuk.ifs.threads.security.ProjectFinanceNotePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class FinanceCheckNotesServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckNotesService> {


    private ProjectFinanceNotePermissionRules noteRules;
    private NoteLookupStrategy noteLookupStrategy;

    @Override
    protected Class<? extends FinanceCheckNotesService> getClassUnderTest() {
        return FinanceCheckNotesServiceImpl.class;
    }

    @Before
    public void lookupPermissionRules() {
        noteRules = getMockPermissionRulesBean(ProjectFinanceNotePermissionRules.class);
        noteLookupStrategy = getMockPermissionEntityLookupStrategiesBean(NoteLookupStrategy.class);
    }

    @Test
    public void test_create() throws Exception {
        final NoteResource noteResource = new NoteResource(null, null, null, null, null);

        assertAccessDenied(
                () -> classUnderTest.create(noteResource),
                () -> {
                    verify(noteRules).onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(isA(NoteResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(noteRules);
                });
    }

    @Test
    public void test_findOne() throws Exception {
        setLoggedInUser(null);

        when(classUnderTestMock.findOne(1L))
                .thenReturn(serviceSuccess(new NoteResource(1L, null, null, null, null)));

        assertAccessDenied(() -> classUnderTest.findOne(1L), () -> {
            verify(noteRules).onlyProjectFinanceUsersCanViewNotes(isA(NoteResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(noteRules);
        });
    }

    @Test
    public void test_findAll() throws Exception {
        setLoggedInUser(null);

        when(classUnderTestMock.findAll(22L))
                .thenReturn(serviceSuccess(new ArrayList<>(asList(
                        new NoteResource(2L, null, null, null, null),
                        new NoteResource(3L, null, null, null, null)
                ))));

        ServiceResult<List<NoteResource>> results = classUnderTest.findAll(22L);
        assertEquals(0, results.getSuccess().size());

        verify(noteRules, times(2)).onlyProjectFinanceUsersCanViewNotes(isA(NoteResource.class), isNull(UserResource.class));
        verifyNoMoreInteractions(noteRules);
    }

    @Test
    public void test_addPost() throws Exception {
        setLoggedInUser(null);
        when(noteLookupStrategy.findById(3L)).thenReturn(new NoteResource(3L, null, new ArrayList<PostResource>(),
                null, null));

        assertAccessDenied(() -> classUnderTest.addPost(isA(PostResource.class), 3L), () -> {
            verify(noteRules).onlyProjectFinanceUsersCanAddPosts(isA(NoteResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(noteRules);
        });
    }
}