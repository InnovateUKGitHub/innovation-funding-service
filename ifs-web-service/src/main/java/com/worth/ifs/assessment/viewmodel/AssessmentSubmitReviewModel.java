package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.*;

/**
 * A view model backing the Assessment Submit Review page.
 *
 * Created by dwatson on 07/10/15.
 */
public class AssessmentSubmitReviewModel {
    private final Log log = LogFactory.getLog(getClass());

    private final Assessment assessment;
    private final ApplicationResource application;
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
        return Pair::getLeft;
    }

    private static <R, T> Function<Map.Entry<R, T>, T> mapEntryValue() {
        return Map.Entry::getValue;
    }

    private static <R, T> Collector<Pair<R, T>, ?, Map<R, T>> pairsToMap() {
        return toMap(Pair::getLeft, Pair::getRight);
    }

    private static ToIntFunction<String> stringToInteger = score -> StringUtils.isNumeric(score) ? Integer.parseInt(score) : 0;

    public AssessmentSubmitReviewModel(Assessment assessment, List<Response> responses, ProcessRole assessorProcessRole, ApplicationResource application, Competition competition) {
        this.assessment = assessment;
        this.application = application;
        this.competition = competition;

        questions = competition.getSections().stream().
                flatMap(section -> section.getQuestions().stream()).
                collect(toList());

        scorableQuestions = questions.stream().filter(Question::getNeedingAssessorScore).collect(toList());

        List<Pair<Question, Optional<Response>>> questionsAndResponsePairs = questions.stream().
                map(question -> Pair.of(question, responses.stream().
                        filter(response -> response.getQuestion().getId().equals(question.getId())).
                        findFirst())).
                collect(toList());

        Map<Question, Optional<Response>> questionsAndResponses =
                questionsAndResponsePairs.stream().collect(pairsToMap());

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

        if (possibleScore == 0) {
            scorePercentage = 0;
        } else {
            scorePercentage = (totalScore * 100) / possibleScore;
        }

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

    public ApplicationResource getApplication() {
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

    public String getRecommendedValue(){
        return assessment.getLastOutcome() != null ? assessment.getLastOutcome().getOutcome() : RecommendedValue.EMPTY.toString();
    }

    public String getSuitableFeedback(){
        return assessment.getLastOutcome() != null ? assessment.getLastOutcome().getDescription() : "";
    }

    public String getComments(){
        return assessment.getLastOutcome() != null ? assessment.getLastOutcome().getComment() : "";
    }
}
