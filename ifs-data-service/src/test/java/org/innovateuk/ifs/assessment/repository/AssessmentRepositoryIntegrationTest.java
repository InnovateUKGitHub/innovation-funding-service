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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.SUBMITTED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.util.EnumSet.complementOf;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
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

    @Autowired
    @Override
    protected void setRepository(final AssessmentRepository repository) {
        this.repository = repository;
    }

    @Test
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
                .withApplication(application)
                .withParticipant(participant1, participant2)
                .withActivityState(openState)
                .build(2);

        List<Assessment> saved = assessments.stream().map(assessment -> repository.save(assessment)).collect(toList());

        Assessment found = repository.findOneByParticipantId(participant1.getId());
        assertEquals(saved.get(0), found);
    }

    @Test
    public void findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc() throws Exception {
        assessorFormInputResponseRepository.deleteAll();
        repository.deleteAll();

        Long userId = 3L;

        Application application = applicationRepository.findOne(1L);
        int numOfAssessmentsForEachState = 2;

        setUpShuffledAssessments(userRepository.findOne(userId), application, numOfAssessmentsForEachState);

        List<Assessment> found = repository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, application.getCompetition().getId());

        assertEquals(getAssessmentStatesWithoutDecisions().size() * numOfAssessmentsForEachState, found.size());

        Map<AssessmentStates, List<Assessment>> foundByStateMap = found.stream().collect(Collectors.groupingBy(Assessment::getActivityState, LinkedHashMap::new, toList()));

        assertEquals("Expected the assessments to ordered by ActivityState in the natural ordering of their equivalent AssessmentStates", getAssessmentStatesWithoutDecisions(), foundByStateMap.keySet());

        foundByStateMap.values().forEach(foundByState -> {
            List<Long> ids = getAssessmentIds(foundByState);
            assertEquals("Expected the assessments to be ordered by id after ordering by ActivityState", ids.stream().sorted().collect(toList()), ids);
        });
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
        assessorFormInputResponseRepository.save(competition.getQuestions().stream().flatMap(question -> question.getFormInputs().stream().filter(formInput -> ASSESSMENT == formInput.getScope()).map(formInput -> newAssessorFormInputResponse()
                .withAssessment(assessment)
                .withFormInput(formInput)
                .withValue("Value")
                .withUpdatedDate(LocalDateTime.now())
                .build())).collect(toList()));
        assertTrue(repository.isFeedbackComplete(assessment.getId()));

        // Delete a response. The feedback should be incomplete again
        Optional<AssessorFormInputResponse> anyFormInputResponse = assessorFormInputResponseRepository.findByAssessmentId(assessment.getId()).stream().findAny();
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
                                            .withUpdatedDate(LocalDateTime.now())
                                            .build();
                                }
                        )
                ).collect(toList())
        );

        AssessmentTotalScoreResource assessmentTotalScoreAfter = repository.getTotalScore(assessment.getId());
        assertEquals(scoreGivenAccumulator.intValue(), assessmentTotalScoreAfter.getTotalScoreGiven());
        assertEquals(expectedTotalScorePossible, assessmentTotalScoreAfter.getTotalScorePossible());
    }

    private void setUpShuffledAssessments(User user, Application application, int numOfAssessmentsForEachState) {
        List<ActivityState> states = getActivityStates();
        List<Assessment> assessments = states.stream().flatMap(activityState -> newAssessment()
                .with(id(null))
                .withApplication(application)
                .withParticipant(setUpParticipants(user, application, numOfAssessmentsForEachState))
                .withActivityState(activityState)
                .build(numOfAssessmentsForEachState).stream()).collect(toList());

        Collections.shuffle(assessments);
        repository.save(assessments);
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
        return getAssessmentStatesWithoutDecisions().stream().map(assessmentState -> activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, assessmentState.getBackingState())).collect(toList());
    }

    private EnumSet<AssessmentStates> getAssessmentStatesWithoutDecisions() {
        return complementOf(EnumSet.of(AssessmentStates.DECIDE_IF_READY_TO_SUBMIT));
    }
}
