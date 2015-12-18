package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.controller.ResponseController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toMap;

/**
 * AssessmentHandler is responsible to manage the domain logic around the Assessment's domain range.
 * This avoids code coupling and spread knowledge and responsibility over Assessment's and allows us
 * to have the access to them centered here, preventing any incoherence by multiple ways.
 *
 * So this class is responsible to manage interactions with the AssessmentRepository, being a facade between
 * the outside world and the Assessment's world.
 */
@Component
public class AssessmentHandler {

    @Autowired
    private AssessmentRepository assessmentRepository;

    // TODO qqRP change to a service.
    @Autowired
    private ResponseController responseController;

    public AssessmentHandler(){}

    public void save(Assessment a) {
        assessmentRepository.save(a);
    }

    public Assessment saveAndGet(Assessment a) {
        return assessmentRepository.save(a);
    }

    public Assessment getOne(Long id) {
        return assessmentRepository.findById(id);
    }

    /**
     * Get's all the assessments by competition and assessor.
     * By 'All' is meant all the assessments whose invitation was not rejected.
     * Also, groups the assessments by first having the pending ones and only after the open/active/submitted.
     */
    public List<Assessment> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId) {
        Set<String> states = AssessmentStates.getStates();
        states.remove(AssessmentStates.REJECTED.getState());
        return assessmentRepository.findByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusIn(assessorId, competitionId, states);
    }

    public Assessment getOneByProcessRole(Long processRoleId) {
        return assessmentRepository.findOneByProcessRoleId(processRoleId);
    }

    public Integer getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long assessorId) {
        return assessmentRepository.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatus(assessorId, competitionId, ApplicationStatusConstants.SUBMITTED.getName());
    }
    public Integer getTotalAssignedAssessmentsByCompetition(Long competitionId, Long assessorId) {
        // By 'assigned' is meant an assessment process not rejected
        return assessmentRepository.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusNot(assessorId, competitionId, ApplicationStatusConstants.REJECTED.getName());
    }

    public RecommendedValue getRecommendedValueFromString(String value) {
        switch (value) {
            case "yes":
                return RecommendedValue.YES;
            case "no":
                return RecommendedValue.NO;
            default:
                return RecommendedValue.EMPTY;
        }
    }


    // TODO qqRP move to common functional code
    private static <R, T> Collector<Pair<R, T>, ?, Map<R, T>> pairsToMap() {
        return toMap(Pair::getLeft, Pair::getRight);
    }


    private static <R, T> Predicate<Pair<R, Optional<T>>> rightPairIsPresent() {
        return pair -> pair.getRight().isPresent();
    }

    private static <R, T> Function<Pair<R, Optional<T>>, T> presentRightPair() {
        return pair -> pair.getRight().get();
    }

    private static <R, T> Function<Pair<R, T>, R> leftPair() {
        return Pair::getLeft;
    }

    private static <R, T> Function<Map.Entry<R, T>, T> mapEntryValue() {
        return Map.Entry::getValue;
    }

    private static ToIntFunction<String> stringToInteger = score -> StringUtils.isNumeric(score) ? Integer.parseInt(score) : 0;

    public int getScore(Long id) {
        Assessment assessment = assessmentRepository.findById(id);
        Application application = assessment.getProcessRole().getApplication();
        List<Response> responses = responseController.findResponsesByApplication(application.getId());
        Competition competition = application.getCompetition();
        ProcessRole assessorProcessRole = assessment.getProcessRole(); // TODO correct?

        List<Question> questions = competition.getSections().stream().
                flatMap(section -> section.getQuestions().stream()).
                collect(Collectors.toList());

        List<Pair<Question, Optional<Response>>> questionsAndResponsePairs = questions.stream().
                map(question -> Pair.of(question, responses.stream().
                        filter(response -> response.getQuestion().getId().equals(question.getId())).
                        findFirst())).
                collect(Collectors.toList());

        Map<Question, Optional<Response>> questionsAndResponses =
                questionsAndResponsePairs.stream().collect(pairsToMap());

        Map<Long, Optional<Response>> questionIdsAndResponses = questionsAndResponses.entrySet().stream().
                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));

        Map<Response, AssessorFeedback> responsesAndFeedback = responses.stream().
                map(response -> Pair.of(response, response.getResponseAssessmentForAssessor(assessorProcessRole))).
                filter(rightPairIsPresent()).
                collect(toMap(leftPair(), presentRightPair()));

        Map<Long, AssessorFeedback> responseIdsAndFeedback = responsesAndFeedback.entrySet().stream().
                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));

        int totalScore = questionsAndResponses.entrySet().stream().
                filter(e -> e.getKey().getNeedingAssessorScore()).
                map(mapEntryValue()).
                map(response -> response.map(r -> Optional.ofNullable(responseIdsAndFeedback.get(r.getId()))).orElse(empty())).
                map(optionalFeedback -> optionalFeedback.map(AssessorFeedback::getAssessmentValue).orElse("0")).
                collect(summingInt(stringToInteger));

        int possibleScore = questions.stream().
                filter(Question::getNeedingAssessorScore).
                collect(summingInt(q -> 10));

        return possibleScore == 0 ? 0 : (totalScore * 100) / possibleScore;
    }
}
