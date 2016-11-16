package com.worth.ifs.competition.controller;

import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.transactional.CompetitionSetupQuestionService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * QuestionController exposes question data and operations through a REST API.
 */
@RestController
@RequestMapping("/competitionSetupQuestion")
public class CompetitionSetupQuestionController {

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @RequestMapping("/{id}")
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(@PathVariable("id") final Long id) {
        return competitionSetupQuestionService.getByQuestionId(id).toGetResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public RestResult<Void> save(@PathVariable("id") final Long questionId,
                                 @RequestBody final CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return competitionSetupQuestionService.save(competitionSetupQuestionResource).toPutResponse();
    }

}
