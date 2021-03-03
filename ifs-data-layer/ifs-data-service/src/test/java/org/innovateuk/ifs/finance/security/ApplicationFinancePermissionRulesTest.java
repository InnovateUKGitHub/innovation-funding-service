package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.security.ApplicationSecurityHelper;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigBuilder.newCompetitionAssessmentConfig;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationFinancePermissionRulesTest extends BasePermissionRulesTest<ApplicationFinancePermissionRules> {

    private ApplicationFinanceResource applicationFinance;
    private Application application;
    private UserResource assessor;
    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;
    private UserResource stakeholderResource;
    private UserResource kta;

    private ApplicationFinanceResource otherApplicationFinance;
    private UserResource otherLeadApplicant;
    private UserResource otherKta;
    private UserResource supporter;

    @Override
    protected ApplicationFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationFinancePermissionRules();
    }

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private ApplicationSecurityHelper applicationSecurityHelper;

    @Before
    public void setup() {

        // Create a compAdmin
        compAdmin = this.compAdminUser();

        {
            // Set up users on an organisation and application
            final long applicationId = 1L;
            final long organisationId = 2L;

            CompetitionAssessmentConfig competitionAssessmentConfig = newCompetitionAssessmentConfig()
                    .withAssessorFinanceView(DETAILED)
                    .build();

            Competition competition = newCompetition()
                    .withCompetitionAssessmentConfig(competitionAssessmentConfig)
                    .build();

            application = newApplication().with(id(applicationId)).withCompetition(competition).build();

            OrganisationResource organisation = newOrganisationResource().with(id(organisationId)).build();
            applicationFinance = newApplicationFinanceResource().withOrganisation(organisation.getId()).withApplication(application.getId()).build();
            leadApplicant = newUserResource().build();
            assessor = newUserResource().build();
            collaborator = newUserResource().build();
            stakeholderResource = newUserResource().withRoleGlobal(STAKEHOLDER).build();
            kta = ktaUser();
            supporter = supporterUser();

            when(processRoleRepository.existsByUserIdAndRoleInAndApplicationIdAndOrganisationId(leadApplicant.getId(), EnumSet.of(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR), applicationId, organisationId)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndRoleInAndApplicationIdAndOrganisationId(collaborator.getId(), EnumSet.of(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR), applicationId, organisationId)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndRoleInAndApplicationIdAndOrganisationId(assessor.getId(), EnumSet.of(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR), applicationId, organisationId)).thenReturn(false);


            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), applicationId, ProcessRoleType.LEADAPPLICANT)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(collaborator.getId(), applicationId, ProcessRoleType.LEADAPPLICANT)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(assessor.getId(), applicationId, ProcessRoleType.ASSESSOR)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(kta.getId(), applicationId, ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER)).thenReturn(true);

            when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
            when(competitionRepository.findById(application.getCompetition().getId())).thenReturn(Optional.of(competition));

            setupSupporterAssignmentExpectations(application.getId(), supporter.getId(), true);
        }
        {
            // Set up different users on an organisation and application to check that there is no bleed through of permissions
            final long otherApplicationId = 3L;
            final long otherOrganisationId = 4L;
            OrganisationResource otherOrganisation = newOrganisationResource().with(id(otherOrganisationId)).build();

            CompetitionAssessmentConfig competitionAssessmentConfig = newCompetitionAssessmentConfig()
                    .withAssessorFinanceView(DETAILED)
                    .build();

            Competition otherCompetition = newCompetition().withCompetitionAssessmentConfig(competitionAssessmentConfig).build();
            Application otherApplication = newApplication().with(id(otherApplicationId)).withCompetition(otherCompetition).build();
            otherApplicationFinance = newApplicationFinanceResource().withOrganisation(otherOrganisation.getId()).withApplication(otherApplication.getId()).build();
            otherLeadApplicant = newUserResource().build();
            otherKta = ktaUser();
            when(processRoleRepository.existsByUserIdAndRoleAndApplicationIdAndOrganisationId(otherLeadApplicant.getId(), ProcessRoleType.LEADAPPLICANT, otherApplicationId, otherOrganisationId)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(otherLeadApplicant.getId(), otherApplicationId, ProcessRoleType.LEADAPPLICANT)).thenReturn(true);
            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(otherKta.getId(), otherApplicationId, ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER)).thenReturn(true);

            when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(kta.getId(), otherApplicationId, ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER)).thenReturn(false);

            setupSupporterAssignmentExpectations(otherApplication.getId(), supporter.getId(), false);
        }
    }

    @Test
    public void canFinancesIfCanViewApplication() {
        UserResource canView = newUserResource().build();
        UserResource cantView = newUserResource().build();

        when(applicationSecurityHelper.canViewApplication(applicationFinance.getApplication(), canView)).thenReturn(true);
        when(applicationSecurityHelper.canViewApplication(applicationFinance.getApplication(), cantView)).thenReturn(false);

        assertTrue(rules.canViewFinancesIfCanViewApplication(applicationFinance, canView));
        assertFalse(rules.canViewFinancesIfCanViewApplication(applicationFinance, cantView));
    }

    @Test
    public void updateCosts() {
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, collaborator));

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, compAdmin));
    }

    @Test
    public void addCosts() {
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, collaborator));

        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, otherLeadApplicant));
        assertFalse(rules.consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(applicationFinance, compAdmin));

    }

    @Test
    public void consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation() {
        assertTrue(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation() {
        assertTrue(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }

    @Test
    public void consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation() {
        assertTrue(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, leadApplicant));
        assertTrue(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, collaborator));
        assertFalse(rules.consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(applicationFinance, otherLeadApplicant));
    }
    @Test
    public void canViewApplication() {
        UserResource canView = newUserResource().build();
        UserResource cantView = newUserResource().build();

        when(applicationSecurityHelper.canViewApplication(applicationFinance.getApplication(), canView)).thenReturn(true);
        when(applicationSecurityHelper.canViewApplication(applicationFinance.getApplication(), cantView)).thenReturn(false);

        assertTrue(rules.canViewApplication(applicationFinance, canView));
        assertFalse(rules.canViewApplication(applicationFinance, cantView));
    }
}
