package org.innovateuk.ifs.grant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterFinanceTotalsTablePopulator.GRANT_CLAIM_IDENTIFIER;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class GrantMapperTest {
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2018, 1,2);
    private static final ZonedDateTime DEFAULT_GOL_DATE = ZonedDateTime
            .of(LocalDate.of(2018, 3,4), LocalTime.MIDNIGHT, ZoneId.of("GMT"));
    private static final String OVERHEADS = "Overheads";
    private static final boolean OUTPUT_TEST_JSON = true;
    private static final String OUTPUT_DIRECTORY = "./build/tmp/grant-mapper-json";
    private final Parameter parameter;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Mock
    private SpendProfileRepository spendProfileRepository;

    @Mock
    private FormInputResponseRepository formInputResponseRepository;

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @InjectMocks
    protected GrantMapper grantMapper = new GrantMapper();

    @Before
    public void setupMockInjection() {
        MockitoAnnotations.initMocks(this);
    }

    public GrantMapperTest(Parameter parameter) {
        this.parameter = parameter;
    }

    @Test
    public void testMap() throws IOException {

        Project project = parameter.createProject();

        when(formInputResponseRepository.findByApplicationId(project.getApplication().getId()))
                .thenReturn(parameter.formInputResponses());
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(any(), any()))
                .thenAnswer(i -> Optional.of(parameter.createSpendProfile()));
        ApplicationFinance applicationFinance = mock(ApplicationFinance.class);
        when(applicationFinance.getMaximumFundingLevel()).thenReturn(50);
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(any(), any()))
                .thenReturn(applicationFinance);
        ProjectFinance projectFinance = mock(ProjectFinance.class);
        when(projectFinance.getOrganisationSize()).thenReturn(OrganisationSize.MEDIUM);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(any(), any()))
                .thenReturn(projectFinance);
        ProjectFinanceRow projectFinanceRow = mock(ProjectFinanceRow.class);
        when(projectFinanceRow.getName()).thenReturn(GRANT_CLAIM_IDENTIFIER);
        when(projectFinanceRow.getQuantity()).thenReturn(30);
        when(projectFinanceRowRepository.findByTargetId(any()))
                .thenReturn(Collections.singletonList(projectFinanceRow));

        List<InnovationLead> innovationLeads = newInnovationLead().
                withUser(newUser().withEmailAddress("il1@example.com", "il2@example.com").buildArray(2, User.class)).
                build(2);

        when(innovationLeadRepository.getByCompetitionIdAndRole(project.getApplication().getCompetition().getId(),
                CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(innovationLeads);

        Grant grant = grantMapper.mapToGrant(project);

        if (OUTPUT_TEST_JSON) {
            File outDirectory = new File(OUTPUT_DIRECTORY);
            if (!outDirectory.exists()) {
                assertTrue(outDirectory.mkdir());
            }
            Files.write(Paths.get(OUTPUT_DIRECTORY + "/grant-" + parameter.name() + ".json"),
                    new ObjectMapper().writeValueAsString(grant).getBytes());
        }

        assertThat(grant.getId(), equalTo(parameter.applicationId()));
        assertThat(grant.getCompetitionCode(), equalTo(parameter.competitionId()));
        assertThat(grant.getPublicDescription(), equalTo(parameter.publicDescription()));
        assertThat(grant.getSummary(), equalTo(parameter.projectSummary()));
        assertThat(grant.getStartDate(), equalTo(DEFAULT_START_DATE));
        assertThat(grant.getGrantOfferLetterDate(), equalTo(DEFAULT_GOL_DATE));
        assertThat(grant.getSourceSystem(), equalTo("IFS"));

        // expect 1 Project Manager record, one Finance Contact record for each Organisation and 1 innovation lead record
        int expectedNumberOfParticipantRecords = 1 + (parameter.partnerOrganisationCount) + 1;

        assertThat(grant.getParticipants(), hasSize(expectedNumberOfParticipantRecords));

        Participant projectManagerParticipant = getOnlyElement(simpleFilter(grant.getParticipants(),
                participant -> Role.PROJECT_MANAGER.getName().equals(participant.getContactRole())));

        List<Participant> financeContactParticipants = simpleFilter(grant.getParticipants(),
                participant -> Role.PROJECT_FINANCE.getName().equals(participant.getContactRole()));

        Participant innovationLeadParticipant = getOnlyElement(simpleFilter(grant.getParticipants(),
                participant -> Role.INNOVATION_LEAD.getName().equals(participant.getContactRole())));

        assertThat(projectManagerParticipant.getContactEmail(), equalTo("pm@example.com"));
        assertThat(innovationLeadParticipant.getContactEmail(), equalTo("il1@example.com"));

        forEachWithIndex(financeContactParticipants, (i, participant) -> {

            assertThat(participant.getForecasts().size(), equalTo(parameter.costCategoryCount()));
            Forecast overheads = participant.getForecasts().stream()
                    .filter(forecast -> OVERHEADS.equals(forecast.getCostCategory()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);

            assertThat(overheads.getPeriods().size(), equalTo(parameter.duration()));

            if (parameter.expectedOverheads().size() > i) {
                assertThat(overheads.getCost(), equalTo(parameter.expectedOverheads().get(i)));
            }
            if (parameter.expectedOverheadRates().size() > i) {
                assertThat(participant.getOverheadRate().longValue(), equalTo(parameter.expectedOverheadRates().get(i)));
            }
        });
    }

    @Parameterized.Parameters
    public static Collection<Parameter> parameters() {
        return asList(
                newParameter("basic", newProject()),
                newParameter("single", newProject()).duration(1).expectedOverheads(10L)

        );
    }

    private static Parameter newParameter(String name, ProjectBuilder projectBuilder) {
        return new Parameter().name(name).projectBuilder(projectBuilder);
    }

    private static class Parameter {
        private ProjectBuilder projectBuilder;
        private String name;
        private List<FormInputResponse> formInputResponses = new ArrayList<>();
        private long competitionId = 2L;
        private long applicationId = 1L;
        private long projectId = 1L;
        private String projectSummary;
        private String publicDescription;
        private int duration = 12;
        private int partnerOrganisationCount = 3;
        private int costCategoryCount = 2;
        private int userCount = 3;
        private int value = 10;
        private List<Long> expectedOverheads = Collections.singletonList(120L);
        private List<Long> expectedOverheadRates = Collections.singletonList(50L);

        private Parameter projectBuilder(ProjectBuilder projectBuilder) {
            this.projectBuilder = projectBuilder;
            return this;
        }

        private ProjectBuilder projectBuilder() {
            return projectBuilder;
        }

        private Parameter applicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        private long applicationId() {
            return applicationId;
        }

        private Parameter competitionId(long competitionId) {
            this.competitionId = competitionId;
            return this;
        }

        private long competitionId() {
            return competitionId;
        }

        private Parameter name(String name) {
            this.name = name;
            return this;
        }

        private String name() {
            return name;
        }

        private Parameter participantCount(int participantCount) {
            this.partnerOrganisationCount = participantCount;
            return this;
        }

        private int participantCount() {
            return partnerOrganisationCount;
        }

        private Parameter costCategoryCount(int costCategoryCount) {
            this.costCategoryCount = costCategoryCount;
            return this;
        }

        private int costCategoryCount() {
            return costCategoryCount;
        }

        private Parameter duration(int duration) {
            this.duration = duration;
            return this;
        }

        private int duration() {
            return duration;
        }

        private Parameter expectedOverheads(Long... expectedOverheads) {
            this.expectedOverheads = asList(expectedOverheads);
            return this;
        }

        private List<Long> expectedOverheads() {
            return expectedOverheads;
        }

        private List<Long> expectedOverheadRates() {
            return expectedOverheadRates;
        }

        private Parameter expectedOverheadRates(Long... expectedOverheadRates) {
            this.expectedOverheadRates = asList(expectedOverheadRates);
            return this;
        }


        private String publicDescription() {
            return publicDescription == null ? name + " public description" : publicDescription;
        }

        private String projectSummary() {
            return publicDescription == null ? name + " project summary" : projectSummary;
        }

        private List<FormInputResponse> formInputResponses() {
            List<FormInputResponse>  formInputResponses = new ArrayList<>();
            formInputResponses.add(createFormInputResponse("project summary"));
            formInputResponses.add(createFormInputResponse("public description"));
            return formInputResponses;
        }

        private FormInputResponse createFormInputResponse(String description) {
            return createFormInputResponse(description, name + " " + description);
        }

        private FormInputResponse createFormInputResponse(String description, String value) {
            FormInputResponse formInputResponse = new FormInputResponse();
            FormInput formInput = new FormInput();
            formInput.setDescription(description);
            formInputResponse.setFormInput(formInput);
            formInputResponse.setValue(value);
            return formInputResponse;
        }

        private SpendProfile createSpendProfile() {
            List<Cost> eligibleCosts = new ArrayList<>();
            List<Cost> spendProfileFigures = new ArrayList<>();
            for (int costCategoryIndex = 0 ; costCategoryIndex < costCategoryCount ; costCategoryIndex++ ) {
                for (int durationIndex = 0 ; durationIndex < duration ; durationIndex++ ) {
                    spendProfileFigures.add(new Cost(BigDecimal.valueOf(value))
                            .withTimePeriod(durationIndex, null, null, null)
                            .withCategory(new CostCategory(costCategoryIndex == 0
                                    ? OVERHEADS : "cost-" + costCategoryIndex)));
                }
            }
            return new SpendProfile(null, null, null, eligibleCosts, spendProfileFigures, null, null, true);
        }

        private Project createProject() {

            List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation().
                    withOrganisation(newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build()).
                    withLeadOrganisation(true, false).
                    build(partnerOrganisationCount);

            List<ProjectUser> leadOrganisationProjectUsers = newProjectUser().
                    withOrganisation(partnerOrganisations.get(0).getOrganisation()).
                    withRole(PROJECT_MANAGER, PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).
                    withUser(combineLists(
                            newUser().withEmailAddress("pm@example.com", "fc1@example.com").build(2),
                            newUser().build(userCount - 2)).toArray(new User[] {}
                    )).
                    build(userCount);

            List<ProjectUser> org2ProjectUsers = newProjectUser().
                    withOrganisation(partnerOrganisations.get(1).getOrganisation()).
                    withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).
                    withUser(combineLists(
                            newUser().withEmailAddress("fc2@example.com").build(1),
                            newUser().build(userCount - 1)).toArray(new User[] {}
                    )).
                    build(userCount);

            List<ProjectUser> org3ProjectUsers = newProjectUser().
                    withOrganisation(partnerOrganisations.get(2).getOrganisation()).
                    withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).
                    withUser(combineLists(
                            newUser().withEmailAddress("fc3@example.com").build(1),
                            newUser().build(userCount - 1)).toArray(new User[] {}
                    )).
                    build(userCount);

            List<ProjectUser> projectUsers = combineLists(leadOrganisationProjectUsers, org2ProjectUsers, org3ProjectUsers);

            return projectBuilder
                    .withDuration((long) duration)
                    .withId(projectId)
                    .withTargetStartDate(DEFAULT_START_DATE)
                    .withOfferSubmittedDate(DEFAULT_GOL_DATE)
                    .withPartnerOrganisations(partnerOrganisations)
                    .withApplication(
                        newApplication()
                                .withId(applicationId)
                                .withCompetition(
                                        newCompetition().withId(competitionId).build())
                                .build())
                    .withProjectUsers(projectUsers)
                    .build();
        }
    }
}
