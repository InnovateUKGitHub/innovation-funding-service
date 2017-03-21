package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.viewmodel.SendNotificationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SendNotificationsModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    public SendNotificationsViewModel populate(long competitionId, List<Long> applicationIds){


        ApplicationSummaryPageResource pagedApplications = applicationSummaryRestService
                .getAllApplications(competitionId, null, 0, Integer.MAX_VALUE, null)
                .getSuccessObjectOrThrowException();

        List<ApplicationSummaryResource> filteredApplications = pagedApplications.getContent().stream()
                .filter(application -> applicationIds.contains(application.getId()) )
                .collect(Collectors.toList());

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        CompetitionInFlightStatsViewModel keyStatistics = competitionInFlightStatsModelPopulator.populateStatsViewModel(competitionResource);
        return new SendNotificationsViewModel(filteredApplications, keyStatistics, competitionId, competitionResource.getName());
    }
}
