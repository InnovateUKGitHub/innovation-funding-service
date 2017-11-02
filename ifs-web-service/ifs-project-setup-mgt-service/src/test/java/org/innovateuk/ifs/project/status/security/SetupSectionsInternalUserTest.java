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
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
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
                mock -> mock.isProjectDetailsSubmitted()
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
                mock -> mock.canAccessMonitoringOfficer()
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
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isSpendProfileSubmitted()
        );
    }

    @Test
    public void testCheckAccessToSpendProfileSectionButSpendProfileSectionIsNotSubmitted() {
        when(setupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getFinanceTeamMember()));
    }

    private UserResource getFinanceTeamMember() {
        List<RoleResource> roles = newRoleResource().withType(PROJECT_FINANCE).build(1);
        return newUserResource().withRolesGlobal(roles).build();
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPath() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isOtherDocumentsRejected()).thenReturn(false);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                mock -> mock.isOtherDocumentsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionDocsApproved() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                mock -> mock.isOtherDocumentsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionDocsRejected() {
        when(setupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isOtherDocumentsRejected()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(newUserResource().withRolesGlobal(newRoleResource().withType(COMP_ADMIN).build(1)).build()));

        verifyInteractions(
                mock -> mock.isOtherDocumentsSubmitted()
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

        verifyInteractions(mock -> mock.isGrantOfferLetterSent());
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

        Map<UserRoleType, ProjectActivityStates> roleSpecificActivityStates = new HashMap<UserRoleType, ProjectActivityStates>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(setupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<RoleResource> roles = RoleResourceBuilder.newRoleResource().withType(COMP_ADMIN).build(1);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(compAdmin));

        verifyInteractions(mock -> mock.isOtherDocumentsApproved(),
                           mock -> mock.isSpendProfileApproved(),
                           mock -> mock.getRoleSpecificActivityState());
    }

    @Test
    public void testCheckFinanceUserGetsCompAdminActivityStates() {
        Map<UserRoleType, ProjectActivityStates> roleSpecificActivityStates = new HashMap<UserRoleType, ProjectActivityStates>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(setupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<RoleResource> roles = RoleResourceBuilder.newRoleResource().withType(PROJECT_FINANCE).build(1);
        UserResource financeUser = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(financeUser));

        verifyInteractions(mock -> mock.getRoleSpecificActivityState());
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
