package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.LocalDate.now;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.junit.Assert.assertEquals;

@Transactional
@Rollback
public class PartnerOrganisationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<PartnerOrganisationRepository> {

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

    private User projectManager;
    private User projectPartner;
    private Project project;
    private Organisation empire;
    private Organisation ludlow;
    private List<ProjectUser> projectUsers;
    private List<PartnerOrganisation> partnerOrganisations;
    private ProjectFinance projectFinanceEmpire;
    private ProjectFinance projectFinanceLudlow;

    @Autowired
    @Override
    protected void setRepository(final PartnerOrganisationRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        projectManager = userRepository.findByEmail("steve.smith@empire.com").get();
        projectPartner = userRepository.findByEmail("jessica.doe@ludlow.co.uk").get();
        empire = organisationRepository.findOneByName("Empire Ltd");
        ludlow = organisationRepository.findOneByName("Ludlow");
        projectUsers = newProjectUser()
                .withRole(PROJECT_MANAGER, PROJECT_PARTNER)
                .withUser(projectManager, projectPartner)
                .build(2);
        project = newProject()
                .withApplication(newApplication().withId(1L).build())
                .withDateSubmitted(ZonedDateTime.now())
                .withDuration(6L)
                .withName("My test project")
                .withAddress(newAddress().withAddressLine1("2 Polaris House").withAddressLine2("Swindon").withPostcode("SN2 1EU").build())
                .withOtherDocumentsApproved(ApprovalType.APPROVED)
                .withTargetStartDate(now())
                .withProjectUsers(projectUsers)
                .build();
        projectRepository.save(project);
        partnerOrganisations = newPartnerOrganisation()
                .withProject(project)
                .withId(30L, 40L)
                .withOrganisation(empire, ludlow)
                .withLeadOrganisation(true, false)
                .build(2);
        repository.save(partnerOrganisations.get(0));
        repository.save(partnerOrganisations.get(1));
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
    }

    @Test
    public void getProjectPartnerOrganisations() {
        List<PartnerOrganisation> partners = repository.findByProjectId(project.getId());

        assertEquals(empire, partners.get(0).getOrganisation());
        assertEquals(ludlow, partners.get(1).getOrganisation());
    }

    @Test
    public void deleteOneByProjectIdAndOrganisationId() {
        repository.deleteOneByProjectIdAndOrganisationId(project.getId(), ludlow.getId());

        assertEquals(1, repository.findByProjectId(project.getId()).size());
    }
}