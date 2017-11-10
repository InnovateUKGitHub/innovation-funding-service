package org.innovateuk.ifs.project.grantofferletter.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

@PermissionRules
@Component
public class GrantOfferLetterPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "DOWNLOAD_GRANT_OFFER",
            description = "Partners can download grant offer documents (Unsigned grant offer, signed grant offer, Additional contract)")
    public boolean partnersCanDownloadGrantOfferLetter(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "DOWNLOAD_GRANT_OFFER",
            description = "Competitions team & Project Finance can download grant offer documents (Unsigned grant offer, signed grant offer, Additional contract)")
    public boolean internalUsersCanDownloadGrantOfferLetter(ProjectResource project, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(
            value = "DOWNLOAD_GRANT_OFFER",
            description = "Support users can download grant offer documents (Unsigned grant offer, signed grant offer, Additional contract)")
    public boolean supportUsersCanDownloadGrantOfferLetter(ProjectResource project, UserResource user) {
        return isSupport(user);
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER",
            description = "Partners can view grant offer documents (Unsigned grant offer, signed grant offer, Additional contract)")

    public boolean partnersCanViewGrantOfferLetter(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER",
            description = "Competitions team & Project Finance can view grant offer documents (Unsigned grant offer, signed grant offer, Additional contract)")
    public boolean internalUsersCanViewGrantOfferLetter(ProjectResource project, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER",
            description = "Support users can view grant offer documents (Unsigned grant offer, signed grant offer, Additional contract)")
    public boolean supportUsersCanViewGrantOfferLetter(ProjectResource project, UserResource user) {
        return isSupport(user);
    }

    @PermissionRule(
            value = "UPLOAD_SIGNED_GRANT_OFFER",
            description = "Project manager or Lead partner can upload signed grant offer letter")
    public boolean leadPartnerCanUploadGrantOfferLetter(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "UPLOAD_SIGNED_GRANT_OFFER",
            description = "Project manager or Lead partner can upload signed grant offer letter")
    public boolean projectManagerCanUploadGrantOfferLetter(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "DELETE_SIGNED_GRANT_OFFER",
            description = "Lead partner can delete signed grant offer letter")
    public boolean leadPartnerCanDeleteSignedGrantOfferLetter(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "SUBMIT_GRANT_OFFER_LETTER",
            description = "Project manager can submit the grant offer letter")
    public boolean projectManagerSubmitGrantOfferLetter(Long projectId, UserResource user) {
        return isProjectManager(projectId, user.getId());
    }

    @PermissionRule(
            value = "APPROVE_SIGNED_GRANT_OFFER",
            description = "Competitions team & Project Finance can approve signed grant offer letter")
    public boolean internalUsersCanApproveSignedGrantOfferLetter(Long projectId, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(
            value = "SEND_GRANT_OFFER_LETTER",
            description = "Internal users can send the Grant Offer Letter notification")
    public boolean internalUserCanSendGrantOfferLetter(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "APPROVE_SIGNED_GRANT_OFFER_LETTER",
            description = "Internal users can approve the signed Grant Offer Letter")
    public boolean internalUsersCanApproveSignedGrantOfferLetter(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER_LETTER_SEND_STATUS",
            description = "Internal users can view the send status of Grant Offer Letter for a project")
    public boolean internalAdminUserCanViewSendGrantOfferLetterStatus(ProjectResource project, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER_LETTER_SEND_STATUS",
            description = "Support users can view the send status of Grant Offer Letter for a project")
    public boolean supportUserCanViewSendGrantOfferLetterStatus(ProjectResource project, UserResource user) {
        return isSupport(user);
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER_LETTER_SEND_STATUS",
            description = "Partners can view the send status of Grant Offer Letter for a project")
    public boolean externalUserCanViewSendGrantOfferLetterStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "VIEW_SIGNED_GRANT_OFFER_LETTER_APPROVED_STATUS", description = "A user can see grant offer approval status that they are partners on")
    public boolean partnersOnProjectCanViewGrantOfferApprovedStatus(ProjectResource project, UserResource user) {
        return project != null && isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "VIEW_SIGNED_GRANT_OFFER_LETTER_APPROVED_STATUS", description = "Internal users can see grant offer approval status")
    public boolean internalUsersCanViewGrantOfferApprovedStatus(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

}
