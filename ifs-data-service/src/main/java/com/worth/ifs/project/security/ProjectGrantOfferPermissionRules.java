package com.worth.ifs.project.security;

import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;


@PermissionRules
@Component
public class ProjectGrantOfferPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "DOWNLOAD_GRANT_OFFER",
            description = "Partners can download grant offer documents (Unsigned grant offer, signed rant offer, Additional contract)")
    public boolean partnersCanDownloadGrantOfferLetter(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "DOWNLOAD_GRANT_OFFER",
            description = "Comp Admin can download grant offer documents (Unsigned grant offer, signed rant offer, Additional contract)")
    public boolean compAdminCanDownloadGrantOfferLetter(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }


    @PermissionRule(
            value = "VIEW_GRANT_OFFER",
            description = "Partners can view grant offer documents (Unsigned grant offer, signed rant offer, Additional contract)")
    public boolean partnersCanViewGrantOfferLetter(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER",
            description = "Comp Admin can view grant offer documents (Unsigned grant offer, signed rant offer, Additional contract)")
    public boolean compAdminCanViewGrantOfferLetter(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
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

}
