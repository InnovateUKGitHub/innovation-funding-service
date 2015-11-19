package com.worth.ifs.form.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.validator.EmailValidator;
import com.worth.ifs.validator.NotEmptyValidator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * Repository Integration tests for Form Inputs.
 */
public class FormValidatorRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<FormValidatorRepository> {

    @Autowired
    private FormValidatorRepository repository;

    @Override
    @Autowired
    protected void setRepository(FormValidatorRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findByIdEmailValidator() {
        Long id= 1L;
        FormValidator validator = repository.findById(id);
        assertEquals(Long.valueOf(id), validator.getId());
        assertEquals(EmailValidator.class.getName(), validator.getClazzName());
        try {
            assertEquals(EmailValidator.class, validator.getClazz());
        } catch (ClassNotFoundException e) {
            assertFalse("ClassNotFoundException " + validator.getClazzName(), true);
        }
    }

    @Test
    public void test_findByIdNotEmptyValidator() {
        Long id= 2L;
        FormValidator validator = repository.findById(id);
        assertEquals(Long.valueOf(id), validator.getId());
        assertEquals(NotEmptyValidator.class.getName(), validator.getClazzName());
        try {
            assertEquals(NotEmptyValidator.class, validator.getClazz());
        } catch (ClassNotFoundException e) {
            assertFalse("ClassNotFoundException " + validator.getClazzName(), true);
        }
    }


    @Test
    public void test_findById_nonExistentInput() {
        assertEquals(null, repository.findById(Long.MAX_VALUE));
    }


}
