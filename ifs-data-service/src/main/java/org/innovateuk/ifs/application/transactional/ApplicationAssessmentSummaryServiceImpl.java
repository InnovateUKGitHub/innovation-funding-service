package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
@Service
public class ApplicationAssessmentSummaryServiceImpl extends BaseTransactionalService implements ApplicationAssessmentSummaryService {

    @Override
    public ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn((Application application) -> {

            Competition competition = application.getCompetition();

            ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = new ApplicationAssessmentSummaryResource(application.getId(),
                    application.getName(),
                    competition.getId(),
                    competition.getName(),
                    getPartnerOrganisationNames(application));
            return applicationAssessmentSummaryResource;
        });
    }

    private List<String> getPartnerOrganisationNames(Application application) {
        return application.getProcessRoles().stream()
                .filter(processRole ->
                        processRole.isLeadApplicant() || processRole.isCollaborator())
                .map(ProcessRole::getOrganisation)
                .map(Organisation::getName)
                .collect(toList());
    }
}