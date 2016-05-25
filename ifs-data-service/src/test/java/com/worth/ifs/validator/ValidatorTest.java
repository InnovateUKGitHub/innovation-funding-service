package com.worth.ifs.validator;

import org.springframework.validation.Validator;

public interface ValidatorTest {
    Validator getValidator();
    void testInvalid() throws Exception;
    void testValid() throws Exception;
}
