package org.innovateuk.ifs.project.otherdocuments.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@PermissionRules
@Component
public class OtherDocumentsPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "UPLOAD_OTHER_DOCUMENTS",
            description = "The lead partners can upload Other Documents (Collaboration Agreement, Exploitation Plan) for their Projects")
    public boolean leadPartnersCanUploadOtherDocuments(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "DOWNLOAD_OTHER_DOCUMENTS",
            description = "Partners can download Other Documents (Collaboration Agreement, Exploitation Plan)")
    public boolean partnersCanDownloadOtherDocuments(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "DOWNLOAD_OTHER_DOCUMENTS",
            description = "Internal users can download Other Documents (Collaboration Agreement, Exploitation Plan)")
    public boolean internalUserCanDownloadOtherDocuments(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "VIEW_OTHER_DOCUMENTS_DETAILS",
            description = "Partners can view Other Documents (Collaboration Agreement, Exploitation Plan) details that their lead partners have uploaded")
    public boolean partnersCanViewOtherDocumentsDetails(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_OTHER_DOCUMENTS_DETAILS",
            description = "Internal users can view Other Documents (Collaboration Agreement, Exploitation Plan) details that their lead partners have uploaded")
    public boolean internalUserCanViewOtherDocumentsDetails(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "DELETE_OTHER_DOCUMENTS",
            description = "The lead partners can delete Other Documents (Collaboration Agreement, Exploitation Plan) for their Projects")
    public boolean leadPartnersCanDeleteOtherDocuments(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "SUBMIT_OTHER_DOCUMENTS",
            description = "Only a Project Manager can submit completed partner documents")
    public boolean onlyProjectManagerCanMarkDocumentsAsSubmit(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "ACCEPT_REJECT_OTHER_DOCUMENTS",
            description = "Internal users can accept or reject Other Documents (Collaboration Agreement, Exploitation Plan)")
    public boolean internalUserCanAcceptOrRejectOtherDocuments(ProjectResource project, UserResource user) {
        return isInternal(user);
    }
}
