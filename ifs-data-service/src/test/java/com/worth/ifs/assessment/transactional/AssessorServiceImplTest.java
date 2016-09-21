package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.mapper.RoleMapper;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.transactional.RegistrationService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class AssessorServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessorService assessorService = new AssessorServiceImpl();

    @Mock
    private RegistrationService userRegistrationService;

    @Mock
    private CompetitionInviteService competitionInviteService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @Test
    public void registerAssessorByHash_callCorrectServicesAndHaveSuccessfulAutocome() throws Exception {
        UserResource userResource = newUserResource().build();
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().build();
        String hash = "testhash";
        Role role = newRole().build();
        RoleResource roleResource = newRoleResource().build();

        ServiceResult<UserResource> userResourceRestResult = ServiceResult.serviceSuccess(userResource);
        ServiceResult<CompetitionInviteResource> inviteResult = ServiceResult.serviceSuccess(competitionInviteResource);
        ServiceResult<Void> acceptedInviteResult = ServiceResult.serviceSuccess();


        when(userRegistrationService.createUser(userResource)).thenReturn(userResourceRestResult);
        when(competitionInviteService.getInvite(hash)).thenReturn(inviteResult);
        when(competitionInviteService.acceptInvite(hash)).thenReturn(acceptedInviteResult);
        when(roleRepository.findOneByName(UserRoleType.ASSESSOR.name())).thenReturn(role);
        when(roleMapper.mapToResource(role)).thenReturn(roleResource);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userResource);

        verify(userRegistrationService, times(1)).createUser(userResource);
        verify(competitionInviteService, times(1)).getInvite(hash);
        verify(competitionInviteService, times(1)).acceptInvite(hash);

        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void registerAssessorByHash_inviteDoesNotExistResultsInFailureAndSkippingUserRegistrationAndInviteAcceptance() throws Exception {
        UserResource userResource = newUserResource().build();
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().build();
        String hash = "testhash";

        Error notFoundError = new Error(GENERAL_NOT_FOUND, "invite not found", "", NOT_FOUND);

        ServiceResult<UserResource> userResourceRestResult = ServiceResult.serviceSuccess(userResource);
        ServiceResult<CompetitionInviteResource> inviteResult = ServiceResult.serviceFailure(notFoundError);

        when(userRegistrationService.createUser(userResource)).thenReturn(userResourceRestResult);
        when(competitionInviteService.getInvite(hash)).thenReturn(inviteResult);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userResource);

        verify(userRegistrationService, times(0)).createUser(userResource);
        verify(competitionInviteService, times(1)).getInvite(hash);
        verify(competitionInviteService, times(0)).acceptInvite(hash);

        assertTrue(serviceResult.isFailure());
        assertEquals(serviceResult.getErrors().get(0),notFoundError);
    }

    @Test
    public void registerAssessorByHash_unopenedInviteCannotBeAcceptedResultsInServiceFailure() throws Exception {
        UserResource userResource = newUserResource().build();
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().build();
        String hash = "testhash";
        Role role = newRole().build();
        RoleResource roleResource = newRoleResource().build();

        Error notFoundError = new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "invite already accepted", "", BAD_REQUEST);

        ServiceResult<UserResource> userResourceRestResult = ServiceResult.serviceSuccess(userResource);
        ServiceResult<CompetitionInviteResource> inviteResult = ServiceResult.serviceSuccess(competitionInviteResource);
        ServiceResult<Void> acceptedInviteResult = ServiceResult.serviceFailure(notFoundError);

        when(userRegistrationService.createUser(userResource)).thenReturn(userResourceRestResult);
        when(competitionInviteService.getInvite(hash)).thenReturn(inviteResult);
        when(competitionInviteService.acceptInvite(hash)).thenReturn(acceptedInviteResult);
        when(roleRepository.findOneByName(UserRoleType.ASSESSOR.name())).thenReturn(role);
        when(roleMapper.mapToResource(role)).thenReturn(roleResource);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userResource);

        verify(userRegistrationService, times(1)).createUser(userResource);
        verify(competitionInviteService, times(1)).getInvite(hash);
        verify(competitionInviteService, times(1)).acceptInvite(hash);

        assertTrue(serviceResult.isFailure());
        assertEquals(serviceResult.getErrors().get(0),notFoundError);
    }

    @Test
    public void registerAssessorByHash_userValidationFailureResultsInFailureAndNotAcceptingInvite() throws Exception {
        UserResource userResource = newUserResource().build();
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().build();
        String hash = "testhash";
        Role role = newRole().build();
        RoleResource roleResource = newRoleResource().build();

        Error notFoundError = new Error(GENERAL_UNEXPECTED_ERROR, "unexpected error", "", BAD_REQUEST);

        ServiceResult<UserResource> userResourceRestResult = ServiceResult.serviceFailure(notFoundError);
        ServiceResult<CompetitionInviteResource> inviteResult = ServiceResult.serviceSuccess(competitionInviteResource);
        ServiceResult<Void> acceptedInviteResult = ServiceResult.serviceSuccess();

        when(userRegistrationService.createUser(userResource)).thenReturn(userResourceRestResult);
        when(competitionInviteService.getInvite(hash)).thenReturn(inviteResult);
        when(competitionInviteService.acceptInvite(hash)).thenReturn(acceptedInviteResult);
        when(roleRepository.findOneByName(UserRoleType.ASSESSOR.name())).thenReturn(role);
        when(roleMapper.mapToResource(role)).thenReturn(roleResource);

        ServiceResult<Void> serviceResult = assessorService.registerAssessorByHash(hash, userResource);

        verify(userRegistrationService, times(1)).createUser(userResource);
        verify(competitionInviteService, times(1)).getInvite(hash);
        verify(competitionInviteService, times(0)).acceptInvite(hash);

        assertTrue(serviceResult.isFailure());
        assertEquals(serviceResult.getErrors().get(0),notFoundError);
    }
}