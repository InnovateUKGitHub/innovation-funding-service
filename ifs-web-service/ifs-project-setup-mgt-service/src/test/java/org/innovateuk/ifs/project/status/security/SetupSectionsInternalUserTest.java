package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SetupSectionsInternalUserTest extends BaseUnitTest {

    @Mock
    private SetupProgressChecker setupProgressCheckerMock;

    @InjectMocks
    private SetupSectionInternalUser internalUser;

    @Test
    public void checkAccessToProjectDetailsSectionHappyPath() {
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));

        verifyInteractions(
                SetupProgressChecker::isProjectDetailsSubmitted
        );
    }

    @Test
    public void checkAccessToProjectDetailsSectionButProjectDetailsSectionIncomplete() {
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));
    }

    @Test
    public void checkAccessToMonitoringOfficerSectionHappyPath() {
        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer
        );
    }

    @Test
    public void supportUserCannotAccessIfMonitoringOfficerNotSubmitted() {

        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressCheckerMock.isMonitoringOfficerSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(getSupportUser()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    private UserResource getSupportUser(){
        return newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build();
    }

    private UserResource getInnovationLeadUser(){
        return newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();
    }

    private UserResource stakeholderUser(){
        return newUserResource().withRolesGlobal(singletonList(STAKEHOLDER)).build();
    }

    private UserResource compAdmin(){
        return newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build();
    }

    @Test
    public void supportUserCanAccessIfMonitoringOfficerSubmitted() {
        UserResource supportUser = newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build();

        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressCheckerMock.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(supportUser));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void innovationLeadUserCanAccessIfMonitoringOfficerSubmitted() {
        UserResource supportUser = newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();

        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressCheckerMock.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(supportUser));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void canAccessMonitoringOfficerSectionWhenStakeholder() {

        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressCheckerMock.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(stakeholderUser()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void checkAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(setupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(null));
    }

    @Test
    public void checkAccessToBankDetailsSectionHappyPath() {
        when(setupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isBankDetailsActionRequired()).thenReturn(true);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(getFinanceTeamMember()));
    }

    @Test
    public void checkAccessToBankDetailsSectionButNotFinanceTeamMember() {

        stream(Role.values()).forEach(role -> {
            if (role != PROJECT_FINANCE) {

                List<Role> roles = singletonList(Role.getByName(role.getName()));
                UserResource nonFinanceTeam = newUserResource().withRolesGlobal(roles).build();
                assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(nonFinanceTeam));

                verify(setupProgressCheckerMock, never()).isBankDetailsApproved();
                verify(setupProgressCheckerMock, never()).isBankDetailsActionRequired();
            }
        });

    }

    @Test
    public void checkAccessToBankDetailsSectionButBankDetailsNotApproved() {
        when(setupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isBankDetailsAccessible()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessBankDetailsSection(getFinanceTeamMember()));
    }

    @Test
    public void checkAccessToFinanceChecksSectionAsFinanceTeamMembers() {
        assertEquals(ACCESSIBLE, internalUser.canAccessFinanceChecksSection(getFinanceTeamMember()));
    }

    @Test
    public void checkAccessToFinanceChecksSectionAsNonFinanceTeamMembers() {
        stream(Role.values()).forEach(role -> {
            if (role != PROJECT_FINANCE) {
                List<Role> roles = singletonList(Role.getByName(role.getName()));
                UserResource nonFinanceTeam = newUserResource().withRolesGlobal(roles).build();
                assertEquals(NOT_ACCESSIBLE, internalUser.canAccessFinanceChecksSection(nonFinanceTeam));
            }
        });
    }

    @Test
    public void checkAccessToSpendProfileSectionHappyPath() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkSupportAccessToSpendProfileWhenNotApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getSupportUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkSupportAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(getSupportUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkInnovationLeadAccessToSpendProfileWhenNotApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getInnovationLeadUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkInnovationLeadAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(getInnovationLeadUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkStakeholderAccessToSpendProfileWhenNotApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(stakeholderUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkStakeholderAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(stakeholderUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkAccessToSpendProfileSectionButSpendProfileSectionIsNotSubmitted() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getFinanceTeamMember()));
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection((getSupportUser())));
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection((getInnovationLeadUser())));
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection((stakeholderUser())));
    }

    private UserResource getFinanceTeamMember() {
        List<Role> roles = singletonList(PROJECT_FINANCE);
        return newUserResource().withRolesGlobal(roles).build();
    }

    @Test
    public void internalAdminCanAccessDocumentsSectionIrrespectiveOfDocumentStatus() {
        when(setupProgressCheckerMock.allDocumentsApproved()).thenReturn(false);
        assertEquals(ACCESSIBLE, internalUser.canAccessDocumentsSection(compAdmin()));
    }

    @Test
    public void supportCannotAccessDocumentsSectionWhenAllDocumentsNotYetApproved() {
        assertEquals(NOT_ACCESSIBLE, doTestDocumentsAccess(false, getSupportUser()));
    }

    @Test
    public void supportCanAccessDocumentsSectionWhenAllDocumentsAreApproved() {
        assertEquals(ACCESSIBLE, doTestDocumentsAccess(true, getSupportUser()));
    }

    @Test
    public void innovationLeadCannotAccessDocumentsSectionWhenAllDocumentsNotYetApproved() {
        assertEquals(NOT_ACCESSIBLE, doTestDocumentsAccess(false, getInnovationLeadUser()));
    }

    @Test
    public void innovationLeadCanAccessDocumentsSectionWhenAllDocumentsAreApproved() {
        assertEquals(ACCESSIBLE, doTestDocumentsAccess(true, getInnovationLeadUser()));
    }

    @Test
    public void stakeholderCannotAccessDocumentsSectionWhenAllDocumentsNotYetApproved() {
        assertEquals(NOT_ACCESSIBLE, doTestDocumentsAccess(false, stakeholderUser()));
    }

    @Test
    public void stakeholderCanAccessDocumentsSectionWhenAllDocumentsAreApproved() {
        assertEquals(ACCESSIBLE, doTestDocumentsAccess(true, stakeholderUser()));
    }

    private SectionAccess doTestDocumentsAccess(boolean allDocumentsApproved, UserResource user) {
        when(setupProgressCheckerMock.allDocumentsApproved()).thenReturn(allDocumentsApproved);
        SectionAccess sectionAccess = internalUser.canAccessDocumentsSection(user);

        verifyInteractions(
                SetupProgressChecker::allDocumentsApproved
        );

        return sectionAccess;
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionHappyPath() {
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionButOtherSectionsAreIncomplete() {
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));
    }
    @Test
    public void testCheckAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);

        Map<Role, ProjectActivityStates> roleSpecificActivityStates = new HashMap<>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(setupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<Role> roles = singletonList(COMP_ADMIN);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(compAdmin));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::getRoleSpecificActivityState);
    }

    //OD = Other Documents
    @Test
    public void checkAccessToGrantOfferLetterSendSectionWhenODNotApprovedButDocumentsApproved() {
        when(setupProgressCheckerMock.allDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);

        Map<Role, ProjectActivityStates> roleSpecificActivityStates = new HashMap<>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(setupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<Role> roles = singletonList(COMP_ADMIN);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(compAdmin));

        verifyInteractions(SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::getRoleSpecificActivityState);
    }


    @Test
    public void checkSupportAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void checkSupportAccessToGrantOfferLetterSendSectionWhenODNotApprovedButDocumentsApproved() {
        when(setupProgressCheckerMock.allDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void checkSupportAccessToGrantOfferLetterSendSectionGrantOfferLetterApproved() {
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void checkSupportAccessToGrantOfferLetterSection() {
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void checkInnovationLeadAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getInnovationLeadUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void checkInnovationLeadAccessToGrantOfferLetterSendSectionGrantOfferLetterApproved() {
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getInnovationLeadUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void checkInnovationLeadAccessToGrantOfferLetterSection() {
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(getInnovationLeadUser()));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void checkStakeholderAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(stakeholderUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void checkStakeholderAccessToGrantOfferLetterSendSectionWhenGrantOfferLetterNotApproved() {
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterApproved()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(stakeholderUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved);
    }

    @Test
    public void checkFinanceUserGetsCompAdminActivityStates() {
        Map<Role, ProjectActivityStates> roleSpecificActivityStates = new HashMap<>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(setupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<Role> roles = singletonList(PROJECT_FINANCE);
        UserResource financeUser = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(financeUser));

        verifyInteractions(SetupProgressChecker::getRoleSpecificActivityState);
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionSendButOtherSectionsAreIncomplete() {
        List<Role> roles = singletonList(COMP_ADMIN);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<SetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(setupProgressCheckerMock)));
        verifyNoMoreInteractions(setupProgressCheckerMock);
    }
}
