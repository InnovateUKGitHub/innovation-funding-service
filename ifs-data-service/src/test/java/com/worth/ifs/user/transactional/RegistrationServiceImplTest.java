package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.LambdaMatcher;
import com.worth.ifs.authentication.service.RestIdentityProviderService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.domain.TokenType;
import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.CompAdminEmailBuilder.newCompAdminEmail;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static com.worth.ifs.user.domain.UserRoleType.COMP_ADMIN;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Tests around Registration Service
 */
public class RegistrationServiceImplTest extends BaseServiceUnitTest<RegistrationServiceImpl> {

    @Override
    protected RegistrationServiceImpl supplyServiceUnderTest() {
        return new RegistrationServiceImpl();
    }

    @Test
    public void testCreateUserLeadApplicantForOrganisation() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        Role applicantRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findByName(APPLICANT.getName())).thenReturn(singletonList(applicantRole));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));

        LambdaMatcher<User> expectedUserMatcher = lambdaMatches(user -> {

            assertNull(user.getId());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());

            assertEquals("email@example.com", user.getEmail());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("Mr", user.getTitle());
            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(applicantRole, user.getRoles().get(0));
            assertEquals(1, user.getOrganisations().size());
            assertEquals(selectedOrganisation, user.getOrganisations().get(0));

            return true;
        });

        User expectedCreatedUser = argThat(expectedUserMatcher);

        User savedUser = newUser().with(id(999L)).build();

        when(userRepositoryMock.save(expectedCreatedUser)).thenReturn(savedUser);

        Token expectedToken = argThat(lambdaMatches(token -> {
            assertEquals(TokenType.VERIFY_EMAIL_ADDRESS, token.getType());
            assertEquals(User.class.getName(), token.getClassName());
            assertEquals(savedUser.getId(), token.getClassPk());
            assertFalse(token.getHash().isEmpty());
            return true;
        }));

        when(tokenRepositoryMock.save(expectedToken)).thenReturn(expectedToken);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);
        ServiceResult<Void> result = service.createApplicantUser(123L, userToCreate);
        assertTrue(result.isSuccess());
        verify(tokenRepositoryMock).save(isA(Token.class));
    }

    @Test
    public void testCreateUserLeadApplicantForOrganisationButOrganisationNotFound() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(null);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);

        ServiceResult<Void> result = service.createApplicantUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Organisation.class, 123L)));
    }

    @Test
    public void testCreateUserLeadApplicantForOrganisationButRoleNotFound() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findByName(APPLICANT.getName())).thenReturn(emptyList());
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);

        ServiceResult<Void> result = service.createApplicantUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Role.class, APPLICANT.getName())));
    }

    @Test
    public void testCreateUserLeadApplicantForOrganisationButIdpCallFails() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        Role applicantRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findByName(APPLICANT.getName())).thenReturn(singletonList(applicantRole));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);

        ServiceResult<Void> result = service.createApplicantUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void testCreateCompAdminUserForOrganisation() {
        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        Role compAdminRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findByName(COMP_ADMIN.getName())).thenReturn(singletonList(compAdminRole));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));

        LambdaMatcher<User> expectedUserMatcher = lambdaMatches(user -> {

            assertNull(user.getId());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());

            assertEquals("email@example.com", user.getEmail());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("Mr", user.getTitle());
            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(compAdminRole, user.getRoles().get(0));
            assertEquals(1, user.getOrganisations().size());
            assertEquals(selectedOrganisation, user.getOrganisations().get(0));

            return true;
        });

        User expectedCreatedUser = argThat(expectedUserMatcher);

        User savedUser = newUser().with(id(999L)).build();

        when(userRepositoryMock.save(expectedCreatedUser)).thenReturn(savedUser);

        Token expectedToken = argThat(lambdaMatches(token -> {
            assertEquals(TokenType.VERIFY_EMAIL_ADDRESS, token.getType());
            assertEquals(User.class.getName(), token.getClassName());
            assertEquals(savedUser.getId(), token.getClassPk());
            assertFalse(token.getHash().isEmpty());
            return true;
        }));

        CompAdminEmail compAdminEmail = newCompAdminEmail().withEmail("email@example.com").build();

        when(tokenRepositoryMock.save(expectedToken)).thenReturn(expectedToken);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(compAdminEmail);
        ServiceResult<Void> result = service.createApplicantUser(123L, userToCreate);
        assertTrue(result.isSuccess());

        verify(tokenRepositoryMock).save(isA(Token.class));
    }

    @Test
    public void testCreateCompAdminUserForOrganisationButRoleNotFound() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        CompAdminEmail compAdminEmail = newCompAdminEmail().withEmail("email@example.com").build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findByName(COMP_ADMIN.getName())).thenReturn(emptyList());
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(compAdminEmail);

        ServiceResult<Void> result = service.createApplicantUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Role.class, COMP_ADMIN.getName())));
    }
}
