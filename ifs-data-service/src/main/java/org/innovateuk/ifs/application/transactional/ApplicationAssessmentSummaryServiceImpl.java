package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

/**
 * Service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
@Service
public class ApplicationAssessmentSummaryServiceImpl extends BaseTransactionalService implements ApplicationAssessmentSummaryService {

    @Override
    public ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(application -> {
            Competition competition = application.getCompetition();

            ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = new ApplicationAssessmentSummaryResource(application.getId(),
                    application.getName(),
                    competition.getId(),
                    competition.getName(),
                    null);
            return applicationAssessmentSummaryResource;
        });
    }
}