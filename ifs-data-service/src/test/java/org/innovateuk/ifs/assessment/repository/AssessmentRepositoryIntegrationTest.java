package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.Process;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Collectors;

import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.*;

public class AssessmentRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessmentRepository> {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;

    @Autowired
    @Override
    protected void setRepository(final AssessmentRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setUp() throws Exception {
        user = userRepository.findByEmail("paul.plum@gmail.com")
                .orElseThrow(() -> new IllegalStateException("Expected to find test user for email paul.plum@gmail.com"));
    }

    @Test
    @Rollback
    public void findAll() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        ProcessRole participant1 = processRoleRepository.save(newProcessRole()
                .with(id(null))
                .build());

        ProcessRole participant2 = processRoleRepository.save(newProcessRole()
                .with(id(null))
                .build());

        Application application = applicationRepository.findOne(1L);

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        List<Assessment> assessments = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(participant1, participant2)
                .withActivityState(openState)
                .build(2);

        Set<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(Collectors.toSet());

        Set<Assessment> found = repository.findAll();
        assertEquals(2, found.size());
        assertEquals(saved, found);
    }

    @Test
    public void findOneByParticipantId() throws Exception {
        ProcessRole participant1 = processRoleRepository.save(newProcessRole()
                .with(id(null))
                .build());

        ProcessRole participant2 = processRoleRepository.save(newProcessRole()
                .with(id(null))
                .build());

        Application application = applicationRepository.findOne(1L);

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        List<Assessment> assessments = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(participant1, participant2)
                .withActivityState(openState)
                .build(2);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(toList());

        Assessment found = repository.findOneByParticipantId(participant1.getId());
        assertEquals(saved.get(0), found);
    }

    @Test
    @Rollback
    public void findFirstByParticipantUserIdAndTargetIdOrderByIdDesc() throws Exception {
        Long applicationId = 1L;

        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        Application application = applicationRepository.findOne(applicationId);
        int numOfAssessmentsForEachState = 2;

        List<Assessment> assessments = setUpAssessments(user, application, numOfAssessmentsForEachState);
        assessments.sort(Comparator.comparing(Process::getId));
        Assessment latest = assessments.stream().reduce((first, second) -> second).get();

        Optional<Assessment> found = repository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(user.getId(), applicationId);
        assertTrue(found.isPresent());
        assertEquals(latest.getId(), found.get().getId());
    }

    @Test
    @Rollback
    public void countByParticipantUserIdAndActivityStateStateNotIn() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        int numOfAssessmentsForEachState = 2;

        Application application = applicationRepository.findOne(1L);
        List<Assessment> assessments = setUpAssessments(user, application, numOfAssessmentsForEachState);

        Set<State> statesNotToCount = AssessmentStates.getBackingStates(of(CREATED, PENDING));

        assertEquals(assessments.size() - statesNotToCount.size() * numOfAssessmentsForEachState, repository
                .countByParticipantUserIdAndActivityStateStateNotIn(user.getId(), statesNotToCount));
    }

    @Test
    @Rollback
    public void countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        int numOfAssessmentsForEachState = 2;

        Application application = applicationRepository.findOne(1L);
        setUpAssessments(user, application, numOfAssessmentsForEachState);

        Set<State> statesToCount = AssessmentStates.getBackingStates(of(CREATED, PENDING));

        assertEquals(statesToCount.size() * numOfAssessmentsForEachState, repository
                .countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(user.getId(), application.getCompetition().getId(), statesToCount));
    }

    @Test
    @Rollback
    public void findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        Application application = applicationRepository.findOne(1L);
        int numOfAssessmentsForEachState = 2;

        setUpShuffledAssessments(user, application, numOfAssessmentsForEachState);

        List<Assessment> found = repository
                .findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(
                        user.getId(),
                        application.getCompetition().getId());

        assertEquals(getAssessmentStatesWithoutDecisions().size() * numOfAssessmentsForEachState, found.size());

        Map<AssessmentStates, List<Assessment>> foundByStateMap = found.stream()
                .collect(Collectors.groupingBy(Assessment::getActivityState, LinkedHashMap::new, toList()));

        assertEquals("Expected the assessments to ordered by ActivityState in the natural " +
                "ordering of their equivalent AssessmentStates", getAssessmentStatesWithoutDecisions(), foundByStateMap.keySet());

        foundByStateMap.values().forEach(foundByState -> {
            List<Long> ids = getAssessmentIds(foundByState);
            assertEquals("Expected the assessments to be ordered by id after ordering by ActivityState",
                    ids.stream().sorted().collect(toList()), ids);
        });
    }

    @Test
    public void findByActivityStateStateAndTargetCompetitionId() throws Exception {
        State state = State.CREATED;
        Application application = applicationRepository.findOne(1L);

        List<Assessment> found = repository
                .findByActivityStateStateAndTargetCompetitionId(state, application.getCompetition().getId());

        assertEquals(1, found.size());
        assertEquals(state, found.get(0).getActivityState().getBackingState());
        assertEquals(application.getCompetition().getId(), found.get(0).getTarget().getCompetition().getId());
    }

    @Test
    public void countByActivityStateStateAndTargetCompetitionId() throws Exception {
        State state = State.CREATED;
        Application application = applicationRepository.findOne(1L);

        long found = repository
                .countByActivityStateStateAndTargetCompetitionId(state, application.getCompetition().getId());

        assertEquals(1L, found);
    }

    @Test
    public void countByActiviteStateStateInAndTargetCommpetitionId() throws Exception {
        Set<State> states = EnumSet.of(State.CREATED, State.OPEN );

        Application application = applicationRepository.findOne(1L);

        long found = repository
                .countByActivityStateStateInAndTargetCompetitionId(states, application.getCompetition().getId());

        assertEquals(3L, found);
    }

    @Test
    public void isFeedbackComplete() throws Exception {
        Application application = applicationRepository.findOne(1L);
        Competition competition = application.getCompetition();

        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, OPEN.getBackingState());

        // Feedback should be incomplete for a new assessment with no responses
        Assessment assessment = repository.save(newAssessment()
                .with(id(null))
                .withActivityState(openState)
                .withApplication(application)
                .build());

        assertFalse(repository.isFeedbackComplete(assessment.getId()));

        // Create form input responses for each of the assessment form inputs. Feedback should now be complete
        assessorFormInputResponseRepository.save(competition.getQuestions().stream()
                .flatMap(question -> question.getFormInputs().stream()
                        .filter(formInput -> ASSESSMENT == formInput.getScope())
                        .map(formInput -> newAssessorFormInputResponse()
                                .withAssessment(assessment)
                                .withFormInput(formInput)
                                .withValue("Value")
                                .withUpdatedDate(ZonedDateTime.now())
                                .build())).collect(toList()));
        assertTrue(repository.isFeedbackComplete(assessment.getId()));

        // Delete a response. The feedback should be incomplete again
        Optional<AssessorFormInputResponse> anyFormInputResponse = assessorFormInputResponseRepository
                .findByAssessmentId(assessment.getId()).stream().findAny();
        assertTrue("Expecting there to be at least one assessment form input within the competition questions", anyFormInputResponse.isPresent());
        assessorFormInputResponseRepository.delete(anyFormInputResponse.get());
        assertFalse(repository.isFeedbackComplete(assessment.getId()));
    }

    @Test
    public void getTotalScore() {
        Application application = applicationRepository.findOne(1L);
        Competition competition = application.getCompetition();

        ActivityState submittedState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState());

        int expectedTotalScorePossible = competition.getQuestions().stream()
                .filter(question -> question.getFormInputs().stream().anyMatch(formInput -> formInput.getActive() && ASSESSOR_SCORE == formInput.getType()))
                .mapToInt(question -> ofNullable(question.getAssessorMaximumScore()).orElse(0))
                .sum();

        Assessment assessment = repository.save(newAssessment()
                .with(id(null))
                .withActivityState(submittedState)
                .withApplication(application)
                .build());

        AssessmentTotalScoreResource assessmentTotalScoreBefore = repository.getTotalScore(assessment.getId());
        assertEquals(0, assessmentTotalScoreBefore.getTotalScoreGiven());
        assertEquals(expectedTotalScorePossible, assessmentTotalScoreBefore.getTotalScorePossible());

        // Create form input responses for each of the score form inputs, tracking the total score given
        LongAccumulator scoreGivenAccumulator = new LongAccumulator((x, y) -> x + y, 0);
        assessorFormInputResponseRepository.save(
                competition.getQuestions().stream().flatMap(question ->
                        question.getFormInputs().stream().filter(formInput ->
                                formInput.getActive() && ASSESSOR_SCORE == formInput.getType()
                        ).map(formInput -> {
                                    int randomScore = new Random().nextInt(ofNullable(question.getAssessorMaximumScore()).orElse(0));
                                    scoreGivenAccumulator.accumulate(randomScore);
                                    return newAssessorFormInputResponse()
                                            .withAssessment(assessment)
                                            .withFormInput(formInput)
                                            .withValue(String.valueOf(randomScore))
                                            .withUpdatedDate(ZonedDateTime.now())
                                            .build();
                                }
                        )
                ).collect(toList())
        );

        AssessmentTotalScoreResource assessmentTotalScoreAfter = repository.getTotalScore(assessment.getId());
        assertEquals(scoreGivenAccumulator.intValue(), assessmentTotalScoreAfter.getTotalScoreGiven());
        assertEquals(expectedTotalScorePossible, assessmentTotalScoreAfter.getTotalScorePossible());
    }

    private List<Assessment> setUpAssessments(User user, Application application, int numOfAssessmentsForEachState) {
        List<Assessment> result = new ArrayList<>();
        repository.save(buildAssessments(user, application, numOfAssessmentsForEachState)).forEach(result::add);
        return result;
    }

    private List<Assessment> setUpShuffledAssessments(User user, Application application, int numOfAssessmentsForEachState) {
        List<Assessment> result = new ArrayList<>();
        List<Assessment> assessments = buildAssessments(user, application, numOfAssessmentsForEachState);
        Collections.shuffle(assessments);
        repository.save(assessments).forEach(result::add);
        return result;
    }

    private List<Assessment> buildAssessments(User user, Application application, int numOfAssessmentsForEachState) {
        List<ActivityState> states = getActivityStates();
        return states.stream().flatMap(activityState -> newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(setUpParticipants(user, application, numOfAssessmentsForEachState))
                .withActivityState(activityState)
                .build(numOfAssessmentsForEachState).stream()).collect(toList());
    }

    private ProcessRole[] setUpParticipants(User user, Application application, int count) {
        List<ProcessRole> result = new ArrayList<>();
        processRoleRepository.save(newProcessRole()
                .with(id(null))
                .withUser(user)
                .withRole(roleRepository.findOneByName(ASSESSOR.getName()))
                .withApplication(application)
                .build(count)).forEach(result::add);
        return result.toArray(new ProcessRole[result.size()]);
    }

    private List<Long> getAssessmentIds(List<Assessment> assessments) {
        return assessments.stream().map(Process::getId).collect(toList());
    }

    private List<ActivityState> getActivityStates() {
        return getAssessmentStatesWithoutDecisions().stream().map(assessmentState ->
                activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, assessmentState.getBackingState())).collect(toList());
    }

    private EnumSet<AssessmentStates> getAssessmentStatesWithoutDecisions() {
        return complementOf(EnumSet.of(AssessmentStates.DECIDE_IF_READY_TO_SUBMIT));
    }
}
