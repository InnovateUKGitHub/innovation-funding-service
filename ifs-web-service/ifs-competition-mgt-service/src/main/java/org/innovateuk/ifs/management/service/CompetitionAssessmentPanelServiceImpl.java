package org.innovateuk.ifs.management.service;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of assessment panel requests
 */

@Service
public class CompetitionAssessmentPanelServiceImpl implements CompetitionAssessmentPanelService {

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Override
    public AssessmentPanelKeyStatisticsResource getAssessmentPanelKeyStatistics(Long competitionId) {
        return competitionKeyStatisticsRestService.getAssessmentPanelKeyStatisticsByCompetition(competitionId).getSuccessObjectOrThrowException();
    }
}
