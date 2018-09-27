package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OverheadFilePermissionRulesTest extends BasePermissionRulesTest<OverheadFilePermissionRules> {

    private FinanceRow overheads;
    private FinanceRow submittedOverheads;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;
    private UserResource otherLeadApplicant;
    private UserResource supportUser;
    private UserResource ifsAdmin;
    private UserResource projectFinance;
    private UserResource innovationLead;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Override
    protected OverheadFilePermissionRules supplyPermissionRulesUnderTest() {
        return new OverheadFilePermissionRules();
    }

    @Before
    public void setup() throws Exception {

        // Create a compAdmin
        compAdmin = compAdminUser();
        {
            // Set up users on an organisation and application
            final Long applicationId = 1L;
            final Long submittedApplicationId = 2L;
            final Long organisationId = 2L;

            final Application application = newApplication()
                    .with(id(applicationId))
                    .withApplicationState(ApplicationState.OPEN)
                    .build();

            final Application submittedApplication = newApplication()
                    .with(id(submittedApplicationId))
                    .withApplicationState(ApplicationState.SUBMITTED)
                    .build();

            final Organisation organisation = newOrganisation().with(id(organisationId)).build();

            final ApplicationFinance applicationFinance = newApplicationFinance().withApplication(application).withOrganisation(organisation).build();
            final ApplicationFinance submittedApplicationFinance = newApplicationFinance().withApplication(submittedApplication).withOrganisation(organisation).build();

            overheads = newApplicationFinanceRow().withOwningFinance(applicationFinance).build();

            submittedOverheads = newApplicationFinanceRow().withOwningFinance(submittedApplicationFinance).build();

            leadApplicant = newUserResource().build();
            collaborator = newUserResource().build();

            when(applicationFinanceRowRepositoryMock.findOne(overheads.getId())).thenReturn((ApplicationFinanceRow) overheads);
            when(applicationFinanceRowRepositoryMock.findOne(submittedOverheads.getId())).thenReturn((ApplicationFinanceRow) submittedOverheads);
            when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicant.getId(), Role.LEADAPPLICANT, applicationId, organisationId)).
                    thenReturn(newProcessRole().build());
            when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaborator.getId(), Role.COLLABORATOR, applicationId, organisationId)).
                    thenReturn(newProcessRole().build());

            when(applicationRepositoryMock.findById(applicationId)).thenReturn(application);
            when(applicationRepositoryMock.findById(submittedApplicationId)).thenReturn(submittedApplication);
        }

        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3l;
            final long otherOrganisationId = 4l;
            otherLeadApplicant = newUserResource().build();
            when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(),
                    Role.LEADAPPLICANT, otherApplicationId, otherOrganisationId)).thenReturn(newProcessRole().build());
        }

        // Create project with users for testing getting of partner funding status
        {
            final Long projectId = 1L;
            final Long userId = 1L;
            when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(projectId, userId, PROJECT_PARTNER)).
                    thenReturn(Collections.singletonList(newProjectUser().withId(userId).build()));
        }

        // Create differnet users with different project
        {
            final Long otherProjectProjectId = 2L;
            final Long otherProjectUserId = 2L;
            when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(otherProjectProjectId, otherProjectUserId, PROJECT_PARTNER)).
                    thenReturn(Collections.singletonList(newProjectUser().withId(otherProjectUserId).build()));
        }
        //Create users with roles
        {
            ifsAdmin = newUserResource().withRolesGlobal(asList(Role.IFS_ADMINISTRATOR)).build();
            supportUser = newUserResource().withRolesGlobal(asList(Role.SUPPORT)).build();
            innovationLead = newUserResource().withRolesGlobal(asList(Role.INNOVATION_LEAD)).build();
            projectFinance = newUserResource().withRolesGlobal(asList(Role.PROJECT_FINANCE)).build();
        }
    }

    @Test
    public void consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation() {
        assertTrue(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, leadApplicant));
        assertTrue(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, collaborator));

        assertFalse(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(overheads, compAdmin));
    }

    @Test
    public void supportAndIfsAdminUsersCanReadContentsOfAnOverheadsFileForAnApplication() {
        assertTrue(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, supportUser));
        assertTrue(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, ifsAdmin));

        assertFalse(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, projectFinance));
        assertFalse(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, innovationLead));
        assertFalse(rules.supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(overheads, compAdmin));
    }

    @Test
    public void compAdminAndInnovationLeadAndProjectFinanceUsersCanReadDetailsOfAnOverheadsFileForASubmittedApplication() {
        assertTrue(rules.compAdminAndInnovationLeadAndProjectFinanceUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, projectFinance));
        assertTrue(rules.compAdminAndInnovationLeadAndProjectFinanceUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, compAdmin));
        assertTrue(rules.compAdminAndInnovationLeadAndProjectFinanceUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, innovationLead));
        assertTrue(rules.compAdminAndInnovationLeadAndProjectFinanceUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, supportUser));
        assertTrue(rules.compAdminAndInnovationLeadAndProjectFinanceUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(submittedOverheads, ifsAdmin));
    }
}
