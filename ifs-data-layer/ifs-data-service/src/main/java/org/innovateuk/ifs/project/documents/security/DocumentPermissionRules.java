package org.innovateuk.ifs.project.documents.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;

@PermissionRules
@Component
public class DocumentPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "UPLOAD_DOCUMENT", description = "Project Manager can upload document for their Project")
    public boolean projectManagerCanUploadDocument(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(value = "DOWNLOAD_DOCUMENT", description = "Partner can download Document")
    public boolean partnersCanDownloadDocument(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "DOWNLOAD_DOCUMENT", description = "Internal user can download Document")
    public boolean internalUserCanDownloadDocument(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "DELETE_DOCUMENT", description = "Project Manager can delete document for their Project")
    public boolean projectManagerCanDeleteDocument(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(value = "SUBMIT_DOCUMENT", description = "Project Manager can submit document")
    public boolean projectManagerCanSubmitDocument(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(value = "APPROVE_DOCUMENT", description = "Comp admin or project finance users can approve or reject document")
    public boolean internalAdminCanApproveDocument(ProjectResource project, UserResource user) {
        return isInternalAdmin(user);
    }
}

