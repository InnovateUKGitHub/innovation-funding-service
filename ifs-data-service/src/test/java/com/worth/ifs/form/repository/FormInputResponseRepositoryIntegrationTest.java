package com.worth.ifs.form.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Ignore
    @Test
    public void test_findByApplicationIdAndFormInputId() {

        List<FormInputResponse> responses = repository.findByApplicationIdAndFormInputId(1L, 1L);
        assertEquals(2, responses.size());
    }

    @Test
    public void test_findOne_nonExistentInput() {
        assertEquals(null, repository.findOne(Long.MAX_VALUE));
    }

}
