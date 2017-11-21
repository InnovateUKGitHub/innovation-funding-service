package org.innovateuk.ifs.project.grantofferletter.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionAssessmentParticipantBuilder.newCompetitionAssessmentParticipant;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.INNOVATION_LEAD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class GrantOfferLetterPermissionRulesTest extends BasePermissionRulesTest<GrantOfferLetterPermissionRules> {
    private ProjectResource projectResource1;
    private RoleResource innovationLeadRole = newRoleResource().withType(INNOVATION_LEAD).build();
    private UserResource innovationLeadUserResourceOnProject1;

    @Before
    public void setup() {
        User innovationLeadUserOnProject1 = newUser().withRoles(new HashSet<>(newRole().build(1))).build();
        innovationLeadUserResourceOnProject1 = newUserResource().withId(innovationLeadUserOnProject1.getId()).withRolesGlobal(singletonList(innovationLeadRole)).build();
        CompetitionAssessmentParticipant competitionParticipant = newCompetitionAssessmentParticipant().withUser(innovationLeadUserOnProject1).build();
        Competition competition = newCompetition().withLeadTechnologist(innovationLeadUserOnProject1).build();
        Application application1 = newApplication().withCompetition(competition).build();
        ApplicationResource applicationResource1 = newApplicationResource().withId(application1.getId()).withCompetition(competition.getId()).build();
        projectResource1 = newProjectResource().withApplication(applicationResource1).build();

        when(applicationRepositoryMock.findOne(application1.getId())).thenReturn(application1);
        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(competition.getId(), CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(Collections.singletonList(competitionParticipant));
    }

    @Test
    public void testLeadPartnersCanCreateSignedGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonLeadPartnersCannotCreateSignedGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanCreateSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanUploadGrantOfferLetter(project, user));

    }

    @Test
    public void testNonProjectManagerCannotCreateSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerCanUploadGrantOfferLetter(project, user));

    }


    @Test
    public void testPartnersCanViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testPartnersCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testCompAdminsCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testCompAdminsCanViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectFinanceCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonProjectFinanceCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectFinanceCanViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonProjectFinanceCannotViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanSubmitOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerSubmitGrantOfferLetter(ProjectCompositeId.id(project.getId()), user));

    }

    @Test
    public void testNonProjectManagerCannotSubmitOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerSubmitGrantOfferLetter(ProjectCompositeId.id(project.getId()), user));
    }

    @Test
    public void testCompAdminsCanApproveSignedGrantOfferLetters() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanApproveSignedGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotApproveSignedGrantOfferLetters() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanApproveSignedGrantOfferLetter(project, user));
    }

    @Test
    public void testLeadPartnerCanDeleteSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanDeleteSignedGrantOfferLetter(project, user));

    }

    @Test
    public void testNonLeadPartnerCanDeleteSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanDeleteSignedGrantOfferLetter(project, user));

    }

    @Test
    public void testProjectFinanceUserCanSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.internalUserCanSendGrantOfferLetter(project, projectFinanceUser()));
    }

    @Test
    public void testCompAdminsUserCanSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.internalUserCanSendGrantOfferLetter(project, compAdminUser()));
    }

    @Test
    public void testPartnerUserCannotSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertFalse(rules.internalUserCanSendGrantOfferLetter(project, user));
    }

    @Test
    public void testInternalUserCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser()) || user.equals(compAdminUser())) {
                assertTrue(rules.internalAdminUserCanViewSendGrantOfferLetterStatus(project, user));
            } else {
                assertFalse(rules.internalAdminUserCanViewSendGrantOfferLetterStatus(project, user));
            }
        });
    }

    @Test
    public void testPartnersCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        // Ensure partners can access
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertTrue(rules.externalUserCanViewSendGrantOfferLetterStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        // Ensure non-partners cannot access
        allGlobalRoleUsers.forEach(user2 -> assertFalse(rules.externalUserCanViewSendGrantOfferLetterStatus(project, user2)));
    }

    @Override
    protected GrantOfferLetterPermissionRules supplyPermissionRulesUnderTest() {
        return new GrantOfferLetterPermissionRules();
    }

    @Test
    public void testSupportUserCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(supportUser())) {
                assertTrue(rules.supportUserCanViewSendGrantOfferLetterStatus(project, user));
            } else {
                assertFalse(rules.supportUserCanViewSendGrantOfferLetterStatus(project, user));
            }
        });
    }

    @Test
    public void testSupportUsersCanDownloadGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(supportUser())) {
                assertTrue(rules.supportUsersCanDownloadGrantOfferLetter(project, user));
            } else {
                assertFalse(rules.supportUsersCanDownloadGrantOfferLetter(project, user));
            }
        });
    }

    @Test
    public void testOnlyInnovationLeadUsersAssignedToCompetitionCanDownloadGrantOfferLetter() {
        assertTrue(rules.innovationLeadUsersCanDownloadGrantOfferLetter(projectResource1, innovationLeadUserResourceOnProject1));
        assertFalse(rules.innovationLeadUsersCanDownloadGrantOfferLetter(projectResource1, innovationLeadUser()));
    }

    @Test
    public void testOnlyInnovationLeadUsersAssignedToCompetitionCanViewGrantOfferLetter() {
        assertTrue(rules.innovationLeadUsersCanViewGrantOfferLetter(projectResource1, innovationLeadUserResourceOnProject1));
        assertFalse(rules.innovationLeadUsersCanViewGrantOfferLetter(projectResource1, innovationLeadUser()));
    }

    @Test
    public void testSupportUsersCanViewGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(supportUser())) {
                assertTrue(rules.supportUsersCanViewGrantOfferLetter(project, user));
            } else {
                assertFalse(rules.supportUsersCanViewGrantOfferLetter(project, user));
            }
        });
    }

    @Test
    public void testPartnersOnProjectCanViewGrantOfferApprovedStatus(){
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersOnProjectCanViewGrantOfferApprovedStatus(project, user));

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersOnProjectCanViewGrantOfferApprovedStatus(project, user));
    }
}
