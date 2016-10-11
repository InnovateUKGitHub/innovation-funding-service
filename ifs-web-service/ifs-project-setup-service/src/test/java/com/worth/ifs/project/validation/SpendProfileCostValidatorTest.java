package com.worth.ifs.project.validation;

import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpendProfileCostValidatorTest {

    private Validator validator;

    private SpendProfileTableResource table;

    private BindingResult bindingResult;

    @Before
    public void setUp() {
        validator = new SpendProfileCostValidator();

        table = new SpendProfileTableResource();

        bindingResult = new DataBinder(table).getBindingResult();
    }


    @Test
    public void testWhenCostsAreFractional() {

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.44"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50.10"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("10.31"))));


        validator.validate(table, bindingResult);

        assertTrue(bindingResult.hasErrors());

        List<ObjectError> errors = bindingResult.getAllErrors();

        assertExpectedErrors(errors, "Cost cannot contain fractional part. Category: 1, Month#: 1");
        assertExpectedErrors(errors, "Cost cannot contain fractional part. Category: 2, Month#: 2");
        assertExpectedErrors(errors, "Cost cannot contain fractional part. Category: 3, Month#: 3");

    }

    @Test
    public void testWhenCostsAreLessThanZero() {

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("0"), new BigDecimal("00"), new BigDecimal("-1")),
                2L, asList(new BigDecimal("70"), new BigDecimal("-2"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("1"), new BigDecimal("-33"))));


        validator.validate(table, bindingResult);

        assertTrue(bindingResult.hasErrors());

        List<ObjectError> errors = bindingResult.getAllErrors();

        assertExpectedErrors(errors, "Cost cannot be less than zero. Category: 1, Month#: 3");
        assertExpectedErrors(errors, "Cost cannot be less than zero. Category: 2, Month#: 2");
        assertExpectedErrors(errors, "Cost cannot be less than zero. Category: 3, Month#: 3");
    }

    @Test
    public void testWhenCostsAreGreaterThanOrEqualToMillion() {

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("1000000"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("999999"), new BigDecimal("1000001"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("2000000"), new BigDecimal("10"))));


        validator.validate(table, bindingResult);

        assertTrue(bindingResult.hasErrors());

        List<ObjectError> errors = bindingResult.getAllErrors();

        assertExpectedErrors(errors, "Cost cannot be million or more. Category: 1, Month#: 1");
        assertExpectedErrors(errors, "Cost cannot be million or more. Category: 2, Month#: 2");
        assertExpectedErrors(errors, "Cost cannot be million or more. Category: 3, Month#: 2");
    }

    @Test
    public void testWhenCostsAreFractionalLessThanZeroOrGreaterThanMillion() {

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.12"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("-30"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("1000001"))));


        validator.validate(table, bindingResult);

        assertTrue(bindingResult.hasErrors());

        List<ObjectError> errors = bindingResult.getAllErrors();

        assertExpectedErrors(errors, "Cost cannot contain fractional part. Category: 1, Month#: 1");
        assertExpectedErrors(errors, "Cost cannot be less than zero. Category: 2, Month#: 2");
        assertExpectedErrors(errors, "Cost cannot be million or more. Category: 3, Month#: 3");
    }

    @Test
    public void successWhenAllCostsAreCorrect() {

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("30"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("100"))));


        validator.validate(table, bindingResult);

        assertFalse(bindingResult.hasErrors());

    }


    private void assertExpectedErrors(List<ObjectError> errors, String messageToExpect) {

        Assert.assertTrue(errors.stream().anyMatch(objectError -> objectError.getDefaultMessage().equalsIgnoreCase(messageToExpect)));

    }

}
