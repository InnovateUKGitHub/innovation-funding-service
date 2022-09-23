package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationEoiEvidenceResponsePermissionRules extends BasePermissionRules {

    @Autowired
    private UserMapper userMapper;

    @PermissionRule(value = "CREATE_EOI_EVIDENCE_FILE_ENTRY", description = "Lead applicant can create file entry and eoi evidence resource.")
    public boolean applicantCanCreateFileEntryAndEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        long organisationId = processRoleRepository.findByUserAndApplicationId(userMapper.mapToDomain(user), applicationResource.getId()).stream().findFirst().map(ProcessRole::getOrganisationId).get();
        return isMemberOfProjectTeamForOrganisation(applicationResource.getId(), organisationId, user);
    }

    @PermissionRule(value = "UPLOAD_EOI_EVIDENCE", description = "Lead applicant can create the eoi evidence")
    public boolean applicantCanCreateEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

    @PermissionRule(value = "REMOVE_EOI_EVIDENCE", description = "Lead applicant can remove the eoi evidence")
    public boolean applicantCanRemoveEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

}
