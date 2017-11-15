package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.user.builder.RoleResourceBuilder;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.innovateuk.ifs.project.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.project.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SetupSectionsInternalUserTest extends BaseUnitTest {

    @Mock
    private SetupProgressChecker setupProgressCheckerMock;

    @InjectMocks
    private SetupSectionInternalUser internalUser;

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPath() {
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));

        verifyInteractions(
                SetupProgressChecker::isProjectDetailsSubmitted
        );
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionButProjectDetailsSectionIncomplete() {
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionHappyPath() {
        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer
        );
    }

    @Test
    public void testSupportUserCannotAccessIfMonitoringOfficerNotSubmitted() {

        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressCheckerMock.isMonitoringOfficerSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(getSupportUser()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    private UserResource getSupportUser(){
        return newUserResource().withRolesGlobal(newRoleResource().withType(SUPPORT).build(1)).build();
    }

    private UserResource getInnovationLeadUser(){
        return newUserResource().withRolesGlobal(newRoleResource().withType(INNOVATION_LEAD).build(1)).build();
    }

    @Test
    public void testSupportUserCanAccessIfMonitoringOfficerSubmitted() {
        UserResource supportUser = newUserResource().withRolesGlobal(newRoleResource().withType(SUPPORT).build(1)).build();

        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressCheckerMock.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(supportUser));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void testInnovationLeadUserCanAccessIfMonitoringOfficerSubmitted() {
        UserResource supportUser = newUserResource().withRolesGlobal(newRoleResource().withType(INNOVATION_LEAD).build(1)).build();

        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressCheckerMock.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(supportUser));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(null));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionHappyPath() {
        when(setupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isBankDetailsActionRequired()).thenReturn(true);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(getFinanceTeamMember()));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionButNotFinanceTeamMember() {

        stream(UserRoleType.values()).forEach(role -> {
            if (role != PROJECT_FINANCE) {

                List<RoleResource> roles = newRoleResource().withType(role).build(1);
                UserResource nonFinanceTeam = newUserResource().withRolesGlobal(roles).build();
                assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(nonFinanceTeam));

                verify(setupProgressCheckerMock, never()).isBankDetailsApproved();
                verify(setupProgressCheckerMock, never()).isBankDetailsActionRequired();
            }
        });

    }

    @Test
    public void testCheckAccessToBankDetailsSectionButBankDetailsNotApproved() {
        when(setupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isBankDetailsAccessible()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessBankDetailsSection(getFinanceTeamMember()));
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionAsFinanceTeamMembers() {
        assertEquals(ACCESSIBLE, internalUser.canAccessFinanceChecksSection(getFinanceTeamMember()));
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionAsNonFinanceTeamMembers() {
        stream(UserRoleType.values()).forEach(role -> {
            if (role != PROJECT_FINANCE) {
                List<RoleResource> roles = newRoleResource().withType(role).build(1);
                UserResource nonFinanceTeam = newUserResource().withRolesGlobal(roles).build();
                assertEquals(NOT_ACCESSIBLE, internalUser.canAccessFinanceChecksSection(nonFinanceTeam));
            }
        });
    }

    @Test
    public void testCheckAccessToSpendProfileSectionHappyPath() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void testCheckSupportAccessToSpendProfileWhenNotApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getSupportUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void testCheckSupportAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(getSupportUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void testCheckInnovationLeadAccessToSpendProfileWhenNotApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getInnovationLeadUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void testCheckInnovationLeadAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(getInnovationLeadUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void testCheckAccessToSpendProfileSectionButSpendProfileSectionIsNotSubmitted() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getFinanceTeamMember()));
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection((getSupportUser())));
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection((getInnovationLeadUser())));
    }

    private UserResource getFinanceTeamMember() {
        List<RoleResource> roles = newRoleResource().withType(PROJECT_FINANCE).build(1);
        return newUserResource().withRolesGlobal(roles).build();
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPath() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                SetupProgressChecker::isOtherDocumentsSubmitted
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionDocsApproved() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                SetupProgressChecker::isOtherDocumentsSubmitted
        );
    }

    @Test
    public void testCheckSupportAccessToOtherDocumentsSectionDocsApproved() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(getSupportUser()));

        verifyInteractions(
                SetupProgressChecker::isOtherDocumentsSubmitted,
                SetupProgressChecker::isOtherDocumentsApproved
        );
    }

    @Test
    public void testCheckSupportAccessToOtherDocumentsSectionDocsNotApproved() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(getSupportUser()));

        verifyInteractions(
                SetupProgressChecker::isOtherDocumentsSubmitted,
                SetupProgressChecker::isOtherDocumentsApproved
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionDocsRejected() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isOtherDocumentsRejected()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                SetupProgressChecker::isOtherDocumentsSubmitted
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionButSpendProfileSectionIsNotSubmitted() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(null));
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionHappyPath() {
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionButOtherSectionsAreIncomplete() {
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));
    }
    @Test
    public void testCheckAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);

        Map<UserRoleType, ProjectActivityStates> roleSpecificActivityStates = new HashMap<>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(setupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<RoleResource> roles = RoleResourceBuilder.newRoleResource().withType(COMP_ADMIN).build(1);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(compAdmin));

        verifyInteractions(SetupProgressChecker::isOtherDocumentsApproved,
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::getRoleSpecificActivityState);
    }


    @Test
    public void testCheckSupportAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isOtherDocumentsApproved,
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void testCheckSupportAccessToGrantOfferLetterSendSectionGrantOfferLetterApproved() {
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isOtherDocumentsApproved,
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void testCheckSupportAccessToGrantOfferLetterSection() {
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void testCheckFinanceUserGetsCompAdminActivityStates() {
        Map<UserRoleType, ProjectActivityStates> roleSpecificActivityStates = new HashMap<>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(setupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<RoleResource> roles = RoleResourceBuilder.newRoleResource().withType(PROJECT_FINANCE).build(1);
        UserResource financeUser = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(financeUser));

        verifyInteractions(SetupProgressChecker::getRoleSpecificActivityState);
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionSendButOtherSectionsAreIncomplete() {
        List<RoleResource> roles = RoleResourceBuilder.newRoleResource().withType(COMP_ADMIN).build(1);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<SetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(setupProgressCheckerMock)));
        verifyNoMoreInteractions(setupProgressCheckerMock);
    }
}
