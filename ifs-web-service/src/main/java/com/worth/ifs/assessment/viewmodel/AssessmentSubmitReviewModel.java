package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.competition.domain.Competition;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.assessment.domain.AssessmentOutcomes.RECOMMEND;
import static com.worth.ifs.util.CollectionFunctions.mapEntryValue;
import static com.worth.ifs.util.CollectionFunctions.pairsToMap;
import static com.worth.ifs.util.PairFunctions.*;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    private final Score score;
    private final Map<Long, AssessorFeedback> responseIdsAndFeedback;
    private final Map<Long, Optional<Response>> questionIdsAndResponses;
    private final List<AssessmentSummarySection> assessmentSummarySections;


    public AssessmentSubmitReviewModel(Assessment assessment, List<Response> responses, ApplicationResource application, Competition competition, Score score) {
        this.assessment = assessment;
        this.application = application;
        this.competition = competition;
        this.score = score;

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
                map(response -> Pair.of(response, response.getResponseAssessmentForAssessor(assessment.getProcessRole()))).
                filter(rightPairIsPresent()).
                collect(toMap(leftPair(), presentRightPair()));

        responseIdsAndFeedback = responsesAndFeedback.entrySet().stream().
                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));

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
        return score.getTotal();
    }

    public int getPossibleScore() {
        return score.getPossible();
    }

    public int getScorePercentage() {
        return score.getPercentage();
    }

    public List<AssessmentSummarySection> getAssessmentSummarySections() {
        return assessmentSummarySections;
    }

    public AssessorFeedback getFeedbackForQuestion(Question question) {
        Optional<Response> responseOption = questionIdsAndResponses.get(question.getId());
        return responseOption.map(response -> responseIdsAndFeedback.get(response.getId())).orElse(null);
    }

    public String getRecommendedValue(){
        return assessment.getLastOutcome(RECOMMEND) != null ? assessment.getLastOutcome(RECOMMEND).getOutcome() : RecommendedValue.EMPTY.toString();
    }

    public String getSuitableFeedback(){
        return assessment.getLastOutcome(RECOMMEND) != null ? assessment.getLastOutcome(RECOMMEND).getDescription() : "";
    }

    public String getComments(){
        return assessment.getLastOutcome(RECOMMEND) != null ? assessment.getLastOutcome(RECOMMEND).getComment() : "";
    }
}
