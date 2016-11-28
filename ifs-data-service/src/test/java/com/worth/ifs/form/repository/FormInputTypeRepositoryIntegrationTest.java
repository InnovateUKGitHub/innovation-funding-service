package com.worth.ifs.form.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class FormInputTypeRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<FormInputTypeRepository> {

    @Override
    @Autowired
    protected void setRepository(FormInputTypeRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByTitle() throws Exception {
        assertEquals(Long.valueOf(1L), repository.findByTitle("textinput").getId());
    }
}