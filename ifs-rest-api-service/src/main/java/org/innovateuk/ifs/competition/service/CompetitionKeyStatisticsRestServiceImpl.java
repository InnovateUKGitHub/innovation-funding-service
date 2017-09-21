package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelInviteStatisticsResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.*;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Interface for retrieving Competition Key Statistics Resources}
 */
@Service
public class CompetitionKeyStatisticsRestServiceImpl extends BaseRestService implements CompetitionKeyStatisticsRestService {

    private String competitionKeyStatisticsRestURL = "/competitionStatistics";

    @Override
    public RestResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s",competitionKeyStatisticsRestURL,competitionId,"readyToOpen"), CompetitionReadyToOpenKeyStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s",competitionKeyStatisticsRestURL,competitionId,"open"), CompetitionOpenKeyStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s",competitionKeyStatisticsRestURL,competitionId,"closed"), CompetitionClosedKeyStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s",competitionKeyStatisticsRestURL,competitionId,"inAssessment"), CompetitionInAssessmentKeyStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s",competitionKeyStatisticsRestURL,competitionId,"funded"), CompetitionFundedKeyStatisticsResource.class);
    }

    @Override
    public RestResult<AssessmentPanelKeyStatisticsResource> getAssessmentPanelKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s",competitionKeyStatisticsRestURL, competitionId, "panel"), AssessmentPanelKeyStatisticsResource.class);
    }

    @Override
    public RestResult<AssessmentPanelInviteStatisticsResource> getAssessmentPanelInviteStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s",competitionKeyStatisticsRestURL, competitionId, "panelInvites"), AssessmentPanelInviteStatisticsResource.class);
    }
}
