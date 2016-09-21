package com.worth.ifs.assessment.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.mapper.RoleMapper;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class AssessorServiceImpl implements AssessorService {
    @Autowired
    CompetitionInviteService competitionInviteService;

    @Autowired
    RegistrationService userRegistrationService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleMapper roleMapper;

    @Override
    public ServiceResult<Void> registerAssessorByHash(String inviteHash, UserResource userResource) {

        // TODO: Handle failures gracefully and hand them back to the webservice

        ServiceResult<Void> result = retrieveInvite(inviteHash)
                .andOnSuccess(
                        inviteResource -> createAssessor(inviteResource, userResource)
                                .andOnSuccess(() -> acceptInvite(inviteHash))
                                .andOnSuccessReturnVoid()
                );

        return result;
    }

    private ServiceResult<Void> createAssessor(CompetitionInviteResource invite, UserResource userResource) {
        userResource.setEmail(invite.getEmail());
        userResource.setRoles(retrieveAssessorRole());
        //TODO: Retrieve and add assessor role through RoleService before account creation
        return userRegistrationService.createUser(userResource).andOnSuccessReturnVoid();
    }
    private ServiceResult<CompetitionInviteResource> retrieveInvite(String inviteHash) {
        return competitionInviteService.getInvite(inviteHash);
    }

    private ServiceResult<Void> acceptInvite(String inviteHash) {
        return competitionInviteService.acceptInvite(inviteHash);
    }

    private List<RoleResource> retrieveAssessorRole() {
        List<RoleResource> roles = new ArrayList<>();
        roles.add(getAssessorRoleResource());

        return roles;
    }

    private RoleResource getAssessorRoleResource() {
        Role role = find(roleRepository.findOneByName(UserRoleType.ASSESSOR.name()), notFoundError(Role.class, UserRoleType.ASSESSOR.name())).getSuccessObject();
        return roleMapper.mapToResource(role);
    }
}
