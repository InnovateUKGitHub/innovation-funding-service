package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.cofunder.domain.builder.CofunderAssignmentBuilder.newCofunderAssignment;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertThat;

@Rollback
@Transactional
    public class CofunderDashboardServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private CofunderDashboardService cofunderDashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CofunderAssignmentRepository cofunderAssignmentRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    public void findApplicationsNeedingCofunders() {
        loginSteveSmith();
        TestData data = setupTestData();
        setLoggedInUser(newUserResource().withRoleGlobal(Role.COFUNDER).withId(data.cofunder.getId()).build());

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("activityState"));

        CofunderDashboardApplicationPageResource result = cofunderDashboardService.getApplicationsForCofunding(data.cofunder.getId(), data.competition.getId(), pageRequest).getSuccess();

        assertThat(result.getTotalElements(), equalTo(3L));
        assertThat(result.getContent().get(0).getName(), equalTo("3"));
        assertThat(result.getContent().get(0).getLead(), equalTo("3"));
        assertThat(result.getContent().get(0).getState(), equalTo(CofunderState.CREATED));

        assertThat(result.getContent().get(1).getName(), equalTo("2"));
        assertThat(result.getContent().get(1).getLead(), equalTo("2"));
        assertThat(result.getContent().get(1).getState(), equalTo(CofunderState.ACCEPTED));

        assertThat(result.getContent().get(2).getName(), equalTo("1"));
        assertThat(result.getContent().get(2).getLead(), equalTo("1"));
        assertThat(result.getContent().get(2).getState(), equalTo(CofunderState.REJECTED));
    }


    private TestData setupTestData() {
        Competition competition = competitionRepository.save(newCompetition().withId(null).build());
        List<Application> applications =
                newArrayList(applicationRepository.saveAll(newApplication().withName("1", "2", "3").withId(null).withCompetition(competition).build(3)));
        List<Organisation> organisations =
                newArrayList(organisationRepository.saveAll(newOrganisation().withName("1", "2", "3").withId(null).build(3)));

        processRoleRepository.saveAll(
                newProcessRole().withApplication(toArray(applications, Application.class))
                        .withOrganisation(toArray(organisations, Organisation.class))
                        .withUser(userRepository.save(newUser().withId(null).withEmailAddress("asd@gmail").withUid("asdasd").build()))
                        .withRole(Role.LEADAPPLICANT)
                        .build(3)
        );

        User cofunder = userRepository.save(newUser()
                .withId(null)
                .withUid("1")
                .withEmailAddress("cofunder1@gmail.com")
                .withFirstName("Bob")
                .withLastName("Bobberson")
                .withRoles(newHashSet(Role.COFUNDER))
                .build());

        cofunderAssignmentRepository.saveAll(newCofunderAssignment()
                .withParticipant(cofunder)
                .withApplication(toArray(applications, Application.class))
                .withProcessState(CofunderState.REJECTED, CofunderState.ACCEPTED, CofunderState.CREATED)
                .build(3));

        flushAndClearSession();

        return new TestData(competition, applications, cofunder);
    }

    private class TestData {
        Competition competition;
        List<Application> applications;
        User cofunder;

        public TestData(Competition competition, List<Application> applications, User cofunder) {
            this.competition = competition;
            this.applications = applications;
            this.cofunder = cofunder;
        }
    }
}
