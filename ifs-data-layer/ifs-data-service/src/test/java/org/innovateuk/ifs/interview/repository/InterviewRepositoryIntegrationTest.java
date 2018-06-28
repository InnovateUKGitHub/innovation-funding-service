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
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.builder.InterviewBuilder.newInterview;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.CREATED;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    private Application application3;

    @Autowired
    @Override
    protected void setRepository(InterviewRepository repository) {
        this.repository = repository;
    }

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
        application3 = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();
        Application unassignedApplication = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .build();

        applicationRepository.saveAll(asList(application1, application2, application3, unassignedApplication));

        ProcessRole assessorRole1 = newProcessRole()
                .with(id(null))
                .withRole(Role.INTERVIEW_ASSESSOR)
                .withUser(assessor)
                .withApplication(application1)
                .build();
        ProcessRole assessorRole2 = newProcessRole()
                .with(id(null))
                .withRole(Role.INTERVIEW_ASSESSOR)
                .withUser(assessor)
                .withApplication(application2)
                .build();
        ProcessRole assessorRole3 = newProcessRole()
                .with(id(null))
                .withRole(Role.INTERVIEW_ASSESSOR)
                .withUser(assessor)
                .withApplication(application2)
                .build();
        ProcessRole otherAssessorRole1 = newProcessRole()
                .with(id(null))
                .withRole(Role.INTERVIEW_ASSESSOR)
                .withUser(otherAssessor)
                .withApplication(application1)
                .build();
        ProcessRole otherAssessorRole2 = newProcessRole()
                .with(id(null))
                .withRole(Role.INTERVIEW_ASSESSOR)
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

        processRoleRepository.saveAll(asList(assessorRole1, assessorRole2, assessorRole3, otherAssessorRole1, otherAssessorRole2, lead1, lead2));

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

        interviewAssignmentRepository.saveAll(asList(created, submitted, responded));

        Interview assignedInterview = newInterview()
                .with(id(null))
                .withState(InterviewState.ASSIGNED)
                .withParticipant(assessorRole2)
                .withTarget(application2)
                .build();

        Interview assignedInterview2 = newInterview()
                .with(id(null))
                .withState(InterviewState.ASSIGNED)
                .withParticipant(assessorRole3)
                .withTarget(application2)
                .build();

        Interview notAssignedInterview = newInterview()
                .with(id(null))
                .withState(InterviewState.ASSIGNED)
                .withParticipant(otherAssessorRole1)
                .withTarget(application1)
                .build();

        repository.saveAll(asList(assignedInterview, notAssignedInterview, assignedInterview2));

        flushAndClearSession();
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
        assertThat(content.getNumberOfAssessors(), is(equalTo(2L)));
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

    @Test
    public void findAllNotified() {
        List<InterviewApplicationResource> interviewApplicationResources = repository.findAllNotified( asList(application1.getId(), application2.getId()) );

        InterviewApplicationResource expectedInterviewApplicationResource1 = new InterviewApplicationResource(application1.getId(), application1.getName(), organisation.getName(), 1);
        InterviewApplicationResource expectedInterviewApplicationResource2 = new InterviewApplicationResource(application2.getId(), application2.getName(), organisation.getName(), 2);

        assertEquals(2, interviewApplicationResources.size());
        assertEquals(expectedInterviewApplicationResource1, interviewApplicationResources.get(0));
        assertEquals(expectedInterviewApplicationResource2, interviewApplicationResources.get(1));
    }

    @Test
    public void unallocateApplicationFromAssessor() {

        List<Interview> allocated = repository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(assessor.getId(), competition.getId());
        assertEquals(2, allocated.size());

        repository.deleteOneByParticipantUserIdAndTargetId(assessor.getId(), application2.getId());

        List<Interview> allocatedAfterDeletion = repository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(assessor.getId(), competition.getId());
        assertTrue(allocatedAfterDeletion.isEmpty());
    }
}