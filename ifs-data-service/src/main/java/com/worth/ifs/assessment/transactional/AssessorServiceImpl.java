package com.worth.ifs.assessment.transactional;


import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessorServiceImpl implements AssessorService {
    @Autowired
    CompetitionInviteService competitionInviteService;

    @Autowired
    RegistrationService userRegistrationService;

    @Override
    public ServiceResult<UserResource> registerAssessorByHash(String inviteHash, UserResource userResource) {
        CompetitionInviteResource invite = competitionInviteService.getInvite(inviteHash).getSuccessObject();

        //Stub call to test full stack is working
        ServiceResult<UserResource> userResourceServiceResult = userRegistrationService.createOrganisationUser(0L, userResource);

        // TODO: Actual implementation should include:
        // Retrieve invite by hash - return errors to controller if it doesn't exist
        // Create User by UserResource
        // Validate User - return errors to controller upon validation error
        // Add appropriate user Role
        // Add user to organisation?
        // Delete/disable invite

        return userResourceServiceResult;
    }
}
