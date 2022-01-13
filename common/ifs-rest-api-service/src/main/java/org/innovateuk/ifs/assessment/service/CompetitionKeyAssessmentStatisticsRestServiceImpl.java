package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Interface for retrieving Competition Key Statistics Resources}
 */
@Service
public class CompetitionKeyAssessmentStatisticsRestServiceImpl extends BaseRestService implements
        CompetitionKeyAssessmentStatisticsRestService {

    private static final String COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL = "/competition-assessment-statistics";

    @Override
    public RestResult<CompetitionReadyToOpenKeyAssessmentStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(
            long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL, competitionId,
                "ready-to-open"), CompetitionReadyToOpenKeyAssessmentStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionOpenKeyAssessmentStatisticsResource> getOpenKeyStatisticsByCompetition(
            long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL, competitionId, "open"),
                CompetitionOpenKeyAssessmentStatisticsResource.class);
    }

    @Override
    public RestResult<CompetitionClosedKeyAssessmentStatisticsResource> getClosedKeyStatisticsByCompetition(
            long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL, competitionId, "closed"),
                CompetitionClosedKeyAssessmentStatisticsResource.class);
    }

    @Override
    public RestResult<CompetitionInAssessmentKeyAssessmentStatisticsResource>
    getInAssessmentKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL, competitionId,
                "in-assessment"), CompetitionInAssessmentKeyAssessmentStatisticsResource.class);
    }
}