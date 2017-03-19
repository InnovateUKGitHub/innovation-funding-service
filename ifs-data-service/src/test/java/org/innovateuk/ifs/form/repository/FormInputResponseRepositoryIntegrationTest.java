package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Repository Integration tests for Form Input Responses.
 */
public class FormInputResponseRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<FormInputResponseRepository> {

    @Autowired
    private FormInputResponseRepository repository;

    @Override
    @Autowired
    protected void setRepository(FormInputResponseRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findOne() {
        FormInputResponse response = repository.findOne(1L);
        assertEquals(Long.valueOf(1), response.getId());
        assertTrue(response.getValue().startsWith("Within the Industry"));
        assertEquals(Integer.valueOf(49), response.getWordCount());
        assertEquals(Integer.valueOf(400 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());
    }

    @Test
    public void findByApplicationIdAndFormInputId() {
        // test that we can find responses to form input "1" for certain applications
        assertEquals(1, repository.findByApplicationIdAndFormInputId(1L, 1L).size());
        assertEquals(0, repository.findByApplicationIdAndFormInputId(2L, 1L).size());
        assertEquals(0, repository.findByApplicationIdAndFormInputId(3L, 1L).size());
        assertEquals(0, repository.findByApplicationIdAndFormInputId(4L, 1L).size());
        assertEquals(1, repository.findByApplicationIdAndFormInputId(5L, 1L).size());
        assertEquals(0, repository.findByApplicationIdAndFormInputId(6L, 1L).size());

        // check the details of one of the retrieved FormInputResponses
        FormInputResponse response = repository.findByApplicationIdAndFormInputId(1L, 1L).get(0);
        assertEquals(Long.valueOf(1), response.getId());
        assertTrue(response.getValue().startsWith("Within the Industry"));
        assertEquals(Integer.valueOf(49), response.getWordCount());
        assertEquals(Integer.valueOf(400 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());
    }

    @Test
    public void findByApplicationIdAndUpdateByAndFormInputId() {
        // test that we can find processrole "1"'s response to form input "1" for certain applications
        long processRoleId = 1L;
        assertNotNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(1L, processRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(2L, processRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(3L, processRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(4L, processRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(5L, processRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(6L, processRoleId, 1L));

        // check another processrole's responses to the forminputs for certain applications
        long anotherProcessRoleId = repository.findByApplicationIdAndFormInputId(5L, 1L).get(0).getUpdatedBy().getId();
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(1L, anotherProcessRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(2L, anotherProcessRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(3L, anotherProcessRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(4L, anotherProcessRoleId, 1L));
        assertNotNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(5L, anotherProcessRoleId, 1L));
        assertNull(repository.findByApplicationIdAndUpdatedByIdAndFormInputId(6L, anotherProcessRoleId, 1L));

        // check the details of one of the retrieved FormInputResponses
        FormInputResponse response = repository.findByApplicationIdAndUpdatedByIdAndFormInputId(1L, processRoleId, 1L);
        assertEquals(Long.valueOf(1), response.getId());
        assertTrue(response.getValue().startsWith("Within the Industry"));
        assertEquals(Integer.valueOf(49), response.getWordCount());
        assertEquals(Integer.valueOf(400 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());
    }

    @Test
    public void findByUpdatedById() {
        // test that we can find processrole "1"'s responses to any applications
        long processRoleId = 1L;
        List<FormInputResponse> responses = repository.findByUpdatedById(processRoleId);
        assertEquals(16, responses.size());

        // check the details of one of the retrieved FormInputResponses
        FormInputResponse response = responses.get(0);
        assertEquals(Long.valueOf(1), response.getId());
        assertTrue(response.getValue().startsWith("Within the Industry"));
        assertEquals(Integer.valueOf(49), response.getWordCount());
        assertEquals(Integer.valueOf(400 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());
    }


    @Test
    public void findOne_nonExistentInput() {
        assertEquals(null, repository.findOne(Long.MAX_VALUE));
    }

    @Test
    public void findOneByApplicationIdAndFormInputQuestionName() {
        FormInputResponse response = repository.findOneByApplicationIdAndFormInputQuestionName(1L, "Project Summary");
        assertEquals(15L, response.getId().longValue());
        assertTrue(response.getValue().startsWith("The Project aims to identify,"));
        assertEquals(Integer.valueOf(90), response.getWordCount());
        assertEquals(Integer.valueOf(400 - 90), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("Project summary", response.getFormInput().getDescription());
    }

    @Test
    public void findOneByApplicationIdAndFormInputQuestionName_nonExistentApplication() {
        assertNull(repository.findOneByApplicationIdAndFormInputQuestionName(Long.MAX_VALUE, "Project Summary"));
    }

    @Test
    public void findOneByApplicationIdAndFormInputQuestionName_nonExistentQuestion() {
        assertNull(repository.findOneByApplicationIdAndFormInputQuestionName(1L, "Not exists"));
    }

    @Test
    public void findOneByApplicationIdAndQuestionId() {
        List<FormInputResponse> responses = repository.findByApplicationIdAndFormInputQuestionId(1L, 1L);
        assertEquals(1, responses.size());
        assertEquals(1L, (long)responses.get(0).getId());
        assertTrue(responses.get(0).getValue().startsWith("Within the Industry one issue has caused progress"));
    }
}
