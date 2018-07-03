package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * Interface for retrieving key assessment statistics about competitions
 */
public interface CompetitionKeyAssessmentStatisticsRestService {

    RestResult<CompetitionReadyToOpenKeyAssessmentStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId);

    RestResult<CompetitionOpenKeyAssessmentStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);

    RestResult<CompetitionClosedKeyAssessmentStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);

    RestResult<CompetitionInAssessmentKeyAssessmentStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId);

}