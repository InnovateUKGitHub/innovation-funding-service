package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.worth.ifs.util.IfsFunctionUtils.ifPresent;
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
    private final Map<Long, Response> questionIdsAndResponses;
    private final int scorePercentage;

    public AssessmentSubmitReviewModel(Assessment assessment, List<Response> responses, ProcessRole assessorProcessRole) {

        this.assessment = assessment;
        this.application = assessment.getApplication();
        this.competition = assessment.getApplication().getCompetition();

        questions = assessment.getApplication().getCompetition().getSections().stream().
                flatMap(section -> section.getQuestions().stream()).
                collect(toList());

        scorableQuestions = questions.stream().filter(Question::getNeedingAssessorScore).collect(toList());

        Map<Question, Response> questionsAndResponses = responses.stream().collect(toMap(Response::getQuestion, Function.identity()));
        questionIdsAndResponses = questionsAndResponses.entrySet().stream().collect(toMap(e -> e.getKey().getId(), e -> e.getValue()));

        Map<Response, AssessorFeedback> responsesAndFeedback = responses.stream().
                map(response -> Pair.of(response, response.getResponseAssessmentForAssessor(assessorProcessRole))).
                filter(pair -> pair.getRight().isPresent()).
                collect(toMap(pair -> pair.getLeft(), pair -> pair.getRight().get()));

        responseIdsAndFeedback = responsesAndFeedback.entrySet().stream().collect(toMap(e -> e.getKey().getId(), e -> e.getValue()));

        totalScore = questionsAndResponses.entrySet().stream().
                filter(e -> e.getKey().getNeedingAssessorScore()).
                map(e -> e.getValue()).
                map(response -> Optional.ofNullable(responseIdsAndFeedback.get(response.getId()))).
                map(optionalFeedback -> ifPresent(optionalFeedback, AssessorFeedback::getAssessmentValue).orElse("0")).
                collect(summingInt(score -> StringUtils.isNumeric(score) ? Integer.parseInt(score) : 0));

        possibleScore = questions.stream().
                filter(Question::getNeedingAssessorScore).
                collect(summingInt(q -> 10));

        scorePercentage = (int) ((totalScore * 100) / possibleScore);
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

    public AssessorFeedback getFeedbackForQuestion(Question question) {
        Optional<Response> responseOption = Optional.ofNullable(questionIdsAndResponses.get(question.getId()));
        return responseOption.map(response -> responseIdsAndFeedback.get(response.getId())).orElse(null);
    }
}
