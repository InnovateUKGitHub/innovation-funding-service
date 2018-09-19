package org.innovateuk.ifs.question.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.question.transactional.QuestionSetupCompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * QuestionController exposes competition setup application questions data and operations through a REST API.
 */
@RestController
@RequestMapping("/question-setup")
public class QuestionSetupCompetitionController {

    @Autowired
    private QuestionSetupCompetitionService questionSetupCompetitionService;

    @GetMapping("/get-by-id/{id}")
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(@PathVariable("id") final Long id) {
        return questionSetupCompetitionService.getByQuestionId(id).toGetResponse();
    }

    @PutMapping("/save")
    public RestResult<Void> save(@RequestBody final CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return questionSetupCompetitionService.update(competitionSetupQuestionResource).toPutResponse();
    }

    @PostMapping("/add-default-to-competition/{id}")
    public RestResult<CompetitionSetupQuestionResource> addDefaultToCompetitionId(
            @PathVariable("id") final Long competitionId) {
        return questionSetupCompetitionService.createByCompetitionId(competitionId).toPostCreateResponse();
    }

    @PostMapping("/add-research-category-question-to-competition/{id}")
    public RestResult<Void> addResearchCategoryQuestionToCompetition(@PathVariable("id") final long competitionId) {
        return questionSetupCompetitionService.addResearchCategoryQuestionToCompetition(competitionId)
                .toPostCreateResponse();
    }

    @DeleteMapping("/delete-by-id/{id}")
    public RestResult<Void> deleteById(@PathVariable("id") final long questionId) {
        return questionSetupCompetitionService.delete(questionId).toDeleteResponse();
    }
}
