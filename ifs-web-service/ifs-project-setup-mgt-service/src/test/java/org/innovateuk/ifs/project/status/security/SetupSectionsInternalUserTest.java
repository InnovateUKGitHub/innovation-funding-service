package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.ACTION_REQUIRED;
import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SetupSectionsInternalUserTest extends BaseUnitTest {

    @Mock
    private SetupProgressChecker setupProgressChecker;

    @InjectMocks
    private SetupSectionInternalUser internalUser;

    @Test
    public void checkAccessToProjectDetailsSectionButProjectDetailsSectionIncomplete() {
        when(setupProgressChecker.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));
    }

    @Test
    public void checkAccessToMonitoringOfficerSectionHappyPath() {
        when(setupProgressChecker.canAccessMonitoringOfficer()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(newUserResource().withRoleGlobal(COMP_ADMIN).build()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer
        );
    }

    @Test
    public void supportUserCannotAccessIfMonitoringOfficerNotSubmitted() {

        when(setupProgressChecker.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressChecker.isMonitoringOfficerSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(getSupportUser()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    private UserResource getSupportUser(){
        return newUserResource().withRoleGlobal(SUPPORT).build();
    }

    private UserResource getInnovationLeadUser(){
        return newUserResource().withRoleGlobal(INNOVATION_LEAD).build();
    }

    private UserResource stakeholderUser(){
        return newUserResource().withRoleGlobal(STAKEHOLDER).build();
    }

    private UserResource compAdmin(){
        return newUserResource().withRoleGlobal(COMP_ADMIN).build();
    }

    @Test
    public void supportUserCanAccessIfMonitoringOfficerSubmitted() {
        UserResource supportUser = newUserResource().withRoleGlobal(SUPPORT).build();

        when(setupProgressChecker.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressChecker.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(supportUser));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void innovationLeadUserCanAccessIfMonitoringOfficerSubmitted() {
        UserResource supportUser = newUserResource().withRoleGlobal(INNOVATION_LEAD).build();

        when(setupProgressChecker.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressChecker.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(supportUser));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void canAccessMonitoringOfficerSectionWhenStakeholder() {

        when(setupProgressChecker.canAccessMonitoringOfficer()).thenReturn(true);
        when(setupProgressChecker.isMonitoringOfficerSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(stakeholderUser()));

        verifyInteractions(
                SetupProgressChecker::canAccessMonitoringOfficer,
                SetupProgressChecker::isMonitoringOfficerSubmitted
        );
    }

    @Test
    public void checkAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(setupProgressChecker.canAccessMonitoringOfficer()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(null));
    }

    @Test
    public void checkAccessToBankDetailsSectionHappyPath() {
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(false);
        when(setupProgressChecker.isBankDetailsActionRequired()).thenReturn(true);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(getFinanceTeamMember()));
    }

    @Test
    public void checkAccessToBankDetailsSectionButNotFinanceTeamMember() {

        stream(Role.values()).forEach(role -> {
            if (role != PROJECT_FINANCE) {

                List<Role> roles = singletonList(role);
                UserResource nonFinanceTeam = newUserResource().withRolesGlobal(roles).build();
                assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(nonFinanceTeam));

                verify(setupProgressChecker, never()).isBankDetailsApproved();
                verify(setupProgressChecker, never()).isBankDetailsActionRequired();
            }
        });

    }

    @Test
    public void checkAccessToBankDetailsSectionButBankDetailsNotApproved() {
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(false);
        when(setupProgressChecker.isBankDetailsAccessible()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessBankDetailsSection(getFinanceTeamMember()));
    }

    @Test
    public void checkAccessToFinanceChecksSectionAsFinanceTeamMembers() {
        assertEquals(ACCESSIBLE, internalUser.canAccessFinanceChecksSection(getFinanceTeamMember()));
    }

    @Test
    public void checkAccessToFinanceChecksSectionAsNonFinanceTeamMembers() {
        stream(Role.values()).forEach(role -> {
            if (role != PROJECT_FINANCE && role != EXTERNAL_FINANCE && role != IFS_ADMINISTRATOR && role != SUPER_ADMIN_USER && role != SYSTEM_MAINTAINER) {
                System.out.println(role.getDisplayName());
                List<Role> roles = singletonList(role);
                UserResource nonFinanceTeam = newUserResource().withRolesGlobal(roles).build();
                assertEquals(NOT_ACCESSIBLE, internalUser.canAccessFinanceChecksSection(nonFinanceTeam));
            }
        });
    }

    @Test
    public void checkAccessToSpendProfileSectionHappyPath() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(false);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(newUserResource().withRoleGlobal(COMP_ADMIN).build()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkSupportAccessToSpendProfileWhenNotApproved() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getSupportUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkSupportAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(getSupportUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkInnovationLeadAccessToSpendProfileWhenNotApproved() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getInnovationLeadUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkInnovationLeadAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(getInnovationLeadUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkStakeholderAccessToSpendProfileWhenNotApproved() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(stakeholderUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkStakeholderAccessToSpendProfileSectionWhenApproved() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(stakeholderUser()));
        verifyInteractions(
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isSpendProfileSubmitted
        );
    }

    @Test
    public void checkAccessToSpendProfileSectionButSpendProfileSectionIsNotSubmitted() {
        when(setupProgressChecker.isSpendProfileSubmitted()).thenReturn(false);
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
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(false);
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
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(allDocumentsApproved);
        SectionAccess sectionAccess = internalUser.canAccessDocumentsSection(user);

        verifyInteractions(SetupProgressChecker::allDocumentsApproved);

        return sectionAccess;
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionHappyPath() {
        when(setupProgressChecker.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionButOtherSectionsAreIncomplete() {
        when(setupProgressChecker.isGrantOfferLetterSent()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));
    }
    @Test
    public void checkAccessToGrantOfferLetterSendSectionHappyPath() {

        List<Role> roles = singletonList(COMP_ADMIN);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();

        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.getGrantOfferLetterState()).thenReturn(ACTION_REQUIRED);
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(compAdmin));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isGrantOfferLetterSent,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkAccessToGrantOfferLetterSendSectionWhenODNotApprovedButDocumentsApproved() {
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);
        when(setupProgressChecker.getGrantOfferLetterState()).thenReturn(ACTION_REQUIRED);

        List<Role> roles = singletonList(COMP_ADMIN);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(compAdmin));

        verifyInteractions(SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterSent,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkNoAccessToGrantOfferLetterSendSectionWhenBankDetailsNotApprovedButDocumentsApproved() {
        List<Role> roles = singletonList(COMP_ADMIN);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(compAdmin));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }


    @Test
    public void checkSupportAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterApproved()).thenReturn(true);
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved,
                SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkSupportAccessToGrantOfferLetterSendSectionWhenODNotApprovedButDocumentsApproved() {
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkSupportAccessToGrantOfferLetterSendSectionGrantOfferLetterApproved() {
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterApproved()).thenReturn(false);
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved,
                SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkSupportAccessToGrantOfferLetterSection() {
        when(setupProgressChecker.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(getSupportUser()));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void checkInnovationLeadAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterApproved()).thenReturn(true);
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getInnovationLeadUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved,
                SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkInnovationLeadAccessToGrantOfferLetterSendSectionGrantOfferLetterApproved() {
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterApproved()).thenReturn(false);
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(getInnovationLeadUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved,
                SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkInnovationLeadAccessToGrantOfferLetterSection() {
        when(setupProgressChecker.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(getInnovationLeadUser()));

        verifyInteractions(SetupProgressChecker::isGrantOfferLetterSent);
    }

    @Test
    public void checkStakeholderAccessToGrantOfferLetterSendSectionHappyPath() {
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterApproved()).thenReturn(true);
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(stakeholderUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved,
                SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkStakeholderAccessToGrantOfferLetterSendSectionWhenGrantOfferLetterNotApproved() {
        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterApproved()).thenReturn(false);
        when(setupProgressChecker.allDocumentsApproved()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved()).thenReturn(true);
        when(setupProgressChecker.isApplicationSuccessful()).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(stakeholderUser()));

        verifyInteractions(SetupProgressChecker::isSpendProfileApproved,
                SetupProgressChecker::isGrantOfferLetterApproved,
                SetupProgressChecker::allDocumentsApproved,
                SetupProgressChecker::isBankDetailsApproved,
                SetupProgressChecker::isApplicationSuccessful);
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionSendButOtherSectionsAreIncomplete() {
        List<Role> roles = singletonList(COMP_ADMIN);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<SetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(setupProgressChecker)));
        verifyNoMoreInteractions(setupProgressChecker);
    }
}
