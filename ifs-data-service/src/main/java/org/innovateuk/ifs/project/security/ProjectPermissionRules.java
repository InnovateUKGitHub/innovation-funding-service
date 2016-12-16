package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isCompAdmin;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

@PermissionRules
@Component
public class ProjectPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "A user can see projects that they are partners on")
    public boolean partnersOnProjectCanView(ProjectResource project, UserResource user) {
        return project != null && isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Comp admins can see project resources")
    public boolean compAdminsCanViewProjects(final ProjectResource project, final UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "Project finance users can see project resources")
    public boolean projectFinanceUsersCanViewProjects(final ProjectResource project, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "UPDATE_BASIC_PROJECT_SETUP_DETAILS",
            description = "The lead partners can update the basic project details, like start date, address, project manager")
    public boolean leadPartnersCanUpdateTheBasicProjectDetails(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }


    @PermissionRule(
            value = "UPDATE_FINANCE_CONTACT",
            description = "The lead partner can update the basic project details like start date")
    public boolean partnersCanUpdateTheirOwnOrganisationsFinanceContacts(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Comp admins can view Monitoring Officers on any Project")
    public boolean compAdminsCanViewMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Project finance managers can view Monitoring Officers on any Project")
    public boolean projectFinanceUsersCanViewMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_MONITORING_OFFICER",
            description = "Partners can view monitoring officers on Projects that they are partners on")
    public boolean partnersCanViewMonitoringOfficersOnTheirProjects(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "ASSIGN_MONITORING_OFFICER",
            description = "Comp admins can assign Monitoring Officers on any Project")
    public boolean compAdminsCanAssignMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "ASSIGN_MONITORING_OFFICER",
            description = "Project finance users can assign Monitoring Officers on any Project")
    public boolean projectFinanceUsersCanAssignMonitoringOfficersForAnyProject(ProjectResource project, UserResource user) {
        return isProjectFinanceUser(user);
    }

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
            description = "Competition Admin can download Other Documents (Collaboration Agreement, Exploitation Plan)")
    public boolean competitionAdminCanDownloadOtherDocuments(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "DOWNLOAD_OTHER_DOCUMENTS",
            description = "Project finance users can download Other Documents (Collaboration Agreement, Exploitation Plan)")
    public boolean projectFinanceUserCanDownloadOtherDocuments(ProjectResource project, UserResource user) {
        return isProjectFinanceUser(user);
    }


    @PermissionRule(
            value = "VIEW_OTHER_DOCUMENTS_DETAILS",
            description = "Partners can view Other Documents (Collaboration Agreement, Exploitation Plan) details that their lead partners have uploaded")
    public boolean partnersCanViewOtherDocumentsDetails(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_OTHER_DOCUMENTS_DETAILS",
            description = "Competitions admins can view Other Documents (Collaboration Agreement, Exploitation Plan) details that their lead partners have uploaded")
    public boolean competitionAdminCanViewOtherDocumentsDetails(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "VIEW_OTHER_DOCUMENTS_DETAILS",
            description = "Project Finance Users can view Other Documents (Collaboration Agreement, Exploitation Plan) details that their lead partners have uploaded")
    public boolean projectFinanceUserCanViewOtherDocumentsDetails(ProjectResource project, UserResource user) {
        return isProjectFinanceUser(user);
    }


    @PermissionRule(
            value = "DELETE_OTHER_DOCUMENTS",
            description = "The lead partners can delete Other Documents (Collaboration Agreement, Exploitation Plan) for their Projects")
    public boolean leadPartnersCanDeleteOtherDocuments(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "SUBMIT_OTHER_DOCUMENTS",
            description = "Only a project manager can submit completed partner documents")
    public boolean onlyProjectManagerCanMarkDocumentsAsSubmit(ProjectResource project, UserResource user) {
        return isProjectManager(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "ACCEPT_REJECT_OTHER_DOCUMENTS",
            description = "Competition Admin can accept or reject Other Documents (Collaboration Agreement, Exploitation Plan)")
    public boolean competitionAdminCanAcceptOrRejectOtherDocuments(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "ACCEPT_REJECT_OTHER_DOCUMENTS",
            description = "Project finance user can accept or reject Other Documents (Collaboration Agreement, Exploitation Plan)")
    public boolean projectFinanceUserCanAcceptOrRejectOtherDocuments(ProjectResource project, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "All partners can view team status")
    public boolean partnersCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "Comp admins can see a team's status")
    public boolean compAdminsCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "VIEW_TEAM_STATUS",
            description = "Project finance user can see a team's status")
    public boolean projectFinanceUserCanViewTeamStatus(ProjectResource project, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "All partners can view the project status")
    public boolean partnersCanViewStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "Comp admins can see the project status")
    public boolean compAdminsCanViewStatus(ProjectResource project, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "VIEW_STATUS",
            description = "Project finance user can see the project status")
    public boolean projectFinanceUserCanViewStatus(ProjectResource project, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "SEND_GRANT_OFFER_LETTER",
            description = "Comp admins & project finance can send the Grant Offer Letter notification")
    public boolean internalUserCanSendGrantOfferLetter(ProjectResource project, UserResource user) {
        return isCompAdmin(user) || isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "APPROVE_SIGNED_GRANT_OFFER_LETTER",
            description = "Internal users can approve the signed Grant Offer Letter")
    public boolean internalUsersCanApproveSignedGrantOfferLetter(ProjectResource project, UserResource user) {
        return isCompAdmin(user) || isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER_LETTER_SEND_STATUS",
            description = "Comp admins & project finance can view the send status of Grant Offer Letter for a project")
    public boolean internalUserCanViewSendGrantOfferLetterStatus(ProjectResource project, UserResource user) {
        return isCompAdmin(user) || isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_GRANT_OFFER_LETTER_SEND_STATUS",
            description = "Partners can view the send status of Grant Offer Letter for a project")
    public boolean externalUserCanViewSendGrantOfferLetterStatus(ProjectResource project, UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "VIEW_GRANT_OFFER", description = "A user can see grant offer page that they are partners on")
    public boolean partnersOnProjectCanViewGrantOfferPage(ProjectResource project, UserResource user) {
        return project != null && isPartner(project.getId(), user.getId());
    }

}
