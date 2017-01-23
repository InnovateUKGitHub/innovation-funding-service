package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.mapper.ApplicationCountSummaryMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class ApplicationCountSummaryServiceImpl extends BaseTransactionalService implements ApplicationCountSummaryService {

    @Autowired
    private ApplicationCountSummaryMapper applicationCountSummaryMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OrganisationRepository processRoleRepository;

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Override
    public ServiceResult<List<ApplicationCountSummaryResource>> getApplicationCountSummariesByCompetitionId(Long competitionId) {
        List<ApplicationStatistics> applicationStatistics = applicationStatisticsRepository.findByCompetition(competitionId);

        List<Organisation> organisations = organisationRepository.findAll(simpleMap(applicationStatistics, ApplicationStatistics::getLeadOrganisationId));

        return serviceSuccess(simpleMap(applicationStatistics, application -> {
            ApplicationCountSummaryResource summaryResource = applicationCountSummaryMapper.mapToResource(application);
            summaryResource.setLeadOrganisation(
                    organisations.stream()
                            .filter(organisation -> organisation.getId().equals(application.getLeadOrganisationId()))
                            .findFirst()
                            .map(Organisation::getName)
                            .orElse("")
            );

            return summaryResource;
        }));
    }
}
