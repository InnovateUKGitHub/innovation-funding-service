package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.transactional.QuestionSetupService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/question/setup")
public class QuestionSetupStatusController {

    @Autowired
    private QuestionSetupService questionSetupService;

    @PutMapping("/markAsComplete/{questionId}/{competitionId}")
    public RestResult<Void> markQuestionSetupAsComplete(@PathVariable("questionId") final Long questionId,
                                                        @PathVariable("competitionId") final Long competitionId){
        return questionSetupService.markInSetupAsComplete(questionId, competitionId).toPutResponse();
    }

    @PutMapping("/markAsInComplete/{questionId}/{competitionId}")
    public RestResult<Void> markQuestionSetupAsInComplete(@PathVariable("questionId") final Long questionId,
                                                          @PathVariable("competitionId") final Long competitionId){
        return questionSetupService.markInSetupAsInComplete(questionId, competitionId).toPutResponse();
    }

    @GetMapping("/getStatuses/{competitionId}/{parentSection}")
    public RestResult<Map<Long, Boolean>> getQuestionStatuses(@PathVariable("competitionId") final Long competitionId,
                                                              @PathVariable("parentSection") final CompetitionSetupSection parentSection){
        return questionSetupService.getQuestionStatuses(competitionId, parentSection).toGetResponse();
    }
}