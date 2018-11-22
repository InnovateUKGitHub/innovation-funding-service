package org.innovateuk.ifs.grant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterFinanceTotalsTablePopulator.GRANT_CLAIM_IDENTIFIER;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GrantMapperTest {
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2018, 1,2);
    private static final ZonedDateTime DEFAULT_GOL_DATE = ZonedDateTime
            .of(LocalDate.of(2018, 3,4), LocalTime.MIDNIGHT, ZoneId.of("GMT"));
    private static final boolean OUTPUT_TEST_JSON = false;
    private static final String OUTPUT_DIRECTORY = "./build/tmp/grant-mapper-json";

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

    @InjectMocks
    protected GrantMapper grantMapper = new GrantMapper();

    @Before
    public void setupMockInjection() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMap() throws IOException {
        Parameter parameter = parameters().iterator().next();
        Project project = parameter.project();
        when(formInputResponseRepository.findByApplicationId(project.getApplication().getId()))
                .thenReturn(parameter.formInputResponses());
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(any(), any()))
                .thenReturn(Optional.of(parameter.spendProfile()));
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
        Optional<Participant> participant = grant.getParticipants().stream()
                .filter(it -> it.getId() == 1).findFirst();
        assertThat(participant.isPresent(), Is.is(true));
        participant.ifPresent(it ->
                assertThat(it.getContactEmail(), equalTo("user@test.com"))
                        );
    }

    @Parameterized.Parameters
    public static Collection<Parameter> parameters() {
        return Arrays.asList(
                newParameter("basic", newProject())
        );
    }

    private static Parameter newParameter(String name, ProjectBuilder projectBuilder) {
        long applicationId = 1L;
        long competitionId = 2L;
        long projectId = 1L;
        long duration = 12;
        int costCategoryCount = 2;
        int organisationCount = 3;
        int userCount = 3;
        return new Parameter().project(projectBuilder
                .withDuration(duration)
                .withId(projectId)
                .withTargetStartDate(DEFAULT_START_DATE)
                .withOfferSubmittedDate(DEFAULT_GOL_DATE)
                .withPartnerOrganisations(
                        Stream.iterate(0, i -> i + 1).limit(organisationCount)
                                .map(i -> new PartnerOrganisation(null, createOrganisation(i), i == 0))
                                .collect(Collectors.toList())
                ).withApplication(
                        newApplication()
                                .withId(applicationId)
                                .withCompetition(
                                        newCompetition().withId(competitionId).build())
                                .build())
                .withProjectUsers(
                        Stream.iterate(0, i -> i + 1).limit(organisationCount * userCount)
                            .map(i -> createProjectUser(i, organisationCount))
                            .collect(Collectors.toList())
                ).build())
                .participantCount(organisationCount)
                .costCategoryCount(costCategoryCount)
                .name(name)
                .applicationId(applicationId)
                .competitionId(competitionId);
    }

    private static Organisation createOrganisation(long id) {
        Organisation organisation = mock(Organisation.class);
        when(organisation.getId()).thenReturn(id);
        OrganisationType organisationType = mock(OrganisationType.class);
        when(organisationType.getName()).thenReturn("Business");
        when(organisation.getOrganisationType()).thenReturn(organisationType);
        return organisation;
    }

    private static ProjectUser createProjectUser(long id, long organisationCount) {
        ProjectUser projectUser = mock(ProjectUser.class);
        when(projectUser.getRole()).thenReturn(
                id % organisationCount == 0 ? ProjectParticipantRole.PROJECT_FINANCE_CONTACT : ProjectParticipantRole.PROJECT_MANAGER);
        Organisation organisation = mock(Organisation.class);
        when(organisation.getId()).thenReturn(id / organisationCount);
        when(projectUser.getOrganisation()).thenReturn(organisation);
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("user@test.com");
        when(projectUser.getUser()).thenReturn(user);
        return projectUser;
    }

    private static class Parameter {
        private Project project;
        private String name;
        private List<FormInputResponse> formInputResponses = new ArrayList<>();
        private long competitionId;
        private long applicationId;
        private String projectSummary;
        private String publicDescription;
        private int participantCount;
        private int costCategoryCount;
        private int count;

        private Parameter project(Project project) {
            this.project = project;
            return this;
        }

        private Project project() {
            return project;
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
            this.participantCount = participantCount;
            return this;
        }

        private int participantCount() {
            return participantCount;
        }

        private Parameter costCategoryCount(int costCategoryCount) {
            this.costCategoryCount = costCategoryCount;
            return this;
        }

        private int costCategoryCount() {
            return costCategoryCount;
        }

        private Parameter count(int count) {
            this.count = count;
            participantCount(count);
            return this;
        }

        private int count() {
            return count;
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

        private SpendProfile spendProfile() {
            List<Cost> eligibleCosts = new ArrayList<>();
            List<Cost> spendProfileFigures =
                    Stream.iterate(0, i -> i + 1).limit(participantCount * costCategoryCount)
                            .map(i -> new Cost(BigDecimal.valueOf(10))
                                    .withTimePeriod(i, null, null, null)
                                    .withCategory(new CostCategory("Overheads")))
                            .collect(Collectors.toList());
            return new SpendProfile(null, null, null, eligibleCosts, spendProfileFigures, null, null, true);
        }
    }
}
