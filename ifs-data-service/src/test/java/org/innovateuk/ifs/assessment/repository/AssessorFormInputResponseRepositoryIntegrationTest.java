package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssessorFormInputResponseRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessorFormInputResponseRepository> {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    @Override
    protected void setRepository(AssessorFormInputResponseRepository repository) {
        this.repository = repository;
    }

    @Test
    public void testFindAll() throws Exception {
        repository.deleteAll();

        ProcessRole processRole = processRoleRepository.findOne(1L);
        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState());

        Assessment assessment = assessmentRepository.save(
                newAssessment().
                        withParticipant(processRole).
                        withApplication(application).
                        withActivityState(openState).
                        build());

        List<Question> questions = asList(new Question(), new Question()).stream().map(question -> questionRepository.save(question)).collect(toList());

        List<FormInput> formInputs = newFormInput()
                .withId(null, null)
                .withQuestion(questions.get(0), questions.get(1))
                .withPriority(0, 0)
                .withType(TEXTAREA, TEXTAREA)
                .withScope(ASSESSMENT, ASSESSMENT)
                .build(2).stream().map(formInput -> formInputRepository.save(formInput)).collect(toList());

        List<AssessorFormInputResponse> saved = newAssessorFormInputResponse()
                .withId(null, null)
                .withAssessment(assessment, assessment)
                .withFormInput(formInputs.get(0), formInputs.get(1))
                .withValue("Sample response 1", "Sample response 2")
                .withUpdatedDate(LocalDateTime.parse("2016-07-12T16:10:50.21").atZone(ZoneId.systemDefault()), LocalDateTime.parse("2016-07-12T16:15:25.42").atZone(ZoneId.systemDefault()))
                .build(2).stream().map(assessorFormInputResponse -> repository.save(assessorFormInputResponse)).collect(toList());

        List<AssessorFormInputResponse> found = repository.findAll();

        assertEquals(2, found.size());
        assertEquals(saved.get(0), found.get(0));
        assertEquals(saved.get(1), found.get(1));
    }

    @Test
    public void testFindByAssessmentId() throws Exception {

        ProcessRole processRole = processRoleRepository.findOne(1L);
        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState());

        List<Assessment> assessments =
                newAssessment().
                        withParticipant(processRole).
                        withApplication(application).
                        withActivityState(openState).
                        build(2);

        List<Assessment> savedAssessments = simpleMap(assessments, assessmentRepository::save);

        // Save two questions
        List<Question> questions = asList(new Question(), new Question()).stream().map(question -> questionRepository.save(question)).collect(toList());

        // Save two form inputs for each of the two questions
        List<FormInput> formInputs = newFormInput()
                .withId(null, null)
                .withQuestion(questions.get(0), questions.get(1), questions.get(0), questions.get(1))
                .withPriority(0, 1, 0, 1)
                .withType(TEXTAREA, TEXTAREA, TEXTAREA, TEXTAREA)
                .withScope(ASSESSMENT, ASSESSMENT, ASSESSMENT, ASSESSMENT)
                .build(4).stream().map(formInput -> formInputRepository.save(formInput)).collect(toList());

        // For each of the assessments, save one response for each of the four form inputs
        List<AssessorFormInputResponse> saved = newAssessorFormInputResponse()
                .withId()
                .withAssessment(savedAssessments.get(0), savedAssessments.get(0), savedAssessments.get(0), savedAssessments.get(0), savedAssessments.get(1), savedAssessments.get(1), savedAssessments.get(1), savedAssessments.get(1))
                .withFormInput(formInputs.get(0), formInputs.get(1), formInputs.get(2), formInputs.get(3), formInputs.get(0), formInputs.get(1), formInputs.get(2), formInputs.get(3))
                .withValue("Sample response 1", "Sample response 2", "Sample response 3", "Sample response 4", "Sample response 5", "Sample response 6", "Sample response 7", "Sample response 8")
                .withUpdatedDate(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())
                .build(8).stream().map(assessorFormInputResponse -> repository.save(assessorFormInputResponse)).collect(toList());

        // There should be four responses found for each of the questions
        List<AssessorFormInputResponse> expectedForAssessment1 = saved.subList(0, 4);
        List<AssessorFormInputResponse> expectedForAssessment2 = saved.subList(4, 8);

        List<AssessorFormInputResponse> foundForAssessment1 = repository.findByAssessmentId(savedAssessments.get(0).getId());
        List<AssessorFormInputResponse> foundForAssessment2 = repository.findByAssessmentId(savedAssessments.get(1).getId());
        assertEquals(expectedForAssessment1, foundForAssessment1);
        assertEquals(expectedForAssessment2, foundForAssessment2);
    }

    @Test
    public void testFindByAssessmentIdAndFormInputQuestionId() throws Exception {

        ProcessRole processRole = processRoleRepository.findOne(1L);
        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState());

        Assessment assessment = assessmentRepository.save(
                newAssessment().
                        withParticipant(processRole).
                        withApplication(application).
                        withActivityState(openState).
                        build());
        // Save two questions
        List<Question> questions = asList(new Question(), new Question()).stream().map(question -> questionRepository.save(question)).collect(toList());

        // Save two form inputs for each of the two questions
        List<FormInput> formInputs = newFormInput()
                .withId(null, null, null, null)
                .withQuestion(questions.get(0), questions.get(0), questions.get(1), questions.get(1))
                .withPriority(0, 1, 0, 1)
                .withType(TEXTAREA, TEXTAREA, TEXTAREA, TEXTAREA)
                .withScope(ASSESSMENT, ASSESSMENT, ASSESSMENT, ASSESSMENT)
                .build(4).stream().map(formInput -> formInputRepository.save(formInput)).collect(toList());

        // For the assessment, save one response for each of the four form inputs
        List<AssessorFormInputResponse> saved = newAssessorFormInputResponse()
                .withId(null, null, null, null)
                .withAssessment(assessment, assessment, assessment, assessment)
                .withFormInput(formInputs.get(0), formInputs.get(1), formInputs.get(2), formInputs.get(3))
                .withValue("Sample response 1", "Sample response 2", "Sample response 3", "Sample response 4")
                .withUpdatedDate(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())
                .build(4).stream().map(assessorFormInputResponse -> repository.save(assessorFormInputResponse)).collect(toList());

        // There should be two responses found for each of the questions
        List<AssessorFormInputResponse> expectedForQuestion1 = saved.subList(0, 2);
        List<AssessorFormInputResponse> expectedForQuestion2 = saved.subList(2, 4);

        List<AssessorFormInputResponse> foundForQuestion1 = repository.findByAssessmentIdAndFormInputQuestionId(assessment.getId(), questions.get(0).getId());
        List<AssessorFormInputResponse> foundForQuestion2 = repository.findByAssessmentIdAndFormInputQuestionId(assessment.getId(), questions.get(1).getId());
        assertEquals(expectedForQuestion1, foundForQuestion1);
        assertEquals(expectedForQuestion2, foundForQuestion2);
    }

    @Test
    public void testFindByAssessmentIdAndFormInputId() throws Exception {

        ProcessRole processRole = processRoleRepository.findOne(1L);
        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState());

        List<Assessment> assessments =
                newAssessment().
                        withParticipant(processRole).
                        withApplication(application).
                        withActivityState(openState).
                        build(2);

        List<Assessment> savedAssessments = simpleMap(assessments, assessmentRepository::save);

        // Save a question
        Question question = questionRepository.save(new Question());

        // Save two form inputs for the question
        List<FormInput> formInputs = newFormInput()
                .withId(null, null)
                .withQuestion(question, question)
                .withPriority(0, 1)
                .withType(TEXTAREA, TEXTAREA)
                .withScope(ASSESSMENT, ASSESSMENT)
                .build(2).stream().map(formInput -> formInputRepository.save(formInput)).collect(toList());

        // For each of the assessments, save one response for each of the two form inputs
        List<AssessorFormInputResponse> saved = newAssessorFormInputResponse()
                .withId(null, null, null, null)
                .withAssessment(savedAssessments.get(0), savedAssessments.get(0), savedAssessments.get(1), savedAssessments.get(1))
                .withFormInput(formInputs.get(0), formInputs.get(1), formInputs.get(0), formInputs.get(1))
                .withValue("Sample response 1", "Sample response 2", "Sample response 3", "Sample response 4")
                .withUpdatedDate(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())
                .build(4).stream().map(assessorFormInputResponse -> repository.save(assessorFormInputResponse)).collect(toList());

        // There should be a response found for each of the form inputs, for each assessment
        AssessorFormInputResponse foundForAssessment1FormInput1 = repository.findByAssessmentIdAndFormInputId(savedAssessments.get(0).getId(), formInputs.get(0).getId());
        AssessorFormInputResponse foundForAssessment1FormInput2 = repository.findByAssessmentIdAndFormInputId(savedAssessments.get(0).getId(), formInputs.get(1).getId());
        AssessorFormInputResponse foundForAssessment2FormInput1 = repository.findByAssessmentIdAndFormInputId(savedAssessments.get(1).getId(), formInputs.get(0).getId());
        AssessorFormInputResponse foundForAssessment2FormInput2 = repository.findByAssessmentIdAndFormInputId(savedAssessments.get(1).getId(), formInputs.get(1).getId());
        assertEquals(saved.get(0), foundForAssessment1FormInput1);
        assertEquals(saved.get(1), foundForAssessment1FormInput2);
        assertEquals(saved.get(2), foundForAssessment2FormInput1);
        assertEquals(saved.get(3), foundForAssessment2FormInput2);
    }

    @Test
    public void findByAssessmentTargetId() {

        ProcessRole processRole = processRoleRepository.findOne(1L);
        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState());

        List<Assessment> assessments =
                newAssessment().
                        withParticipant(processRole).
                        withApplication(application).
                        withActivityState(openState).
                        build(2);

        List<Assessment> savedAssessments = simpleMap(assessments, assessmentRepository::save);

        // Save a question
        Question question = questionRepository.save(new Question());

        // Save two form inputs for the question
        List<FormInput> formInputs = newFormInput()
                .withId(null, null)
                .withQuestion(question, question)
                .withPriority(0, 1)
                .withType(TEXTAREA, TEXTAREA)
                .withScope(ASSESSMENT, ASSESSMENT)
                .build(2).stream().map(formInput -> formInputRepository.save(formInput)).collect(toList());

        // For each of the assessments, save one response for each of the two form inputs
        List<AssessorFormInputResponse> saved = newAssessorFormInputResponse()
                .withId(null, null, null, null)
                .withAssessment(savedAssessments.get(0), savedAssessments.get(0), savedAssessments.get(1), savedAssessments.get(1))
                .withFormInput(formInputs.get(0), formInputs.get(1), formInputs.get(0), formInputs.get(1))
                .withValue("Sample response 1", "Sample response 2", "Sample response 3", "Sample response 4")
                .withUpdatedDate(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())
                .build(4).stream().map(assessorFormInputResponse -> repository.save(assessorFormInputResponse)).collect(toList());

        // There should be a response found for each of the form inputs, for each assessment
        List<AssessorFormInputResponse> responses = repository.findByAssessmentTargetId(application.getId());

        assertEquals(saved, responses);
    }

    @Test
    public void findByAssessmentTargetIdAndFormInputQuestionId() {

        ProcessRole processRole = processRoleRepository.findOne(1L);
        Application application = applicationRepository.findOne(1L);
        ActivityState openState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState());

        List<Assessment> assessments =
                newAssessment().
                        withParticipant(processRole).
                        withApplication(application).
                        withActivityState(openState).
                        build(2);

        List<Assessment> savedAssessments = simpleMap(assessments, assessmentRepository::save);

        // Save a question
        Question question = questionRepository.save(new Question());
        Question otherQuestion = questionRepository.save(new Question());

        // Save two form inputs for the question
        List<FormInput> formInputs = newFormInput()
                .withId(null, null, null)
                .withQuestion(question, question, otherQuestion)
                .withPriority(0, 1,2)
                .withType(TEXTAREA, TEXTAREA, TEXTAREA)
                .withScope(ASSESSMENT, ASSESSMENT, ASSESSMENT)
                .build(3).stream().map(formInput -> formInputRepository.save(formInput)).collect(toList());

        // For each of the assessments, save one response for each of the two form inputs
        List<AssessorFormInputResponse> saved = newAssessorFormInputResponse()
                .withId(null, null, null, null, null, null)
                .withAssessment(savedAssessments.get(0), savedAssessments.get(0),savedAssessments.get(0), savedAssessments.get(1), savedAssessments.get(1), savedAssessments.get(1))
                .withFormInput(formInputs.get(0), formInputs.get(1), formInputs.get(2), formInputs.get(0), formInputs.get(1), formInputs.get(2))
                .withValue("Sample response 1", "Sample response 2", "Sample response 3", "Sample response 4", "Sample response 5", "Sample response 6")
                .withUpdatedDate(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())
                .build(6).stream().map(assessorFormInputResponse -> repository.save(assessorFormInputResponse)).collect(toList());

        List<AssessorFormInputResponse> expected = saved.stream().filter(x -> !x.getFormInput().equals(formInputs.get(2))).collect(toList());

        // There should be a response found for each of the form inputs, for each assessment
        List<AssessorFormInputResponse> responses = repository.findByAssessmentTargetIdAndFormInputQuestionId(application.getId(), question.getId());
        assertEquals(expected.size(), responses.size());
        assertTrue(expected.containsAll(responses));
    }
}
