package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
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
import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void registerAssessorByHashShouldCallCorrectServicesAndHaveSuccesfullAutocome() throws Exception {
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

        assert(serviceResult.isSuccess());
    }



    //TODO: create test case for failure to find invite
    //TODO: create test case for validation error on user resource content
    //TODO: create test case for to accept invite
}