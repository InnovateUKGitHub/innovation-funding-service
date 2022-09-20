package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ApplicationEoiEvidenceResponsePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "UPLOAD_EOI_EVIDENCE", description = "Lead applicant can create file entry and eoi evidence resource.")
    public boolean applicantCanCreateFileEntryAndEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

    @PermissionRule(value = "CREATE_EOI_EVIDENCE", description = "Lead applicant can create the eoi evidence")
    public boolean applicantCanCreateEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

}
