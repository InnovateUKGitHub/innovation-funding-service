package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.stereotype.Component;


@PermissionRules
@Component
public class ProjectGrantOfferPermissionRules extends BasePermissionRules {

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
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE);
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
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE);
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
            value = "SUBMIT_GRANT_OFFER_LETTER",
            description = "Project manager can submit the grant offer letter")
    public boolean projectManagerSubmitGrantOfferLetter(Long projectId, UserResource user) {
        return isProjectManager(projectId, user.getId());
    }

    @PermissionRule(
            value = "SEND_GRANT_OFFER_LETTER",
            description = "Contracts team can send the grant offer letter")
    public boolean contractsTeamSendGrantOfferLetter(Long projectId, UserResource user) {
        return user.hasRole(UserRoleType.COMP_ADMIN);
    }

    @PermissionRule(
            value = "APPROVE_SIGNED_GRANT_OFFER",
            description = "Competitions team & Project Finance can approve signed grant offer letter")
    public boolean internalUsersCanApproveSignedGrantOfferLetter(Long projectId, UserResource user) {
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE);
    }

}
