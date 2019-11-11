package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.*;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;

import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
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

import static java.time.LocalDate.now;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentBuilder.newCompetitionDocument;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentBuilder.newProjectDocument;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.junit.Assert.*;

@Rollback
@Transactional
public class PartnerOrganisationServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private ProjectPartnerChangeService ProjectPartnerChangeService;

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private PendingPartnerProgressRepository pendingPartnerProgressRepository;

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

    private User projectManager;
    private User projectPartner;
    private Project project;
    private Organisation empire;
    private Organisation ludlow;
    private List<ProjectUser> projectUsers;
    private List<PartnerOrganisation> partnerOrganisations;

    private ProjectFinance projectFinanceEmpire;
    private ProjectFinance projectFinanceLudlow;
    private BankDetails bankDetailsEmpire;
    private BankDetails bankDetailsLudlow;
    private OrganisationAddress organisationAddress;
    private OrganisationAddress organisationAddress2;
    private UserResource webUser;
    private ProjectDocument projectDocument;
    private PendingPartnerProgress pendingPartnerProgress;
    private PendingPartnerProgress pendingPartnerProgress1;
    private EligibilityProcess eligibilityProcess;
    private Process process;
    private User user;

    @Before
    public void setup() {
        loginCompAdmin();

        projectManager = userRepository.findByEmail("steve.smith@empire.com").get();
        projectPartner = userRepository.findByEmail("jessica.doe@ludlow.co.uk").get();
        empire = organisationRepository.findOneByName("Empire Ltd");
        ludlow = organisationRepository.findOneByName("Ludlow");
        projectUsers = newProjectUser()
                .withRole(PROJECT_MANAGER, PROJECT_PARTNER)
                .withUser(projectManager, projectPartner)
                .withStatus(ACCEPTED, PENDING)
                .build(2);
        project = newProject()
                .withApplication(newApplication().withId(1L).build())
                .withDateSubmitted(ZonedDateTime.now())
                .withDuration(6L)
                .withName("My test project")
                .withAddress(newAddress().withAddressLine1("2 Polaris House").withAddressLine2("Swindon").withPostcode("SN2 1EU").build())
                .withOtherDocumentsApproved(ApprovalType.UNSET)
                .withTargetStartDate(now())
                .withProjectUsers(projectUsers)
                .withSpendProfileSubmittedDate(ZonedDateTime.now())
                .build();
        projectRepository.save(project);

        partnerOrganisations = newPartnerOrganisation()
                .withProject(project)
                .withOrganisation(empire, ludlow)
                .withId(30L, 40L)
                .withLeadOrganisation(true, false)
                .build(2);

        partnerOrganisationRepository.save(partnerOrganisations.get(0));
        partnerOrganisationRepository.save(partnerOrganisations.get(1));

        pendingPartnerProgress = new PendingPartnerProgress(partnerOrganisations.get(0));
        pendingPartnerProgress1 = new PendingPartnerProgress(partnerOrganisations.get(1));

        projectFinanceEmpire = newProjectFinance()
                .withProject(project)
                .withOrganisation(empire)
                .build();
        projectFinanceLudlow = newProjectFinance()
                .withProject(project)
                .withOrganisation(ludlow)
                .build();
        projectFinanceRepository.save(projectFinanceEmpire);
        projectFinanceRepository.save(projectFinanceLudlow);
        organisationAddress = newOrganisationAddress()
                .withOrganisation(empire)
                .withAddress(newAddress()
                        .withAddressLine1("45 Happy Street").withAddressLine2("BH2 1UR").build())
                .withAddressType(newAddressType().build())
                .build();
        organisationAddress2 = newOrganisationAddress()
                .withOrganisation(empire)
                .withAddress(newAddress()
                        .withAddressLine1("8 Strength Street").withAddressLine2("BH12 5SL").build())
                .withAddressType(newAddressType().build())
                .build();
        bankDetailsEmpire = newBankDetails()
                .withOrganisation(empire)
                .withSortCode("100006")
                .withAccountNumber("98765432")
                .withOrganiationAddress(organisationAddress)
                .withProject(project)
                .build();
        bankDetailsLudlow = newBankDetails()
                .withOrganisation(ludlow)
                .withSortCode("120034")
                .withAccountNumber("1200146")
                .withOrganiationAddress(organisationAddress2)
                .withProject(project)
                .build();
        bankDetailsRepository.save(bankDetailsEmpire);
        bankDetailsRepository.save(bankDetailsLudlow);
        projectDocument = newProjectDocument()
                .withProject(project)
                .withCompetitionDocument(newCompetitionDocument()
                        .build())
                .withFileEntry(newFileEntry()
                        .build())
                .withStatus(DocumentStatus.APPROVED)
                .build();
        projectProcessRepository.save(newProjectProcess().withProject(project).withActivityState(SETUP).build());
        eligibilityProcessRepository.save(new EligibilityProcess(projectUsers.get(0), partnerOrganisations.get(0), EligibilityState.REVIEW));
        viabilityProcessRepository.save(new ViabilityProcess(projectUsers.get(0), partnerOrganisations.get(0), ViabilityState.REVIEW));
        projectDetailsProcessRepository.save(new ProjectDetailsProcess(projectUsers.get(0), project, ProjectDetailsState.PENDING));
        spendProfileProcessRepository.save(new SpendProfileProcess(projectUsers.get(0), project, SpendProfileState.PENDING));
    }

    @Test
    public void getProjectPartnerOrganisations() {
        ServiceResult<List<PartnerOrganisationResource>> result = partnerOrganisationService.getProjectPartnerOrganisations(project.getId());

        assertTrue(result.isSuccess());
        assertEquals("Empire Ltd", result.getSuccess().get(0).getOrganisationName());
        assertEquals("Ludlow", result.getSuccess().get(1).getOrganisationName());
    }

    @Test
    public void getPartnerOrganisation() {
        ServiceResult<PartnerOrganisationResource> result = partnerOrganisationService.getPartnerOrganisation(project.getId(), empire.getId());
        ServiceResult<PartnerOrganisationResource> result2 = partnerOrganisationService.getPartnerOrganisation(project.getId(), ludlow.getId());

        assertTrue(result.isSuccess());
        assertEquals("Empire Ltd", result.getSuccess().getOrganisationName());
        assertEquals("Ludlow", result2.getSuccess().getOrganisationName());
    }

//    @Test
//    public void removePartnerOrganisation() {
//        ServiceResult<Void> result = partnerOrganisationService.removePartnerOrganisation(new ProjectOrganisationCompositeId(project.getId(), ludlow.getId()));
//
//        assertTrue(result.isSuccess());
//    }
}