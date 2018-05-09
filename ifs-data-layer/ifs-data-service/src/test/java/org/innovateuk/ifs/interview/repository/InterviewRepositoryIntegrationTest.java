package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.builder.InterviewBuilder.newInterview;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.*;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertThat;

public class InterviewRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InterviewRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    private User assessor;
    private User otherAssessor;
    private Organisation organisation;
    private Competition competition;
    private Application application1;
    private Application application2;

    @Before
    public void setup() {
        assessor = userRepository.findByEmail("paul.plum@gmail.com")
                .orElseThrow(() -> new IllegalStateException("Expected to find test user for email paul.plum@gmail.com"));
        otherAssessor = userRepository.findByEmail("felix.wilson@gmail.com")
                .orElseThrow(() -> new IllegalStateException("Expected to find test user for email felix.wilson@gmail.com"));

        organisation = newOrganisation()
                .withName("orgName")
                .build();

        organisationRepository.save(organisation);

        competition = newCompetition()
                .with(id(null))
                .build();

        competitionRepository.save(competition);

        application1 = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();
        application2 = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();
        Application unassignedApplication = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();

        applicationRepository.save(asList(application1, application2, unassignedApplication));

        ProcessRole assessorRole1 = newProcessRole()
                .with(id(null))
                .withUser(assessor)
                .withApplication(application1)
                .build();
        ProcessRole assessorRole2 = newProcessRole()
                .with(id(null))
                .withUser(assessor)
                .withApplication(application2)
                .build();
        ProcessRole otherAssessorRole1 = newProcessRole()
                .with(id(null))
                .withUser(otherAssessor)
                .withApplication(application1)
                .build();
        ProcessRole otherAssessorRole2 = newProcessRole()
                .with(id(null))
                .withUser(otherAssessor)
                .withApplication(application2)
                .build();
        ProcessRole lead1 = newProcessRole()
                .with(id(null))
                .withRole(Role.LEADAPPLICANT)
                .withApplication(application1)
                .withOrganisationId(organisation.getId())
                .build();
        ProcessRole lead2 = newProcessRole()
                .with(id(null))
                .withRole(Role.LEADAPPLICANT)
                .withApplication(application2)
                .withOrganisationId(organisation.getId())
                .build();

        processRoleRepository.save(asList(assessorRole1, assessorRole2, otherAssessorRole1, otherAssessorRole2, lead1, lead2));

        InterviewAssignment created = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .withTarget(unassignedApplication)
                .build();

        InterviewAssignment submitted = newInterviewAssignment()
                .with(id(null))
                .withState(AWAITING_FEEDBACK_RESPONSE)
                .withTarget(application1)
                .build();

        InterviewAssignment responded = newInterviewAssignment()
                .with(id(null))
                .withState(SUBMITTED_FEEDBACK_RESPONSE)
                .withTarget(application2)
                .build();

        interviewAssignmentRepository.save(asList(created, submitted, responded));

        Interview assignedInterview = newInterview()
                .with(id(null))
                .withState(InterviewState.ACCEPTED)
                .withParticipant(assessorRole2)
                .withTarget(application2)
                .build();

        Interview notAssignedInterview = newInterview()
                .with(id(null))
                .withState(InterviewState.ACCEPTED)
                .withParticipant(otherAssessorRole1)
                .withTarget(application1)
                .build();

        repository.save(asList(assignedInterview, notAssignedInterview));

        flushAndClearSession();

    }

    @Autowired
    @Override
    protected void setRepository(InterviewRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findApplicationsNotAssignedToAssessor() {
        Pageable pageable = new PageRequest(0, 20);

        Page<InterviewApplicationResource> page = repository.findApplicationsNotAssignedToAssessor(competition.getId(), assessor.getId(), pageable);

        assertThat(page.getTotalElements(), is(equalTo(1L)));

        InterviewApplicationResource content = page.getContent().get(0);

        assertThat(content.getId(), is(equalTo(application1.getId())));
        assertThat(content.getLeadOrganisation(), is(equalTo(organisation.getName())));
        assertThat(content.getNumberOfAssessors(), is(equalTo(1L)));
    }

    @Test
    public void findApplicationsAssignedToAssessor() {
        Pageable pageable = new PageRequest(0, 20);

        Page<InterviewApplicationResource> page = repository.findApplicationsAssignedToAssessor(competition.getId(), assessor.getId(), pageable);

        assertThat(page.getTotalElements(), is(equalTo(1L)));

        InterviewApplicationResource content = page.getContent().get(0);

        assertThat(content.getId(), is(equalTo(application2.getId())));
        assertThat(content.getLeadOrganisation(), is(equalTo(organisation.getName())));
        assertThat(content.getNumberOfAssessors(), is(equalTo(1L)));
    }

    @Test
    public void countUnallocatedApplications() {
        long count = repository.countUnallocatedApplications(competition.getId(), assessor.getId());

        assertThat(count, is(equalTo(1L)));
    }

    @Test
    public void countAllocatedApplications() {
        long count = repository.countAllocatedApplications(competition.getId(), assessor.getId());

        assertThat(count, is(equalTo(1L)));
    }
}