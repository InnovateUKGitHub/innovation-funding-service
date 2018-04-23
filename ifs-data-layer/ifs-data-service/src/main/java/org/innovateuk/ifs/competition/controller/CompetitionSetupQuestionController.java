package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.*;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.question.transactional.QuestionSetupCompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * CompetitionSetupQuestionController exposes competition setup application questions data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition-setup-question")
public class CompetitionSetupQuestionController {

    @Autowired
    private QuestionSetupCompetitionService questionCompetitionService;

    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionController")
    @GetMapping("/getById/{id}")
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(@PathVariable("id") final Long id) {
        return questionCompetitionService.getByQuestionId(id).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionController")
    @PutMapping("/save")
    public RestResult<Void> save(@RequestBody final CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return questionCompetitionService.update(competitionSetupQuestionResource).toPutResponse();
    }

    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionController")
    @PostMapping("/addDefaultToCompetition/{id}")
    public RestResult<CompetitionSetupQuestionResource> addDefaultToCompetitionId(@PathVariable("id") final Long competitionId) {
        return questionCompetitionService.createByCompetitionId(competitionId).toPostCreateResponse();
    }

    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionController")
    @DeleteMapping("/deleteById/{id}")
    public RestResult<Void> deleteById(@PathVariable("id") final Long questionId) {
        return questionCompetitionService.delete(questionId).toDeleteResponse();
    }

}
