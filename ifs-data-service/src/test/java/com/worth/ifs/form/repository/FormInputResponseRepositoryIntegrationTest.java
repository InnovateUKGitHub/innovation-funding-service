package com.worth.ifs.form.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Repository Integration tests for Form Input Responses.
 */
public class FormInputResponseRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<FormInputResponseRepository> {

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private FormInputResponseRepository repository;

    @Override
    @Autowired
    protected void setRepository(FormInputResponseRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findOne() {

        FormInputResponse response = repository.findOne(1L);
        assertEquals(Long.valueOf(1), response.getId());
        assertTrue(response.getValue().startsWith("Within the Industry"));
        assertEquals(Integer.valueOf(49), response.getWordCount());
        assertEquals(Integer.valueOf(500 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());
    }

    @Test
    public void test_findByApplicationIdAndFormInputId() {

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
        assertEquals(Integer.valueOf(500 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());
    }

    @Test
    public void test_findByApplicationIdAndUpdateByAndFormInputId() {

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
        assertEquals(Integer.valueOf(500 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());
    }

    @Test
    public void test_findByUpdateBy() {

        // test that we can find processrole "1"'s responses to any applications
        long processRoleId = 1L;
        List<FormInputResponse> responses = repository.findByUpdatedById(processRoleId);
        assertEquals(16, responses.size());

        // check the details of one of the retrieved FormInputResponses
        FormInputResponse response = responses.get(0);
        assertEquals(Long.valueOf(1), response.getId());
        assertTrue(response.getValue().startsWith("Within the Industry"));
        assertEquals(Integer.valueOf(49), response.getWordCount());
        assertEquals(Integer.valueOf(500 - 49), response.getWordCountLeft());
        assertEquals(18, response.getUpdateDate().getDayOfMonth());
        assertEquals(9, response.getUpdateDate().getMonthValue());
        assertEquals(2015, response.getUpdateDate().getYear());
        assertEquals("steve.smith@empire.com", response.getUpdatedBy().getUser().getEmail());
        assertEquals("1. What is the business opportunity that your project addresses?", response.getFormInput().getDescription());

    }


    @Test
    public void test_findOne_nonExistentInput() {
        assertEquals(null, repository.findOne(Long.MAX_VALUE));
    }

}
