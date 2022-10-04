package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplicationEoiEvidenceResponsePermissionRulesTest extends BasePermissionRulesTest<ApplicationEoiEvidenceResponsePermissionRules> {

    private ApplicationResource applicationResource;
    private ProcessRole leadProcessRole;
    private ProcessRole collaboratorProcessRole;

    private UserResource leadApplicant;
    private UserResource collaborator;
    private final long leadOrganisationId = 1L;
    private final long collaboratorOrgId = 2L;

    private OrganisationResource leadOrganisation;
    private OrganisationResource collaboratorOrg;


    @Override
    protected ApplicationEoiEvidenceResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationEoiEvidenceResponsePermissionRules();
    }

    @Before
    public void setup() {
        leadOrganisation = newOrganisationResource().withId(leadOrganisationId).build();
        applicationResource = newApplicationResource().withLeadOrganisationId(leadOrganisationId).build();
        Application application = newApplication().withId(applicationResource.getId()).build();
        leadApplicant = newUserResource().withRoleGlobal(APPLICANT).build();
        leadProcessRole = newProcessRole()
                .withRole(LEADAPPLICANT)
                .withApplication(application)
                .withOrganisationId(leadOrganisationId)
                .withUser(newUser()
                        .withId(leadApplicant.getId())
                        .build())
                .build();

        collaboratorOrg = newOrganisationResource().withId(collaboratorOrgId).build();
        collaborator = newUserResource().withRoleGlobal(APPLICANT).build();
        collaboratorProcessRole = newProcessRole()
                .withRole(COLLABORATOR)
                .withApplication(application)
                .withOrganisationId(collaboratorOrgId)
                .withUser(newUser()
                        .withId(collaborator.getId())
                        .build())
                .build();
        setUpApplicationUsersForLeadOrganisation(applicationResource, leadOrganisation, leadApplicant, leadProcessRole);
        setUpApplicationUsersForLeadOrganisation(applicationResource, collaboratorOrg, collaborator, collaboratorProcessRole);

    }

    @Test
    public void leadOrganisationMemberCanSendApplicationSubmittedNotification() {
        assertTrue(rules.isLeadOrganisationMemberCanSendApplicationSubmittedNotification(applicationResource, leadApplicant));
        assertFalse(rules.isLeadOrganisationMemberCanSendApplicationSubmittedNotification(applicationResource, collaborator));
    }

    @Test
    public void applicantCanCreateFileEntryAndEoiEvidence() {
        assertTrue(rules.applicantCanCreateFileEntryAndEoiEvidence(applicationResource, leadApplicant));
        assertFalse(rules.applicantCanCreateFileEntryAndEoiEvidence(applicationResource, collaborator));
    }

    @Test
    public void applicantCanSubmitEoiEvidence() {
        assertTrue(rules.applicantCanSubmitEoiEvidence(applicationResource, leadApplicant));
        assertFalse(rules.applicantCanSubmitEoiEvidence(applicationResource, collaborator));

    }

    @Test
    public void applicantCanRemoveEoiEvidence() {
        assertTrue(rules.applicantCanRemoveEoiEvidence(applicationResource, leadApplicant));
        assertFalse(rules.applicantCanRemoveEoiEvidence(applicationResource, collaborator));
    }

    @Test
    public void applicantCanGetEoiEvidenceFileContents() {
        assertTrue(rules.applicantCanViewEvidenceFileContents(applicationResource, leadApplicant));
        assertFalse(rules.applicantCanViewEvidenceFileContents(applicationResource, collaborator));
    }

    @Test
    public void applicantCanGetEoiEvidenceFileDetails() {
        assertTrue(rules.applicantCanGetEvidenceFileEntryDetails(applicationResource, leadApplicant));
        assertFalse(rules.applicantCanGetEvidenceFileEntryDetails(applicationResource, collaborator));
    }
    
}