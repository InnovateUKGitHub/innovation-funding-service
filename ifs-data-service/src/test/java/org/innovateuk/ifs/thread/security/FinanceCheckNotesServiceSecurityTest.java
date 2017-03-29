package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.financecheck.service.FinanceCheckNotesService;
import org.innovateuk.ifs.threads.security.NoteLookupStrategy;
import org.innovateuk.ifs.threads.security.ProjectFinanceNotePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class FinanceCheckNotesServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckNotesService> {


    private ProjectFinanceNotePermissionRules noteRules;
    private NoteLookupStrategy noteLookupStrategy;

    @Override
    protected Class<? extends FinanceCheckNotesService> getClassUnderTest() {
        return TestFinanceCheckNotesService.class;
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

        assertAccessDenied(() -> classUnderTest.findOne(1L), () -> {
            verify(noteRules).onlyProjectFinanceUsersCanViewNotes(isA(NoteResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(noteRules);
        });
    }

    @Test
    public void test_findAll() throws Exception {
        setLoggedInUser(null);

        ServiceResult<List<NoteResource>> results = classUnderTest.findAll(22L);
        assertEquals(0, results.getSuccessObject().size());

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


    public static class TestFinanceCheckNotesService implements FinanceCheckNotesService {

        @Override
        public ServiceResult<List<NoteResource>> findAll(Long contextClassPk) {
            List<NoteResource> notes = new ArrayList<>();
            notes.add(findOne(2L).getSuccessObject());
            notes.add(findOne(3L).getSuccessObject());
            return ServiceResult.serviceSuccess(notes);
        }

        @Override
        public ServiceResult<NoteResource> findOne(Long id) {
            return ServiceResult.serviceSuccess(new NoteResource(id,
                    null, null, null, null));
        }

        @Override
        public ServiceResult<Long> create(NoteResource NoteResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> addPost(PostResource post, Long noteId) {
            return null;
        }
    }


}