package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.*;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.assessment.domain.AssessmentOutcomes.RECOMMEND;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.PairFunctions.leftPair;
import static com.worth.ifs.util.PairFunctions.presentRightPair;
import static com.worth.ifs.util.PairFunctions.rightPairIsPresent;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * A view model backing the Assessment Submit Review page.
 *
 * Created by dwatson on 07/10/15.
 */
public class AssessmentSubmitReviewModel {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(AssessmentSubmitReviewModel.class);

    private final Assessment assessment;
    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final List<QuestionResource> questions;
    private final List<QuestionResource> scorableQuestions;
    private final Score score;
    private final Map<Long, AssessorFeedbackResource> responseIdsAndFeedback;
    private final Map<Long, Optional<ResponseResource>> questionIdsAndResponses;
    private final List<AssessmentSummarySection> assessmentSummarySections;


    // TODO this logic should live in the data layer and we should return a dto instead.
    // TODO Note there is code commonality with AssessmentHandler.getScore.
    // TODO make these changes when converting to dtos.
    public AssessmentSubmitReviewModel(Assessment assessment, List<ResponseResource> responses, ApplicationResource application, CompetitionResource competition, Score score, List<QuestionResource> questions, List<SectionResource> sections) {
        this.assessment = assessment;
        this.application = application;
        this.competition = competition;
        this.score = score;
        this.questions = questions;

        scorableQuestions = questions.stream().filter(QuestionResource::getNeedingAssessorScore).collect(toList());

        List<Pair<QuestionResource, Optional<ResponseResource>>> questionsAndResponsePairs = questions.stream().
                map(question -> Pair.of(question, responses.stream().
                        filter(response -> response.getQuestion().equals(question.getId())).
                        findFirst())).
                collect(toList());

        Map<QuestionResource, Optional<ResponseResource>> questionsAndResponses =
                questionsAndResponsePairs.stream().collect(pairsToMap());

        questionIdsAndResponses = questionsAndResponses.entrySet().stream().
                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));
        responseIdsAndFeedback = new HashedMap<>();
        assessmentSummarySections = new ArrayList<>();

//        Map<ResponseResource, Long> responsesAndFeedback = responses.stream().
//                map(response -> Pair.of(response, response.getResponseAssessmentFeedbacks())).
//                filter(rightPairIsPresent()).
//                collect(toMap(leftPair(), presentRightPair()));
//
//        responses.forEach(response -> {
//            response.getResponseAssessmentFeedbacks()
//            responseIdsAndFeedback.put(response.getId(), )
//        });
//
//        responseIdsAndFeedback = ;
//                responsesAndFeedback.entrySet().stream().
//                collect(toMap(e -> e.getKey().getId(), mapEntryValue()));
//
//
//        Map<QuestionResource, Optional<AssessorFeedbackResource>> questionsAndFeedback =
//                questionsAndResponses.entrySet().stream().
//                map(e -> Pair.of(e.getKey(), e.getValue().map(feedback -> Optional.ofNullable(responseIdsAndFeedback.get(feedback.getId()))).orElse(empty()))).
//                        collect(pairsToMap());
//
//        Map<Long, List<QuestionResource>> sectionQuestions = simpleToMap(sections,
//                SectionResource::getId,
//                s -> simpleFilter(questions,
//                        q -> s.getQuestions()
//                                .contains(
//                                        q.getId()
//                                )
//                )
//        );
//
//        assessmentSummarySections = sections.stream().
//                filter(SectionResource::isDisplayInAssessmentApplicationSummary).
//                map(section -> new AssessmentSummarySection(section, sectionQuestions.get(section.getId()), questionsAndFeedback)).
//                collect(toList());
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public List<QuestionResource> getScorableQuestions() {
        return scorableQuestions;
    }

    public List<QuestionResource> getQuestions() {
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

    public AssessorFeedbackResource getFeedbackForQuestion(QuestionResource question) {
        Optional<ResponseResource> responseOption = questionIdsAndResponses.get(question.getId());
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
