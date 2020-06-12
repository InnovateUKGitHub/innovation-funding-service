package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.*;

import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.domain.GOLProcess;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static java.time.LocalDate.now;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.junit.Assert.*;

@Transactional
public class PartnerOrganisationServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private EligibilityProcessRepository eligibilityProcessRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private ViabilityProcessRepository viabilityProcessRepository;

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Autowired
    private SpendProfileProcessRepository spendProfileProcessRepository;

    @Autowired
    private GrantOfferLetterProcessRepository GOLProcessRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Project project;
    private Organisation empire;
    private Organisation ludlow;
    private User projectFinanceUser;
    private UserResource projectFinanceUserResource;

    @Before
    public void setup() {
        User webUser = userMapper.mapToDomain(getSystemRegistrationUser());
        projectFinanceUser = newUser()
                .with(id(null))
                .withEmailAddress("lee.bowman@innovateuk.test")
                .withRoles(singleton(PROJECT_FINANCE))
                .withUid("uid1200000012")
                .withCreatedOn(ZonedDateTime.now())
                .withCreatedBy(webUser)
                .withModifiedBy(webUser)
                .withModifiedOn(ZonedDateTime.now())
                .build();

        Application application = applicationRepository.findById(1L).get();
        User projectManager = userRepository.findByEmail("steve.smith@empire.com").get();
        User projectPartner = userRepository.findByEmail("jessica.doe@ludlow.co.uk").get();
        empire = organisationRepository.findOneByName("Empire Ltd");
        ludlow = organisationRepository.findOneByName("Ludlow");
        List<ProjectUser> projectUsers = newProjectUser()
                .with(id(null))
                .withRole(PROJECT_MANAGER, PROJECT_PARTNER)
                .withUser(projectManager, projectPartner)
                .withOrganisation(empire, ludlow)
                .withStatus(ACCEPTED, PENDING)
                .build(2);
        project = projectRepository.save(newProject()
                .with(id(null))
                .withProjectProcess(null)
                .withApplication(application)
                .withDateSubmitted(ZonedDateTime.now())
                .withDuration(6L)
                .withName("My test project")
                .withAddress(newAddress()
                        .with(id(null))
                        .withAddressLine1("2 Polaris House")
                        .withAddressLine2("Swindon")
                        .withPostcode("SN2 1EU")
                        .build())
                .withOtherDocumentsApproved(ApprovalType.UNSET)
                .withTargetStartDate(now())
                .withProjectUsers(projectUsers)
                .withSpendProfileSubmittedDate(ZonedDateTime.now())
                .build());

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation()
                .with(id(null))
                .withProject(project)
                .withOrganisation(empire, ludlow)
                .withLeadOrganisation(true, false)
                .build(2);
        partnerOrganisationRepository.save(partnerOrganisations.get(0));
        partnerOrganisationRepository.save(partnerOrganisations.get(1));

        project.setProjectProcess(projectProcessRepository.save(newProjectProcess()
                .with(id(null))
                .withProject(project)
                .withProjectUser(projectUsers.get(0))
                .withActivityState(ProjectState.SETUP)
                .build()));
        eligibilityProcessRepository.save(new EligibilityProcess(projectUsers.get(0), partnerOrganisations.get(0), EligibilityState.REVIEW));
        viabilityProcessRepository.save(new ViabilityProcess(projectUsers.get(0), partnerOrganisations.get(0), ViabilityState.REVIEW));
        projectDetailsProcessRepository.save(new ProjectDetailsProcess(projectUsers.get(0), project, ProjectDetailsState.PENDING));
        spendProfileProcessRepository.save(new SpendProfileProcess(projectUsers.get(0), project, SpendProfileState.PENDING));
        GOLProcessRepository.save(new GOLProcess(projectUsers.get(0), project, GrantOfferLetterState.PENDING));

        projectProcessRepository.save(newProjectProcess()
                .with(id(null))
                .withProject(project)
                .withProjectUser(projectUsers.get(1))
                .withActivityState(ProjectState.SETUP)
                .build());
        eligibilityProcessRepository.save(new EligibilityProcess(projectUsers.get(1), partnerOrganisations.get(1), EligibilityState.REVIEW));
        viabilityProcessRepository.save(new ViabilityProcess(projectUsers.get(1), partnerOrganisations.get(1), ViabilityState.REVIEW));
        projectDetailsProcessRepository.save(new ProjectDetailsProcess(projectUsers.get(1), project, ProjectDetailsState.PENDING));
        spendProfileProcessRepository.save(new SpendProfileProcess(projectUsers.get(1), project, SpendProfileState.PENDING));
        GOLProcessRepository.save(new GOLProcess(projectUsers.get(1), project, GrantOfferLetterState.PENDING));
    }

    @Rollback
    @Test
    public void getProjectPartnerOrganisations() {
        loginIfsAdmin();
        ServiceResult<List<PartnerOrganisationResource>> result = partnerOrganisationService.getProjectPartnerOrganisations(project.getId());

        assertTrue(result.isSuccess());
        assertEquals("Empire Ltd", result.getSuccess().get(0).getOrganisationName());
        assertEquals("Ludlow", result.getSuccess().get(1).getOrganisationName());
    }

    @Rollback
    @Test
    public void getPartnerOrganisation() {
        loginIfsAdmin();
        ServiceResult<PartnerOrganisationResource> result = partnerOrganisationService.getPartnerOrganisation(project.getId(), empire.getId());
        ServiceResult<PartnerOrganisationResource> result2 = partnerOrganisationService.getPartnerOrganisation(project.getId(), ludlow.getId());

        assertTrue(result.isSuccess());
        assertEquals("Empire Ltd", result.getSuccess().getOrganisationName());
        assertEquals("Ludlow", result2.getSuccess().getOrganisationName());
    }

    @Rollback
    @Test
    public void removeNonLeadPartnerOrganisation() {
        userRepository.save(projectFinanceUser);
        projectFinanceUserResource = userMapper.mapToResource(projectFinanceUser);
        setLoggedInUser(projectFinanceUserResource);

        ServiceResult<Void> result = partnerOrganisationService.removePartnerOrganisation(new ProjectOrganisationCompositeId(project.getId(), ludlow.getId()));
        assertTrue(result.isSuccess());
    }

    @Rollback
    @Test
    public void removeLeadPartnerOrganisation() {
        projectFinanceUserResource = userMapper.mapToResource(projectFinanceUser);
        setLoggedInUser(projectFinanceUserResource);

        ServiceResult<Void> result = partnerOrganisationService.removePartnerOrganisation(new ProjectOrganisationCompositeId(project.getId(), empire.getId()));
        assertFalse(result.isSuccess());
    }
}