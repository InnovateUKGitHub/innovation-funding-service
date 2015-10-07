package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.*;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import static com.worth.ifs.util.IfsFunctionUtils.ifPresent;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Created by dwatson on 07/10/15.
 */
public class AssessmentSubmitReviewModel {

    private final Assessment assessment;
    private final Application application;
    private final Competition competition;
    private final List<Question> questions;
    private final List<Question> scorableQuestions;
    private final int totalScore;
    private final int possibleScore;
    private final Map<Long, AssessorFeedback> responseIdsAndFeedback;
    private final Map<Long, Optional<Response>> questionIdsAndResponses;
    private final int scorePercentage;
    private final List<AssessmentSummarySection> assessmentSummarySections;

    private static <R, T> Predicate<Pair<R, Optional<T>>> rightPairIsPresent() {
        return pair -> pair.getRight().isPresent();
    }

    private static <R, T> Function<Pair<R, Optional<T>>, T> presentRightPair() {
        return pair -> pair.getRight().get();
    }

    private static <R, T> Function<Pair<R, T>, R> leftPair() {
        return pair -> pair.getLeft();
    }

    private static <R, T> Function<Map.Entry<R, T>, T> mapEntryValue() {
        return e -> e.getValue();
    }

    private static <R, T> Collector<Pair<R, T>, ?, Map<R, T>> pairsToMap() {
        return toMap(Pair::getLeft, Pair::getRight);
    }

    private static ToIntFunction<String> stringToInteger = score -> StringUtils.isNumeric(score) ? Integer.parseInt(score) : 0;

    public AssessmentSubmitReviewModel(Assessment assessment, List<Response> responses, ProcessRole assessorProcessRole) {

        this.assessment = assessment;
        this.application = assessment.getApplication();
        this.competition = assessment.getApplication().getCompetition();

        questions = assessment.getApplication().getCompetition().getSections().stream().
                flatMap(section -> section.getQuestions().stream()).
                collect(toList());

        scorableQuestions = questions.stream().filter(Question::getNeedingAssessorScore).collect(toList());

        Map<Question, Optional<Response>> questionsAndResponses = questions.stream().
                map(question -> Pair.of(question, responses.stream().
                        filter(response -> response.getQuestion().getId().equals(question.getId())).
                        findFirst())).
                collect(pairsToMap());

        questionIdsAndResponses = questionsAndResponses.entrySet().stream().
                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));

        Map<Response, AssessorFeedback> responsesAndFeedback = responses.stream().
                map(response -> Pair.of(response, response.getResponseAssessmentForAssessor(assessorProcessRole))).
                filter(rightPairIsPresent()).
                collect(toMap(leftPair(), presentRightPair()));

        responseIdsAndFeedback = responsesAndFeedback.entrySet().stream().
                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));

        totalScore = questionsAndResponses.entrySet().stream().
                filter(e -> e.getKey().getNeedingAssessorScore()).
                map(mapEntryValue()).
                map(response -> response.map(r -> Optional.ofNullable(responseIdsAndFeedback.get(r.getId()))).orElse(empty())).
                map(optionalFeedback -> optionalFeedback.map(AssessorFeedback::getAssessmentValue).orElse("0")).
                collect(summingInt(stringToInteger));

        possibleScore = questions.stream().
                filter(Question::getNeedingAssessorScore).
                collect(summingInt(q -> 10));

        scorePercentage = (totalScore * 100) / possibleScore;

        List<Section> sections = competition.getSections();

        Map<Question, Optional<AssessorFeedback>> questionsAndFeedback = questionsAndResponses.entrySet().stream().
                map(e -> Pair.of(e.getKey(), e.getValue().map(feedback -> Optional.ofNullable(responseIdsAndFeedback.get(feedback.getId()))).orElse(empty()))).
                        collect(pairsToMap());

        assessmentSummarySections = sections.stream().
                filter(Section::isDisplayInAssessmentApplicationSummary).
                map(section -> new AssessmentSummarySection(section, questionsAndFeedback)).
                collect(toList());
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public Application getApplication() {
        return application;
    }

    public Competition getCompetition() {
        return competition;
    }

    public List<Question> getScorableQuestions() {
        return scorableQuestions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getPossibleScore() {
        return possibleScore;
    }

    public int getScorePercentage() {
        return scorePercentage;
    }

    public List<AssessmentSummarySection> getAssessmentSummarySections() {
        return assessmentSummarySections;
    }

    public AssessorFeedback getFeedbackForQuestion(Question question) {
        Optional<Response> responseOption = questionIdsAndResponses.get(question.getId());
        return responseOption.map(response -> responseIdsAndFeedback.get(response.getId())).orElse(null);
    }
}
