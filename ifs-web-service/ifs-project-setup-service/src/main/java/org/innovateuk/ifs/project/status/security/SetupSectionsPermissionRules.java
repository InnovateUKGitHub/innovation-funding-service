package org.innovateuk.ifs.project.status.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.otherdocuments.OtherDocumentsService;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.project.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@PermissionRules
@Component
public class SetupSectionsPermissionRules {

    private static final Log LOG = LogFactory.getLog(SetupSectionsPermissionRules.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private OtherDocumentsService otherDocumentsService;

    @Autowired
    private OrganisationService organisationService;

    private SetupSectionPartnerAccessorSupplier accessorSupplier = new SetupSectionPartnerAccessorSupplier();

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "A partner can access the Project Details section when their Companies House data is complete or not required")
    public boolean partnerCanAccessProjectDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessProjectDetailsSection);
    }

    @PermissionRule(value = "ACCESS_PROJECT_MANAGER_PAGE", description = "A lead can access the Project Manager " +
            "page when their Companies House data is complete or not required, and the Project Details have not been submitted")
    public boolean leadCanAccessProjectManagerPage(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::leadCanAccessProjectManagerPage);
    }

    @PermissionRule(value = "ACCESS_PROJECT_START_DATE_PAGE", description = "A lead can access the Project Start Date " +
            "page when their Companies House data is complete or not required, and the Project Details have not been submitted")
    public boolean leadCanAccessProjectStartDatePage(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::leadCanAccessProjectStartDatePage);
    }

    @PermissionRule(value = "ACCESS_PROJECT_ADDRESS_PAGE", description = "A lead can access the Project Address " +
            "page when their Companies House data is complete or not required, and the Project Details have not been submitted")
    public boolean leadCanAccessProjectAddressPage(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::leadCanAccessProjectAddressPage);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "A partner can access the Monitoring Officer " +
            "section when their Companies House details are complete or not required, and the Project Details have been submitted")
    public boolean partnerCanAccessMonitoringOfficerSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessMonitoringOfficerSection);
    }

    @PermissionRule(value = "ACCESS_BANK_DETAILS_SECTION", description = "A partner can access the Bank Details " +
            "section when their Companies House details are complete or not required, and they have a Finance Contact " +
            "available for their Organisation")
    public boolean partnerCanAccessBankDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessBankDetailsSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL", description = "A partner can access the finance details " +
            " when their Companies House details are complete or not required, and the Project Details have been submitted")
    public boolean partnerCanAccessFinanceChecksSection(Long projectId, UserResource user) {
            return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessFinanceChecksSection);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "A partner can access the Spend Profile " +
            "section when their Companies House details are complete or not required, the Project Details have been submitted, " +
            "and the Organisation's Bank Details have been approved or queried")
    public boolean partnerCanAccessSpendProfileSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @PermissionRule(value = "ACCESS_COMPANIES_HOUSE_SECTION", description = "A partner can access the Companies House " +
            "section if their Organisation is a business type (i.e. if Companies House details are required)")
    public boolean partnerCanAccessCompaniesHouseSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessCompaniesHouseSection);
    }

    @PermissionRule(value = "ACCESS_OTHER_DOCUMENTS_SECTION", description = "A partner can access the Other Documents " +
            "section if their Organisation is a business type (i.e. if Companies House details are required)")
    public boolean partnerCanAccessOtherDocumentsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessOtherDocumentsSection);
    }

    @PermissionRule(value = "SUBMIT_OTHER_DOCUMENTS_SECTION", description = "A project manager can submit uploaded Other Documents " +
            "if they have not already been submitted, they are allowed to submit and haven't been rejected")
    public boolean projectManagerCanSubmitOtherDocumentsSection(Long projectId, UserResource user) {
        return doSubmitOtherDocumentsCheck(projectId, user);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SECTION", description = "A lead partner can access the Grant Offer Letter " +
            "section when all other sections are complete and a Grant Offer Letter has been generated by the internal team")
    public boolean partnerCanAccessGrantOfferLetterSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection);
    }

    @PermissionRule(value = "ACCESS_SIGNED_GRANT_OFFER_LETTER", description = "A lead partner can view and download signed grant offer letter document")
    public boolean leadPartnerAccess(Long projectId, UserResource user) {
        return projectService.isUserLeadPartner(projectId, user.getId());
    }

    @PermissionRule(value = "MARK_SPEND_PROFILE_INCOMPLETE", description = "All lead partners can mark partners spend profiles as incomplete")
    public boolean userCanMarkSpendProfileIncomplete(Long projectId, UserResource user) {
        List<ProjectUserResource> projectLeadPartners = projectService.getLeadPartners(projectId);
        Optional<ProjectUserResource> returnedProjectUser = simpleFindFirst(projectLeadPartners, projectUserResource -> projectUserResource.getUser().equals(user.getId()));

        return returnedProjectUser.isPresent();
    }

    private boolean doSubmitOtherDocumentsCheck(Long projectId, UserResource user) {
        ProjectResource project = projectService.getById(projectId);
        boolean isProjectManager = projectService.isProjectManager(user.getId(), projectId);
        boolean isSubmitAllowed = otherDocumentsService.isOtherDocumentSubmitAllowed(projectId);

        boolean otherDocumentsSubmitted = project.getDocumentsSubmittedDate() != null;
        ApprovalType otherDocumentsApproved = project.getOtherDocumentsApproved();

        return isProjectManager && !otherDocumentsSubmitted && isSubmitAllowed && !otherDocumentsApproved.equals(ApprovalType.REJECTED);
    }

    @PermissionRule(value = "IS_NOT_FROM_OWN_ORGANISATION", description = "A lead partner cannot mark their own spend profiles as incomplete")
    public boolean userCannotMarkOwnSpendProfileIncomplete(Long organisationId, UserResource user) {
        OrganisationResource organisation = organisationService.getOrganisationForUser(user.getId());

        return !organisation.getId().equals(organisationId);
    }

    private boolean doSectionCheck(Long projectId, UserResource user, BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> sectionCheckFn) {
        try {
            Long organisationId = organisationService.getOrganisationIdFromUser(projectId, user);

            ProjectTeamStatusResource teamStatus;

            teamStatus = statusService.getProjectTeamStatus(projectId, Optional.of(user.getId()));

            ProjectPartnerStatusResource partnerStatusForUser = teamStatus.getPartnerStatusForOrganisation(organisationId).get();

            SetupSectionAccessibilityHelper sectionAccessor = accessorSupplier.apply(teamStatus);
            OrganisationResource organisation = new OrganisationResource();
            organisation.setId(partnerStatusForUser.getOrganisationId());
            organisation.setOrganisationType(partnerStatusForUser.getOrganisationType().getId());

            return sectionCheckFn.apply(sectionAccessor, organisation) == ACCESSIBLE;
        } catch (ForbiddenActionException e) {
            LOG.error("User " + user.getId() + " is not a Partner on an Organisation for Project " + projectId + ".  Denying access to Project Setup");
            return false;
        } /*catch (Exception e) {
            LOG.error("An exception occurred whilst checking project setup permissions. Denying access to Project Setup");
            return false;
        }*/
    }

    public class SetupSectionPartnerAccessorSupplier implements Function<ProjectTeamStatusResource, SetupSectionAccessibilityHelper> {
        @Override
        public SetupSectionAccessibilityHelper apply(ProjectTeamStatusResource teamStatus) {
            return new SetupSectionAccessibilityHelper(teamStatus);
        }
    }
}
