package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.Disability.NO;
import static com.worth.ifs.user.resource.Gender.NOT_STATED;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class AssessorServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessorService assessorService = new AssessorServiceImpl();

    @Test
    public void registerAssessorByHashShouldCallCorrectServicesAndHaveSuccesfullAutocome() throws Exception {
        String inviteHash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withGender(NOT_STATED)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withDisability(NO)
                .withPassword("Password123")
                .build();

        Role role = newRole().build();

        RoleResource roleResource = newRoleResource().build();

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .withEmail("email@example.com")
                .build();

        when(competitionInviteServiceMock.getInvite(inviteHash)).thenReturn(serviceSuccess(competitionInviteResource));
        when(roleRepositoryMock.findOneByName(ASSESSOR.name())).thenReturn(role);
        when(roleMapperMock.mapToResource(role)).thenReturn(roleResource);

        UserResource userToCreate = createLambdaMatcher(user -> {
            assertNull(user.getId());
            assertEquals("Mr", user.getTitle());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals(NOT_STATED, user.getGender());
            assertEquals(Long.valueOf(1L), user.getEthnicity());
            assertEquals(NO, user.getDisability());
            assertEquals("email@example.com", user.getEmail());
            assertEquals(asList(roleResource), user.getRoles());

            return true;
        });

        UserResource createdUser = newUserResource().build();

        when(registrationServiceMock.createUser(userToCreate)).thenReturn(serviceSuccess(createdUser));
        when(competitionInviteServiceMock.acceptInvite(inviteHash)).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(inviteHash, userRegistrationResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionInviteServiceMock, roleRepositoryMock, registrationServiceMock);
        inOrder.verify(competitionInviteServiceMock).getInvite(inviteHash);
        inOrder.verify(roleRepositoryMock).findOneByName(ASSESSOR.name());
        inOrder.verify(registrationServiceMock).createUser(isA(UserResource.class));
        inOrder.verify(competitionInviteServiceMock).acceptInvite(inviteHash);
        inOrder.verifyNoMoreInteractions();
    }


    //TODO: create test case for failure to find invite
    //TODO: create test case for validation error on user resource content
    //TODO: create test case for to accept invite
}