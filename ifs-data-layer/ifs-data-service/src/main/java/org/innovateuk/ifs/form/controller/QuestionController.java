package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * QuestionController exposes question data and operations through a REST API.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/id/{id}")
    public RestResult<QuestionResource> getQuestionById(@PathVariable("id") final Long id) {
        return questionService.getQuestionById(id).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetition/{competitionId}", "/find-by-competition/{competitionId}"})
    public RestResult<List<QuestionResource>> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionService.findByCompetition(competitionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getNextQuestion/{questionId}", "/get-next-question/{questionId}"})
    public RestResult<QuestionResource> getNextQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getNextQuestion(questionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getPreviousQuestionBySection/{sectionId}", "/get-previous-question-by-section/{sectionId}"})
    public RestResult<QuestionResource> getPreviousQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getPreviousQuestionBySection(sectionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping("/getNextQuestionBySection/{sectionId}")
    public RestResult<QuestionResource> getNextQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getNextQuestionBySection(sectionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getPreviousQuestion/{questionId}", "/get-previous-question/{questionId}"})
    public RestResult<QuestionResource> getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getPreviousQuestion(questionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getQuestionByCompetitionIdAndFormInputType/{competitionId}/{formInputType}", "/get-question-by-competition-id-and-form-input-type/{competitionId}/{formInputType}"})
    public RestResult<QuestionResource> getQuestionByFormInputType(@PathVariable("competitionId") final Long competitionId,
                                                                   @PathVariable("formInputType") final FormInputType formInputType) {
        return questionService.getQuestionResourceByCompetitionIdAndFormInputType(competitionId, formInputType).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getQuestionsBySectionIdAndType/{sectionId}/{type}", "/get-questions-by-section-id-and-type/{sectionId}/{type}"})
    public RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(@PathVariable("sectionId") final Long sectionId, @PathVariable("type") QuestionType type) {
        return questionService.getQuestionsBySectionIdAndType(sectionId, type).toGetResponse();
    }

    @PutMapping("/")
    public RestResult<QuestionResource> save(@RequestBody final QuestionResource questionResource) {
        return questionService.save(questionResource).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getQuestionByIdAndAssessmentId/{questionId}/{assessmentId}", "/get-question-by-id-and-assessmentId/{questionId}/{assessmentId}"})
    public RestResult<QuestionResource> getQuestionByIdAndAssessmentId(@PathVariable("questionId") Long questionId, @PathVariable("assessmentId") Long assessmentId) {
        return questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getQuestionsByAssessment/{assessmentId}", "/get-questions-by-assessment/{assessmentId}"})
    public RestResult<List<QuestionResource>> getQuestionsByAssessmentId(@PathVariable("assessmentId") final Long assessmentId) {
        return questionService.getQuestionsByAssessmentId(assessmentId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getQuestionByCompetitionIdAndQuestionSetupType/{competitionId}/{type}", "/get-question-by-competition-id-and-question-setup-type/{competitionId}/{type}"})
    public RestResult<QuestionResource> getQuestionByCompetitionIdQuestionSetupType(
            @PathVariable("competitionId") final long competitionId,
            @PathVariable("type") final QuestionSetupType questionSetupType) {
        return questionService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                questionSetupType).toGetResponse();
    }
}
