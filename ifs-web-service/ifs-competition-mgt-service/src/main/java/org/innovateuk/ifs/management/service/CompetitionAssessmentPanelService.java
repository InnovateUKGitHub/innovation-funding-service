package org.innovateuk.ifs.management.service;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;

/**
 * Service for managing assessment panel requests
 */

public interface CompetitionAssessmentPanelService {
    AssessmentPanelKeyStatisticsResource getAssessmentPanelKeyStatistics(Long competitionId);

}
