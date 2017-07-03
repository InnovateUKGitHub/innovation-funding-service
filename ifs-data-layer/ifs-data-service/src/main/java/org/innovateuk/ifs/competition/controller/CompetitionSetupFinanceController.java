package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to deal with the finance section of the competition setup.
 */
@RestController
@RequestMapping("/competition-setup-finance")
public class CompetitionSetupFinanceController {

    @Autowired
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    @PutMapping("/{id}")
    public RestResult<Void> save(@PathVariable("id") final Long competitionId,
                                 @RequestBody final CompetitionSetupFinanceResource competitionSetupFinanceResource) {
        return competitionSetupFinanceService.save(competitionSetupFinanceResource).toPutResponse();

    }
    @GetMapping("/{id}")
    public RestResult<CompetitionSetupFinanceResource> getForCompetition(@PathVariable("id") final Long competitionId){
        return competitionSetupFinanceService.getForCompetition(competitionId).toGetResponse();
    }


}
