package com.worth.ifs.commons.validation;

import com.worth.ifs.commons.validation.constraints.WordCount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.worth.ifs.util.StringFunctions.countWords;

/**
 * A validator that asserts that a required string contains less than or equal to a maximum number of allowed words.
 */
public class WordCountValidator implements ConstraintValidator<WordCount, String> {

    private WordCount constraintAnnotation;

    @Override
    public void initialize(WordCount constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return countWords(value) <= constraintAnnotation.max();
    }
}