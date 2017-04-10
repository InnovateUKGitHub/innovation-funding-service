package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.FutureLocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

public class FutureLocalDateValidatorTest {

    class TestLocalDateForm {
        @FutureLocalDate
        private LocalDate localDate;

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }
    }

    private Validator validator;

    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testFutureDateIsValid() {
        LocalDate tomorrow = LocalDate.now().plusDays(1L);

        TestLocalDateForm futureLocalDateForm = new TestLocalDateForm();
        futureLocalDateForm.setLocalDate(tomorrow);
        Set<ConstraintViolation<TestLocalDateForm>> violations = validator.validate(futureLocalDateForm);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void testCurrentDateIsInvalid() {
        LocalDate today = LocalDate.now();

        TestLocalDateForm futureLocalDateForm = new TestLocalDateForm();
        futureLocalDateForm.setLocalDate(today);
        Set<ConstraintViolation<TestLocalDateForm>> violations = validator.validate(futureLocalDateForm);

        assertFalse(violations.isEmpty());
        assertEquals(violations.iterator().next().getMessageTemplate(), "{validation.standard.date.future}");
    }

    @Test
    public void testPastDateIsInvalid() {
        LocalDate yesterday = LocalDate.now().minusDays(1L);

        TestLocalDateForm futureLocalDateForm = new TestLocalDateForm();
        futureLocalDateForm.setLocalDate(yesterday);
        Set<ConstraintViolation<TestLocalDateForm>> violations = validator.validate(futureLocalDateForm);

        Iterator<ConstraintViolation<TestLocalDateForm>> iter = violations.iterator();
        ConstraintViolation<TestLocalDateForm> first = iter.next();

        assertTrue(!violations.isEmpty());
        assertEquals(first.getMessageTemplate(), "{validation.standard.date.future}");
    }

    @Test
    public void testMinimumLocalDateIsInvalid() {
        LocalDate minimumLocalDate = LocalDate.MIN;

        TestLocalDateForm futureLocalDateForm = new TestLocalDateForm();
        futureLocalDateForm.setLocalDate(minimumLocalDate);
        Set<ConstraintViolation<TestLocalDateForm>> violations = validator.validate(futureLocalDateForm);

        Iterator<ConstraintViolation<TestLocalDateForm>> iter = violations.iterator();
        ConstraintViolation<TestLocalDateForm> first = iter.next();

        assertTrue(!violations.isEmpty());
        assertEquals(first.getMessageTemplate(), "{validation.standard.date.future}");
    }

    @Test
    public void testNullLocalDateIsInvalid() {
        LocalDate minimumLocalDate = null;

        TestLocalDateForm futureLocalDateForm = new TestLocalDateForm();
        futureLocalDateForm.setLocalDate(minimumLocalDate);
        Set<ConstraintViolation<TestLocalDateForm>> violations = validator.validate(futureLocalDateForm);

        Iterator<ConstraintViolation<TestLocalDateForm>> iter = violations.iterator();
        ConstraintViolation<TestLocalDateForm> first = iter.next();

        assertTrue(!violations.isEmpty());
        assertEquals(first.getMessageTemplate(), "{validation.standard.date.future}");
    }
}
