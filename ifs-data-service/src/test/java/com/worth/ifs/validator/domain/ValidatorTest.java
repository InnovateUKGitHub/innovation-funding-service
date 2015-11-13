package com.worth.ifs.validator.domain;

import com.worth.ifs.validator.EmailValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.validator.builder.ValidatorBuilder.newValidator;
import static org.junit.Assert.*;

public class ValidatorTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testObjectCreation(){
        List<Validator> validators = newValidator().build(5);

        Validator validator = validators.get(0);
        validator.setClassName(EmailValidator.class.getName());

    }
}