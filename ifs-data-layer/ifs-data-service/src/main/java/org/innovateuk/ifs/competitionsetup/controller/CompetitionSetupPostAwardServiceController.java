package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupPostAwardServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/competition/setup")
public class CompetitionSetupPostAwardServiceController {

    @Autowired
    private CompetitionSetupPostAwardServiceService competitionSetupPostAwardServiceService;

    @PostMapping("/{competitionId}/post-award-service/{postAwardService}")
    public RestResult<Void> configurePostAwardService(@PathVariable("competitionId") final long competitionId,
                                              @PathVariable("postAwardService") final PostAwardService postAwardService) {

        return competitionSetupPostAwardServiceService.configurePostAwardService(competitionId, postAwardService).toPostResponse();
    }
}
