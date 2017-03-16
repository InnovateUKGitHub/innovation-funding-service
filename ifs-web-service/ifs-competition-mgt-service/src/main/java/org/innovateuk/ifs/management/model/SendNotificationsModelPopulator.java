package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
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
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    public SendNotificationsViewModel populate(long competitionId, List<Long> applicationIds){

        ApplicationSummaryPageResource pagedApplications = applicationSummaryService.findByCompetitionId(competitionId, null, null, null, null);

        List<ApplicationSummaryResource> filteredApplications = pagedApplications.getContent().stream()
                .filter(application -> applicationIds.contains(application.getId()) )
                .collect(Collectors.toList());

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        CompetitionInFlightStatsViewModel keyStatistics = competitionInFlightStatsModelPopulator.populateStatsViewModel(competitionResource);
        return new SendNotificationsViewModel(filteredApplications, keyStatistics, competitionId, competitionResource.getName());
    }
}
