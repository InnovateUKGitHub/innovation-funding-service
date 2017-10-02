package org.innovateuk.ifs.shibboleth.api.models.validators;

import org.innovateuk.ifs.shibboleth.api.PasswordPolicyProperties;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;
import org.innovateuk.ifs.shibboleth.api.mocks.Mocked;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class PasswordValidatorTest extends Mocked {

    @InjectMocks
    protected PasswordValidator validator;

    @Mock
    private PasswordPolicyProperties passwordPolicy;

    private final List<String> blacklist = Arrays.asList("invalid-password", "test");


    @Before
    public void before() {
        reset(passwordPolicy);
        when(passwordPolicy.getBlacklist()).thenReturn(blacklist);
    }


    @Test
    public void shouldAcceptValidPasswords() throws InvalidPasswordException {

        validator.validate("valid-password");

    }


    @Test
    public void shouldRejectDuplicateEmails() {
        try {
            validator.validate("invalid-password");

            assertThat("Service failed to throw expected exception.", false);
        } catch (final InvalidPasswordException exception) {
            assertThat(exception.toErrorResponse().getKey(), is(equalTo("INVALID_PASSWORD")));
            assertThat(exception.toErrorResponse().getArguments(), hasItem(equalTo("blacklisted")));
        }
    }


    @After
    public void after() {
        verify(passwordPolicy).getBlacklist();
        verifyNoMoreInteractions(passwordPolicy);
    }
}
