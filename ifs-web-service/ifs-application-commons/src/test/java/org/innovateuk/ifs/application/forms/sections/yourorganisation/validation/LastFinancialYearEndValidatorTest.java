package org.innovateuk.ifs.application.forms.sections.yourorganisation.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.constraints.LastFinancialYearEnd;
import org.junit.Before;
import org.junit.Test;

public class LastFinancialYearEndValidatorTest {

    private final String messageNotNull = "should be not null";
    private final String messagePastYearMonth = "should be in the past";
    private final String messagePositiveYearMonth = "should be positive";
    private Validator validator;
    private LastFinancialYearEndForm lastFinancialYearEndForm;
    private Set<ConstraintViolation<LastFinancialYearEndForm>> violations;
    private Iterator<ConstraintViolation<LastFinancialYearEndForm>> iter;


    class LastFinancialYearEndForm {

        @LastFinancialYearEnd(messageNotNull = messageNotNull,
            messagePastYearMonth = messagePastYearMonth,
            messagePositiveYearMonth = messagePositiveYearMonth)
        private YearMonth financialYearEnd;

        public YearMonth getFinancialYearEnd() {
            return financialYearEnd;
        }

        public void setFinancialYearEnd(YearMonth financialYearEnd) {
            this.financialYearEnd = financialYearEnd;
        }
    }

    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        lastFinancialYearEndForm = new LastFinancialYearEndValidatorTest.LastFinancialYearEndForm();
    }

    @Test
    public void futureDateIsInvalid() {
        YearMonth nextMonth = YearMonth.now().plus(1, ChronoUnit.MONTHS);

        lastFinancialYearEndForm.setFinancialYearEnd(nextMonth);
        Set<ConstraintViolation<LastFinancialYearEndValidatorTest.LastFinancialYearEndForm>> violations = validator.validate(lastFinancialYearEndForm);
        iter = violations.iterator();

        assertFalse(violations.isEmpty());
        assertEquals(messagePastYearMonth, violations.iterator().next().getMessageTemplate());
    }

    @Test
    public void currentDateIsInvalid() {
        YearMonth today = YearMonth.now();

        lastFinancialYearEndForm.setFinancialYearEnd(today);
        violations = validator.validate(lastFinancialYearEndForm);
        iter = violations.iterator();

        assertFalse(violations.isEmpty());
        assertEquals(messagePastYearMonth, violations.iterator().next().getMessageTemplate());
    }

    @Test
    public void pastDateIsValid() {
        YearMonth pastDate = YearMonth.now().minus(1, ChronoUnit.MONTHS);

        lastFinancialYearEndForm.setFinancialYearEnd(pastDate);
        violations = validator.validate(lastFinancialYearEndForm);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void nullDateIsInvalid() {
        YearMonth nullDate = null;

        lastFinancialYearEndForm.setFinancialYearEnd(nullDate);
        violations = validator.validate(lastFinancialYearEndForm);
        iter = violations.iterator();

        assertFalse(violations.isEmpty());
        assertEquals(messageNotNull, iter.next().getMessageTemplate());
    }
}
