package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionKeyStatisticsResource;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * CompetitionKeyStatisticsController exposes the statistics for a {@link Competition}
 */
@RestController
@RequestMapping("/competitionStatistics")
public class CompetitionKeyStatisticsController {

    @Autowired
    private CompetitionKeyStatisticsService competitionKeyStatisticsService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestResult<CompetitionKeyStatisticsResource> getCompetitionKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getKeyStatisticsByCompetition(id).toGetResponse();
    }
}
