package com.worth.ifs.project.sections;

import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class ProjectSetupSectionInternalUser {

    private static final Log LOG = LogFactory.getLog(ProjectSetupSectionInternalUser.class);

    private ProjectSetupProgressChecker projectSetupProgressChecker;

    public ProjectSetupSectionInternalUser(ProjectTeamStatusResource projectTeamStatus) {
        this.projectSetupProgressChecker = new ProjectSetupProgressChecker(projectTeamStatus);
    }

    public SectionAccess canAccessCompaniesHouseSection(UserResource userResource) {
        return SectionAccess.NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessProjectDetailsSection(UserResource userResource) {
        return SectionAccess.NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessMonitoringOfficerSection(UserResource userResource) {
        if (!projectSetupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Monitoring Officer section until Project Details are submitted");
        }

        return SectionAccess.ACCESSIBLE;
    }

    public SectionAccess canAccessBankDetailsSection(OrganisationResource organisation) {
        return SectionAccess.NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceChecksSection(OrganisationResource organisation) {
        return SectionAccess.NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessSpendProfileSection(OrganisationResource organisation) {
        return SectionAccess.NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessOtherDocumentsSection(OrganisationResource organisation) {
        return SectionAccess.NOT_ACCESSIBLE;
    }

    public boolean isProjectDetailsSubmitted() {
        return projectSetupProgressChecker.isProjectDetailsSubmitted();
    }

    private SectionAccess fail(String message) {
        LOG.info(message);
        return SectionAccess.NOT_ACCESSIBLE;
    }

    private boolean isCompaniesHouseSectionIsUnnecessaryOrComplete(OrganisationResource organisation, String failureMessage) {
        if (!projectSetupProgressChecker.isCompaniesHouseSectionRequired(organisation)) {
            return true;
        }

        if (projectSetupProgressChecker.isCompaniesHouseDetailsComplete(organisation)) {
            return true;
        }

        LOG.info(failureMessage);
        return false;
    }
}
