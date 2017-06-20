package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.PasswordPolicyValidator.ExclusionRulePatternGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.transactional.PasswordPolicyValidator.PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME;
import static org.innovateuk.ifs.user.transactional.PasswordPolicyValidator.PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME;
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
        assertTrue(validator.validatePassword("Bobby Smith", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("B0bby sm1th", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("sm1thB0bBy ", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("sst5m1th  B0bBYdef", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));

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
        assertTrue(validator.validatePassword("William", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("w1LLiaM", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("123w1LLiaM456", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));

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
        assertTrue(validator.validatePassword("Shatner", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("sh4tner", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
        assertTrue(validator.validatePassword("123sh4tner456", user).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_FIRST_OR_LAST_NAME)));
    }

    @Test
    public void testValidatePasswordStopsUserUsingLastNameButNotWhenVeryShort() {

        UserResource user = newUserResource().withFirstName("William").withLastName("Lo").build();
        assertTrue(validator.validatePassword("highlow", user).isSuccess());
    }

    @Test
    public void testValidatePasswordStopsUserUsingOrganisationName() {

        User user = newUser().
                withId(13L).
                withFirstName("Steve").
                withLastName("Smith").
                build();
        Organisation org1 = newOrganisation().withId(123L).withName("Empire Ltd").build();
        Organisation org2 = newOrganisation().withId(456L).withName("EGGS").build();
        org1.addUser(user);
        org2.addUser(user);

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(organisationRepositoryMock.findByUsersId(user.getId())).thenReturn(asList(org1, org2));

        UserResource userResource = newUserResource().
                withId(13L).
                withFirstName("Steve").
                withLastName("Smith").
                build();

        // assert that the user gets the appropriate error back indicating they used Organisation name
        assertTrue(validator.validatePassword("Empire Ltd", userResource).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("EGGS", userResource).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("123Empire Ltd456", userResource).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("456eggs456", userResource).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
        assertTrue(validator.validatePassword("456eggsempire ltd456", userResource).
                getFailure().is(badRequestError(PASSWORD_MUST_NOT_CONTAIN_ORGANISATION_NAME)));
    }

    @Test
    public void testValidatePasswordUsingSpecialCharactersInOrganisationName() {

        User user = newUser().
                withId(13L).
                withFirstName("Steve").
                withLastName("Smith").
                build();
        Organisation org1 = newOrganisation().withId(123L).withName("(((£&").build();
        org1.addUser(user);

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(organisationRepositoryMock.findByUsersId(user.getId())).thenReturn(asList(org1));

        UserResource userResource = newUserResource().
                withId(13L).
                withFirstName("Steve").
                withLastName("Smith").
                build();
        assertTrue(validator.validatePassword("jenny", userResource).isSuccess());
    }

    @Test
    public void testValidatePasswordStopsUserUsingOrganisationNameButNotWhenVeryShort() {

        User user = newUser().withId(13L).withFirstName("William").withLastName("Lo").build();
        Organisation org1 = newOrganisation().withId(123L).withName("Hi").build();
        Organisation org2 = newOrganisation().withId(456L).withName("lo").build();
        org1.addUser(user);
        org2.addUser(user);

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(organisationRepositoryMock.findByUsersId(user.getId())).thenReturn(asList(org1, org2));

        UserResource userResource = newUserResource().
                withId(13L).
                withFirstName("William").
                withLastName("Lo").
                build();

        assertTrue(validator.validatePassword("highlow", userResource).isSuccess());
    }
}
