package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.LambdaMatcher;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

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
        when(roleRepositoryMock.findByName(APPLICANT.getName())).thenReturn(asList(applicantRole));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(success("new-uid"));

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

        User savedUser = newUser().build();

        when(userRepositoryMock.save(expectedCreatedUser)).thenReturn(savedUser);

        ServiceResult<User> result = service.createUserLeadApplicantForOrganisation(123L, userToCreate);
        assertTrue(result.isRight());
        assertEquals(savedUser, result.getRight());
    }
}
