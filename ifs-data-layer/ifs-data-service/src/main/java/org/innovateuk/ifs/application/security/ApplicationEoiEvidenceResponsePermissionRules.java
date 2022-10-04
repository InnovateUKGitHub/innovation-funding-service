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

    @PermissionRule(value = "EOI_EVIDENCE_SUBMITTED_NOTIFICATION", description = "A lead organisation member can send the notification of eoi evidence submission")
    public boolean isLeadOrganisationMemberCanSendApplicationSubmittedNotification(final ApplicationResource applicationResource, final UserResource user) {
        return checkHasAnyApplicantParticipantRole(applicationResource.getId(), applicationResource.getLeadOrganisationId(), user);
    }

    @PermissionRule(value = "CREATE_EOI_EVIDENCE_FILE_ENTRY", description = "Lead applicant can create file entry and eoi evidence resource.")
    public boolean applicantCanCreateFileEntryAndEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        return checkHasAnyApplicantParticipantRole(applicationResource.getId(), applicationResource.getLeadOrganisationId(), user);
    }

    @PermissionRule(value = "SUBMIT_EOI_EVIDENCE", description = "Lead applicant can submit the eoi evidence.")
    public boolean applicantCanSubmitEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        return checkHasAnyApplicantParticipantRole(applicationResource.getId(), applicationResource.getLeadOrganisationId(), user);
    }

    @PermissionRule(value = "REMOVE_EOI_EVIDENCE", description = "Lead applicant can remove the eoi evidence.")
    public boolean applicantCanRemoveEoiEvidence(ApplicationResource applicationResource, UserResource user) {
        return checkHasAnyApplicantParticipantRole(applicationResource.getId(), applicationResource.getLeadOrganisationId(), user);
    }

    @PermissionRule(value = "GET_EVIDENCE_FILE_CONTENTS", description = "Lead applicant can view the eoi evidence file.")
    public boolean applicantCanViewEvidenceFileContents(ApplicationResource applicationResource, UserResource user) {
        return checkHasAnyApplicantParticipantRole(applicationResource.getId(), applicationResource.getLeadOrganisationId(), user);
    }

    @PermissionRule(value = "GET_EVIDENCE_FILE_DETAILS", description = "Lead applicant can get eoi evidence file details.")
    public boolean applicantCanGetEvidenceFileEntryDetails(ApplicationResource applicationResource, UserResource user) {
        return checkHasAnyApplicantParticipantRole(applicationResource.getId(), applicationResource.getLeadOrganisationId(), user);
    }
}
