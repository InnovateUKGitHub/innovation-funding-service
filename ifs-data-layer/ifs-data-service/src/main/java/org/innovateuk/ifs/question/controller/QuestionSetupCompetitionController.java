package org.innovateuk.ifs.question.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.question.transactional.QuestionFileSetupCompetitionService;
import org.innovateuk.ifs.question.transactional.QuestionSetupCompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * QuestionController exposes competition setup application questions data and operations through a REST API.
 */
@RestController
@RequestMapping("/question-setup")
public class QuestionSetupCompetitionController {

    @Autowired
    private QuestionSetupCompetitionService questionSetupCompetitionService;
    @Autowired
    private QuestionFileSetupCompetitionService questionFileSetupCompetitionService;

    @GetMapping("/get-by-id/{questionId}")
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(@PathVariable final long questionId) {
        return questionSetupCompetitionService.getByQuestionId(questionId).toGetResponse();
    }

    @PutMapping("/save")
    public RestResult<Void> save(@RequestBody final CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return questionSetupCompetitionService.update(competitionSetupQuestionResource).toPutResponse();
    }

    @PostMapping("/add-default-to-competition/{competitionId}")
    public RestResult<CompetitionSetupQuestionResource> addDefaultToCompetitionId(
            @PathVariable long competitionId) {
        return questionSetupCompetitionService.createByCompetitionId(competitionId).toPostCreateResponse();
    }

    @PostMapping("/add-research-category-question-to-competition/{competitionId}")
    public RestResult<Void> addResearchCategoryQuestionToCompetition(@PathVariable long competitionId) {
        return questionSetupCompetitionService.addResearchCategoryQuestionToCompetition(competitionId)
                .toPostCreateResponse();
    }

    @DeleteMapping("/delete-by-id/{questionId}")
    public RestResult<Void> deleteById(@PathVariable long questionId) {
        return questionSetupCompetitionService.delete(questionId).toDeleteResponse();
    }

    @PostMapping(value = "/template-file/{questionId}", produces = "application/json")
    public RestResult<Void> uploadTemplateFile(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                           @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                           @RequestParam(value = "filename", required = false) String originalFilename,
                                           @PathVariable long questionId,
                                           HttpServletRequest request)
    {
        return questionFileSetupCompetitionService.uploadTemplateFile(contentType, contentLength, originalFilename, questionId, request).toPostCreateResponse();
    }

    @DeleteMapping(value = "/template-file/{questionId}", produces = "application/json")
    public RestResult<Void> deleteFile(@PathVariable long questionId) {
        return questionFileSetupCompetitionService.deleteTemplateFile(questionId).toDeleteResponse();
    }
}
