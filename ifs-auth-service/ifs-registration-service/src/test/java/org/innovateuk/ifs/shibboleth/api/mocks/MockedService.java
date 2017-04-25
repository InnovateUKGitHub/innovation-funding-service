package org.innovateuk.ifs.shibboleth.api.mocks;

import org.innovateuk.ifs.shibboleth.api.PasswordPolicyProperties;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;
import org.innovateuk.ifs.shibboleth.api.models.ErrorResponse;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.innovateuk.ifs.shibboleth.api.services.IdentityServices;
import org.junit.After;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ldap.core.LdapTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public abstract class MockedService<A> extends Mocked {

    private final List<String> passwordBlacklist;

    @InjectMocks
    protected IdentityServices service;

    @Mock
    protected LdapTemplate ldapTemplate;

    @Mock
    protected PasswordPolicyProperties passwordPolicy;


    protected MockedService() {
        this.passwordBlacklist = Arrays.asList("blacklisted-password", "test", "password", "qwerty", "1234567890");
    }


    @Before
    public void before() {
        reset(ldapTemplate, passwordPolicy);

        when(passwordPolicy.getBlacklist()).thenReturn(passwordBlacklist);
    }


    @After
    public void after() {
        verifyNoMoreInteractions(ldapTemplate, passwordPolicy);
    }


    protected void setupFindingIdentityByEmail(final String email) {
        setupFindingIdentityByEmail(new Identity(UUID.randomUUID(), email, "", false));
    }


    protected void setupFindingIdentityByEmail(final Identity identity) {
        when(ldapTemplate.find(any(), eq(Identity.class))).thenReturn(Collections.singletonList(identity));
    }


    protected void setupFindingIdentityByUuid(final UUID uuid, final String email, final String password) {
        when(ldapTemplate.findOne(any(), eq(Identity.class))).thenReturn(new Identity(uuid, email, password, false));
    }


    protected void verifyFindingIdentityByUuid() {
        verify(ldapTemplate).findOne(any(), eq(Identity.class));
    }


    protected void verifyFindingIdentityByEmail() {
        verify(ldapTemplate).find(any(), eq(Identity.class));
    }


    protected void verifyBlacklistedPassworsLookedUp() {
        verify(passwordPolicy).getBlacklist();
    }


    protected void assertThatExceptionIsInvalidPasswordOfType(final Exception exception, final String type) {

        assertThat(exception, is(instanceOf(InvalidPasswordException.class)));

        if (exception instanceof InvalidPasswordException) {

            final ErrorResponse errorResponse = ((InvalidPasswordException) exception).toErrorResponse();

            assertThat(errorResponse.getKey(), is(equalTo("INVALID_PASSWORD")));
            assertThat(errorResponse.getArguments(), contains(equalTo(type)));

        } else {

            assertThat("Assertion of instance failed.", false);
        }

    }


    @SuppressWarnings("unchecked")
    public A getService() {
        return (A) service;
    }
}
