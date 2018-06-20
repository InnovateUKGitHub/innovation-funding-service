package org.innovateuk.ifs.commons.validation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberValidatorTest {

    final String sevenDigitPhoneNumber = "1111222+-";
    final String eightDigitPhoneNumber = "11112222+-";
    final String twentyDigitPhoneNumber = "11111222223333344444+-";
    final String twentyOneDigitPhoneNumber = "111112222233333444441+-";
    final String validPhoneNumberWithBracket = "(+44)02072343456";
    final String invalidPhoneNumberWithBracket = "(+44)02072";
    final String validPhoneNumberLengthWithInvalidCharacters = "11112222<>[]";

    @Test
    public void validPhoneNumberRegex() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertEquals("^[\\\\)\\\\(\\\\+\\s-]*(?:\\d[\\\\)\\\\(\\\\+\\s-]*){8,20}$", tester.pattern.pattern());
    }

    @Test
    public void validPhoneNumberWithEightDigits() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertTrue("Eight digit phonenumber", tester.validate(eightDigitPhoneNumber));
    }

    @Test
    public void invalidPhoneNumberWithSevenDigits() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertFalse("Seven digit phonenumber", tester.validate(sevenDigitPhoneNumber));
    }

    @Test
    public void validPhoneNumberWithTwentyDigits() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertTrue("Twenty digit phonenumber", tester.validate(twentyDigitPhoneNumber));
    }

    @Test
    public void invalidPhoneNumberWithTwentyOneDigits() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertFalse("Twenty one digit phonenumber", tester.validate(twentyOneDigitPhoneNumber));
    }

    @Test
    public void validPhoneNumberWithBrackets() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertTrue("Valid phone number - Brackets are not included in the count.", tester.validate(validPhoneNumberWithBracket));
    }

    @Test
    public void invalidPhoneNumberWithBrackets() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertFalse("Invalid phone number - Brackets are not included in the count.", tester.validate(invalidPhoneNumberWithBracket));
    }

    @Test
    public void validPhoneNumberLengthWithInvalidCharacters() {
        PhoneNumberValidator tester = new PhoneNumberValidator();
        assertFalse("Valid phone number length, invalid characters included", tester.validate(validPhoneNumberLengthWithInvalidCharacters));

    }
}