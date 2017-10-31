package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.transactional.QuestionSetupService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling the statuses of the question resources during competition setup
 */
@RestController
@RequestMapping("/question/setup")
public class QuestionSetupController {

    @Autowired
    private QuestionSetupService questionSetupService;

    @PutMapping("/markAsComplete/{competitionId}/{parentSection}/{questionId}")
    public RestResult<Void> markQuestionSetupAsComplete(@PathVariable("competitionId") final Long competitionId,
                                                        @PathVariable("parentSection") final CompetitionSetupSection parentSection,
                                                        @PathVariable("questionId") final Long questionId){
        return questionSetupService.markQuestionInSetupAsComplete(questionId, competitionId, parentSection).toPutResponse();
    }

    @PutMapping("/markAsIncomplete/{competitionId}/{parentSection}/{questionId}")
    public RestResult<Void> markQuestionSetupAsInComplete(@PathVariable("competitionId") final Long competitionId,
                                                          @PathVariable("parentSection") final CompetitionSetupSection parentSection,
                                                          @PathVariable("questionId") final Long questionId){
        return questionSetupService.markQuestionInSetupAsIncomplete(questionId, competitionId, parentSection).toPutResponse();
    }

    @GetMapping("/getStatuses/{competitionId}/{parentSection}")
    public RestResult<Map<Long, Boolean>> getQuestionStatuses(@PathVariable("competitionId") final Long competitionId,
                                                              @PathVariable("parentSection") final CompetitionSetupSection parentSection){
        return questionSetupService.getQuestionStatuses(competitionId, parentSection).toGetResponse();
    }
}