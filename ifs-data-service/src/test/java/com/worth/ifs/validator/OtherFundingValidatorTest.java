package com.worth.ifs.validator;

import com.worth.ifs.finance.resource.cost.OtherFunding;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OtherFundingValidatorTest extends AbstractValidatorTest {

    @Override
    public Validator getValidator() {
        return new OtherFundingValidator();
    }

    @Test
    @Override
    public void testInvalid() throws Exception {
        OtherFunding otherFunding2 = new OtherFunding(2L, "Yes", "Source1", "", new BigDecimal(100));
        BindingResult bindingResult2 = getBindingResult(otherFunding2);
        getValidator().validate(otherFunding2, bindingResult2);
        assertTrue(bindingResult2.hasErrors());

        OtherFunding otherFunding3 = new OtherFunding(3L, "Yes", "Source1", "2342", new BigDecimal(100));
        BindingResult bindingResult3 = getBindingResult(otherFunding3);
        getValidator().validate(otherFunding3, bindingResult3);
        assertTrue(bindingResult3.hasErrors());

        OtherFunding otherFunding4 = new OtherFunding(4L, "Yes", "Source1", "2014", new BigDecimal(100));
        BindingResult bindingResult4 = getBindingResult(otherFunding4);
        getValidator().validate(otherFunding4, bindingResult4);
        assertTrue(bindingResult4.hasErrors());
    }

    @Test
    @Override
    public void testValid() throws Exception {
        OtherFunding otherFunding = new OtherFunding(1L, "Yes", "Source1", "10-2014", new BigDecimal(100));
        BindingResult bindingResult = getBindingResult(otherFunding);
        getValidator().validate(otherFunding, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}
