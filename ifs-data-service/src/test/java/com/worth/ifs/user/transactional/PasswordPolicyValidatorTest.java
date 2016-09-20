package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.PasswordPolicyValidator.ExclusionRulePatternGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.regex.Pattern;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.transactional.PasswordPolicyValidator.PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME;
import static com.worth.ifs.user.transactional.PasswordPolicyValidator.PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Testing the password policies that are used in addition to the standard password validation
 * supplied by the Shib REST API
 */
public class PasswordPolicyValidatorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private PasswordPolicyValidator validator = new PasswordPolicyValidator();

    private ExclusionRulePatternGenerator lettersForNumbersGenerator;

    @Before
    public void setupValidator() {
        validator.postConstruct();
        lettersForNumbersGenerator = (ExclusionRulePatternGenerator) ReflectionTestUtils.getField(validator, "lettersForNumbersGenerator");
    }

    @Test
    public void testLettersForNumbersGeneratorGeneratesPatternWithInterchangeableLettersAndNumbers() {

        List<Pattern> patterns = lettersForNumbersGenerator.apply("01234567890 abcdefghijklmnopQRSTUVWXYZ");
        assertEquals(1, patterns.size());
        Pattern pattern = patterns.get(0);

        assertEquals("01234567890 [a4][b8]cd[e3]fgh[i1]jk[l1]mn[o0]pqr[s5]tuvwxy[z2]", pattern.toString());
        assertTrue(pattern.matcher("01234567890 abcdefghijklmnopqrstuvwxyz").matches());
        assertTrue(pattern.matcher("01234567890 4bcd3fghijklmn0pqrstuvwxyz").matches());
    }

    @Test
    public void testLettersForNumbersGeneratorGeneratesPatternWithInterchangeableLettersAndNumbersIsCaseInsensitive() {

        List<Pattern> patterns = lettersForNumbersGenerator.apply("Hi");
        assertEquals(1, patterns.size());
        Pattern pattern = patterns.get(0);

        assertEquals("h[i1]", pattern.toString());
        assertTrue(pattern.matcher("Hi").matches());
        assertTrue(pattern.matcher("hi").matches());
        assertTrue(pattern.matcher("H1").matches());
    }

    @Test
    public void testLettersForNumbersGeneratorGeneratesPatternWithInterchangeableLettersAndNumbersWithRealWorldExample() {

        List<Pattern> patterns = lettersForNumbersGenerator.apply("Bob Smith");
        assertEquals(1, patterns.size());
        Pattern pattern = patterns.get(0);

        assertEquals("[b8][o0][b8] [s5]m[i1]th", pattern.toString());
        assertTrue(pattern.matcher("Bob Smith").matches());
        assertTrue(pattern.matcher("B08 5mith").matches());
        assertTrue(pattern.matcher("8ob 5MITH").matches());

        assertFalse(pattern.matcher("Not a match").matches());
        assertFalse(pattern.matcher("8ob 5MITHY").matches());
    }

    @Test
    public void testValidatePasswordStopsUserUsingFullName() {

        UserResource user = newUserResource().withFirstName("Bobby").withLastName("Smith").build();
        ServiceResult<Void> result = validator.validatePassword("B0bbySmith", user);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));

        // try some different permutations of full name
        assertTrue(validator.validatePassword("Bobby Smith", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("B0bby sm1th", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("sm1thB0bBy ", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("sst5m1th  B0bBYdef", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));

        // try some success cases
        assertTrue(validator.validatePassword("Something different", user).isSuccess());
        assertTrue(validator.validatePassword("Babby Smoth", user).isSuccess());
    }

    @Test
    public void testValidatePasswordStopsUserUsingFullNameButNotWhenVeryShort() {

        UserResource user = newUserResource().withFirstName("Jo").withLastName("Om").build();
        assertTrue(validator.validatePassword("jolomo 123", user).isSuccess());
        assertTrue(validator.validatePassword("Joomla", user).isSuccess());
    }

    @Test
    public void testValidatePasswordStopsUserUsingFirstName() {

        UserResource user = newUserResource().withFirstName("William").withLastName("Shatner").build();

        // assert that the user gets the appropriate error back indicating they used first name
        assertTrue(validator.validatePassword("William", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("w1LLiaM", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("123w1LLiaM456", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));

        // try a success case
        assertTrue(validator.validatePassword("w1LLster", user).isSuccess());
    }

    @Test
    public void testValidatePasswordStopsUserUsingFirstNameButNotWhenVeryShort() {

        UserResource user = newUserResource().withFirstName("Jo").withLastName("Shatner").build();
        assertTrue(validator.validatePassword("joomla", user).isSuccess());
    }

    @Test
    public void testValidatePasswordStopsUserUsingLastName() {

        UserResource user = newUserResource().withFirstName("William").withLastName("Shatner").build();

        // assert that the user gets the appropriate error back indicating they used last name
        assertTrue(validator.validatePassword("Shatner", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("sh4tner", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("123sh4tner456", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
    }

    @Test
    public void testValidatePasswordStopsUserUsingLastNameButNotWhenVeryShort() {

        UserResource user = newUserResource().withFirstName("William").withLastName("Lo").build();
        assertTrue(validator.validatePassword("highlow", user).isSuccess());
    }

    @Test
    public void testValidatePasswordStopsUserUsingOrganisationName() {

        UserResource user = newUserResource().withFirstName("Steve").withLastName("Smith").
                withOrganisations(asList(123L, 456L)).build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(newOrganisation().withName("Empire Ltd").build());
        when(organisationRepositoryMock.findOne(456L)).thenReturn(newOrganisation().withName("EGGS").build());

        // assert that the user gets the appropriate error back indicating they used Organisation name
        assertTrue(validator.validatePassword("Empire Ltd", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("EGGS", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("123Empire Ltd456", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("456eggs456", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("456eggsempire ltd456", user).getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
    }

    @Test
    public void testValidatePasswordStopsUserUsingOrganisationNameButNotWhenVeryShort() {

        UserResource user = newUserResource().withFirstName("William").withLastName("Lo").
                withOrganisations(asList(123L, 456L)).build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(newOrganisation().withName("Hi").build());
        when(organisationRepositoryMock.findOne(456L)).thenReturn(newOrganisation().withName("lo").build());

        assertTrue(validator.validatePassword("highlow", user).isSuccess());
    }
}
