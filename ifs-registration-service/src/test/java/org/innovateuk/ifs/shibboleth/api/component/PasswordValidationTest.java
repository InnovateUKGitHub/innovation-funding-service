package org.innovateuk.ifs.shibboleth.api.component;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.shibboleth.api.models.NewIdentity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
public class PasswordValidationTest {

    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    @Test
    public void testValidPassword() {
        assertPasswordViolations("aCtU4lGoodP4$$worD", ImmutableSet.of());
    }

    @Test
    public void testNoNumberPassword() {
        assertPasswordViolations("pAssWordThEFg", ImmutableSet.of(
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_NUMBER"
        ));
    }

    @Test
    public void testLongPassword() {
        assertPasswordViolations("paSSsword123456789!!kljdfsjkdfsjkldfsdfjklsdfsjkldfsjkl", ImmutableSet.of(
                "PASSWORD_CANNOT_BE_SO_LONG"
        ));
    }

    @Test
    public void testNoUppercasePassword() {
        assertPasswordViolations("password123456789", ImmutableSet.of(
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_UPPER_CASE_LETTER"
        ));
    }

    @Test
    public void testNoLowercasePassword() {
        assertPasswordViolations("PASSWORD123456789", ImmutableSet.of(
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_LOWER_CASE_LETTER"
        ));
    }

    @Test
    public void testBlankPassword() {
        assertPasswordViolations("", ImmutableSet.of(
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_UPPER_CASE_LETTER",
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_NUMBER",
                "PASSWORD_CANNOT_BE_SO_SHORT",
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_LOWER_CASE_LETTER",
                "PASSWORD_MUST_NOT_BE_BLANK"
        ));
    }

    @Test
    public void testShortPassword() {
        assertPasswordViolations("short", ImmutableSet.of(
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_UPPER_CASE_LETTER",
                "PASSWORD_MUST_CONTAIN_AT_LEAST_ONE_NUMBER",
                "PASSWORD_CANNOT_BE_SO_SHORT"
        ));
    }

    private void assertPasswordViolations(String password, Set<String> expected) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<NewIdentity>> violations = validator.validate(new NewIdentity("test@test.com", password));
        assertThat(violations.size(), equalTo(expected.size()));
        assertThat(
            Sets.symmetricDifference(
                    violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet()),
                    expected).size(), equalTo(0));
    }

}
