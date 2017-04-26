package org.innovateuk.ifs.project.sections;

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

public class ProjectSetupSectionsInternalUserTest extends BaseUnitTest {

    @Mock
    private ProjectSetupProgressChecker projectSetupProgressCheckerMock;

    @InjectMocks
    private ProjectSetupSectionInternalUser internalUser;

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));

        verifyInteractions(
                mock -> mock.isProjectDetailsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionButProjectDetailsSectionIncomplete() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionHappyPath() {
        when(projectSetupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(null));

        verifyInteractions(
                mock -> mock.canAccessMonitoringOfficer()
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(projectSetupProgressCheckerMock.canAccessMonitoringOfficer()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(null));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isBankDetailsActionRequired()).thenReturn(true);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(getFinanceTeamMember()));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionButNotFinanceTeamMember() {

        stream(UserRoleType.values()).forEach(role -> {
            if (role != PROJECT_FINANCE) {

                List<RoleResource> roles = newRoleResource().withType(role).build(1);
                UserResource nonFinanceTeam = newUserResource().withRolesGlobal(roles).build();
                assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(nonFinanceTeam));

                verify(projectSetupProgressCheckerMock, never()).isBankDetailsApproved();
                verify(projectSetupProgressCheckerMock, never()).isBankDetailsActionRequired();
            }
        });

    }

    @Test
    public void testCheckAccessToBankDetailsSectionButBankDetailsNotApproved() {
        when(projectSetupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isBankDetailsAccessible()).thenReturn(true);
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
        when(projectSetupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessSpendProfileSection(null));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isSpendProfileSubmitted()
        );
    }

    @Test
    public void testCheckAccessToSpendProfileSectionButSpendProfileSectionIsNotSubmitted() {
        when(projectSetupProgressCheckerMock.isSpendProfileSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessSpendProfileSection(getFinanceTeamMember()));
    }

    private UserResource getFinanceTeamMember() {
        List<RoleResource> roles = newRoleResource().withType(PROJECT_FINANCE).build(1);
        return newUserResource().withRolesGlobal(roles).build();
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isOtherDocumentsRejected()).thenReturn(false);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(null));

        verifyInteractions(
                mock -> mock.isOtherDocumentsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionDocsApproved() {
        when(projectSetupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(null));

        verifyInteractions(
                mock -> mock.isOtherDocumentsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionDocsRejected() {
        when(projectSetupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isOtherDocumentsRejected()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(null));

        verifyInteractions(
                mock -> mock.isOtherDocumentsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionButSpendProfileSectionIsNotSubmitted() {
        when(projectSetupProgressCheckerMock.isOtherDocumentsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessOtherDocumentsSection(null));
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));

        verifyInteractions(mock -> mock.isGrantOfferLetterSent());
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionButOtherSectionsAreIncomplete() {
        when(projectSetupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSection(null));
    }
    @Test
    public void testCheckAccessToGrantOfferLetterSendSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);

        Map<UserRoleType, ProjectActivityStates> roleSpecificActivityStates = new HashMap<UserRoleType, ProjectActivityStates>();
        roleSpecificActivityStates.put(COMP_ADMIN, ProjectActivityStates.ACTION_REQUIRED);
        when(projectSetupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

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
        when(projectSetupProgressCheckerMock.getRoleSpecificActivityState()).thenReturn(roleSpecificActivityStates);

        List<RoleResource> roles = RoleResourceBuilder.newRoleResource().withType(PROJECT_FINANCE).build(1);
        UserResource financeUser = newUserResource().withRolesGlobal(roles).build();
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, internalUser.grantOfferLetterActivityStatus(financeUser));

        verifyInteractions(mock -> mock.getRoleSpecificActivityState());
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionSendButOtherSectionsAreIncomplete() {
        List<RoleResource> roles = RoleResourceBuilder.newRoleResource().withType(COMP_ADMIN).build(1);
        UserResource compAdmin = newUserResource().withRolesGlobal(roles).build();
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessGrantOfferLetterSendSection(compAdmin));
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<ProjectSetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(projectSetupProgressCheckerMock)));
        verifyNoMoreInteractions(projectSetupProgressCheckerMock);
    }
}
