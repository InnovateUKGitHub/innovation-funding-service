package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.noteResourceListType;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class ProjectFinanceNotesRestServiceTest extends BaseRestServiceUnitTest<ProjectFinanceNotesRestService> {
    private final static String serviceURL = "/project/finance/notes";

    @Override
    protected ProjectFinanceNotesRestService registerRestServiceUnderTest() {
        return new ProjectFinanceNotesRestService();
    }

    private NoteResource noteWithId(Long id) {
        return new NoteResource(id, null, null, null, null);
    }

    @Test
    public void test_findAll() throws Exception {
        final List<NoteResource> expected = asList(noteWithId(33L), noteWithId(92L));
        setupGetWithRestResultExpectations(serviceURL + "/all/22", noteResourceListType(), expected, OK);
        final List<NoteResource> response = service.findAll(22L).getSuccess();
        assertSame(expected, response);
    }

    @Test
    public void test_findOne() throws Exception {
        final NoteResource note1 = noteWithId(33L);
        setupGetWithRestResultExpectations(serviceURL + "/33", NoteResource.class, note1, OK);
        final NoteResource response = service.findOne(33L).getSuccess();
        assertSame(note1, response);
    }

    @Test
    public void test_create() throws Exception {
        final NoteResource note1 = noteWithId(33L);
        setupPostWithRestResultExpectations(serviceURL, Long.class, note1, 33L, CREATED);
        final Long response = service.create(note1).getSuccess();
        assertSame(note1.id, response);
    }

}