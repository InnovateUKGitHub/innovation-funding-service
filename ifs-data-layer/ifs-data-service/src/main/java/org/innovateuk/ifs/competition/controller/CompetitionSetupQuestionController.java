package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.*;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * CompetitionSetupQuestionController exposes competition setup application questions data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition-setup-question")
public class CompetitionSetupQuestionController {

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @GetMapping("/{id}")
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(@PathVariable("id") final Long id) {
        return competitionSetupQuestionService.getByQuestionId(id).toGetResponse();
    }

    @PutMapping("/{id}")
    public RestResult<Void> save(@PathVariable("id") final Long questionId,
                                 @RequestBody final CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return competitionSetupQuestionService.save(competitionSetupQuestionResource).toPutResponse();
    }

}
