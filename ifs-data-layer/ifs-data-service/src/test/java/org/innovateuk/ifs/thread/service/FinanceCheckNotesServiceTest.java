package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.notes.service.FinanceCheckNotesServiceImpl;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FinanceCheckNotesServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FinanceCheckNotesServiceImpl service;

    @Mock
    private NoteRepository noteRepositoryMock;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private PostMapper postMapper;

    @Test
    public void test_findOne() throws Exception {
        Note note = new Note(null, null, null, null, null);
        NoteResource noteResource = new NoteResource(note.id(), null, null, null, null);
        when(noteRepositoryMock.findOne(note.id())).thenReturn(note);
        when(noteMapper.mapToResource(note)).thenReturn(noteResource);

        NoteResource response = service.findOne(note.id()).getSuccess();

        assertEquals(noteResource, response);
    }

    @Test
    public void test_findAll() throws Exception {
        Long contextId = 22L;
        Note note1 = new Note(null, null, null, null, null);
        Note note2 = new Note(null, null, null, null, null);
        List<Note> notes = asList(note1, note2);

        NoteResource noteResource1 = new NoteResource(1L, null, null, null,
                null);
        NoteResource noteResource2 = new NoteResource(2L, null, null, null,
                null);
        List<NoteResource> noteResources = asList(noteResource1, noteResource2);

        when(noteRepositoryMock.findAllByClassPkAndClassName(contextId, ProjectFinance.class.getName())).thenReturn(notes);
        when(noteMapper.mapToResource(note1)).thenReturn(noteResource1);
        when(noteMapper.mapToResource(note2)).thenReturn(noteResource2);

        List<NoteResource> response = service.findAll(contextId).getSuccess();

        assertEquals(noteResources, response);
    }


    @Test
    public void test_create() throws Exception {
        NoteResource noteToCreate = new NoteResource(null, 22L, null, null, null);
        Note noteToCreateAsDomain = new Note(22L, ProjectFinance.class.getName(), null, null, null);
        when(noteMapper.mapToDomain(noteToCreate)).thenReturn(noteToCreateAsDomain);

        Note savedNote = new Note(22L, ProjectFinance.class.getName(), null, null, null);
        when(noteRepositoryMock.save(noteToCreateAsDomain)).thenReturn(savedNote);

        NoteResource createdNote = new NoteResource(1L, 22L, null, null, null);
        when(noteMapper.mapToResource(savedNote)).thenReturn(createdNote);


        Long result = service.create(noteToCreate).getSuccess();

        assertEquals(result, noteToCreate.id);
    }

    @Test
    public void test_addPost() throws Exception {
        PostResource post = new PostResource(null, newUserResource().withId(33L).build(), null, null, null);
        Post mappedPost = new Post(newUser().withId(33L).build(), null, null, null);
        Note targetedNote = new Note(null, null, null, null, null);
        when(noteRepositoryMock.findOne(targetedNote.id())).thenReturn(targetedNote);

        when(postMapper.mapToDomain(post)).thenReturn(mappedPost);

        assertTrue(service.addPost(post, targetedNote.id()).isSuccess());
    }


}
