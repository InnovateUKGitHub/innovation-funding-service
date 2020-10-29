package org.innovateuk.ifs.project.documents.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.project.document.resource.DocumentStatus.APPROVED;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;


@PermissionRules
@Component
public class DocumentPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "UPLOAD_DOCUMENT", description = "Project Manager can upload document for their project")
    public boolean projectManagerCanUploadDocument(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(value = "DOWNLOAD_DOCUMENT", description = "Partner can download document")
    public boolean partnersCanDownloadDocument(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "DOWNLOAD_DOCUMENT", description = "Internal user can download document")
    public boolean internalUserCanDownloadDocument(ProjectResource project, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "DOWNLOAD_DOCUMENT", description = "Monitoring officer can download document")
    public boolean monitoringOfficerCanDownloadDocument(ProjectResource project, UserResource user) {
        return isMonitoringOfficer(project.getId(), user.getId());
    }

    @PermissionRule(value = "DOWNLOAD_DOCUMENT", description = "Stakeholder can download document")
    public boolean stakeholderCanDownloadDocument(ProjectResource project, UserResource user) {
        return userIsStakeholderOnProject(project, user) && areDocumentsApproved(project);
    }

    @PermissionRule(value = "DELETE_DOCUMENT", description = "Project Manager can delete document for their project")
    public boolean projectManagerCanDeleteDocument(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(value = "SUBMIT_DOCUMENT", description = "Project Manager can submit document")
    public boolean projectManagerCanSubmitDocument(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(value = "REVIEW_DOCUMENT", description = "Comp admin, project finance and IFS admin users can approve or reject document")
    public boolean internalAdminCanApproveDocument(ProjectResource project, UserResource user) {
        return isInternalAdmin(user) || isIFSAdmin(user);
    }

    private boolean userIsStakeholderOnProject(ProjectResource project, UserResource user) {
        return userIsStakeholderOnCompetitionForProject(project.getId(), user.getId());
    }

    private boolean areDocumentsApproved(ProjectResource project) {
        return project.getProjectDocuments().stream().allMatch(documents -> APPROVED.equals(documents.getStatus()));
    }
}