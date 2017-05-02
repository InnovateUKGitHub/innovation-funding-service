package org.innovateuk.ifs.shibboleth.api.models.validators;

import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.mocks.Mocked;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.innovateuk.ifs.shibboleth.api.services.FindIdentityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class EmailValidatorTest extends Mocked {

    @InjectMocks
    protected EmailValidator validator;

    @Mock
    private FindIdentityService findIdentityService;


    @Before
    public void before() {
        reset(findIdentityService);
    }


    @Test
    public void shouldAcceptValidEmails() throws DuplicateEmailException {

        when(findIdentityService.findByEmail(eq("valid@email.com"))).thenReturn(Optional.empty());

        validator.validate("valid@email.com");

        verify(findIdentityService).findByEmail(eq("valid@email.com"));
        verifyNoMoreInteractions(findIdentityService);
    }


    @Test
    public void shouldRejectDuplicateEmails() {

        when(findIdentityService.findByEmail(eq("duplicate@email.com"))).thenReturn(Optional.of(new Identity()));

        try {
            validator.validate("duplicate@email.com");

            assertThat("Service failed to throw expected exception.", false);
        } catch (final DuplicateEmailException exception) {
            assertThat(exception.toErrorResponse().getKey(), is(equalTo("DUPLICATE_EMAIL_ADDRESS")));
            assertThat(exception.toErrorResponse().getArguments(), is(emptyCollectionOf(String.class)));
        }

        verify(findIdentityService).findByEmail(eq("duplicate@email.com"));
        verifyNoMoreInteractions(findIdentityService);
    }
}
