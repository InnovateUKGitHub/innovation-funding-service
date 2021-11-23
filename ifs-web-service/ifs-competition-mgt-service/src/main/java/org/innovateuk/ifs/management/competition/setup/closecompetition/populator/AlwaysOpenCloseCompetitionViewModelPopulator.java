package org.innovateuk.ifs.management.competition.setup.closecompetition.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.closecompetition.viewmodel.AlwaysOpenCloseCompetitionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.competition.resource.MilestoneType.SUBMISSION_DATE;

@Component
public class AlwaysOpenCloseCompetitionViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    public AlwaysOpenCloseCompetitionViewModel populate(Long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ZonedDateTime submissionDate = milestoneRestService.getMilestoneByTypeAndCompetitionId(SUBMISSION_DATE, competitionId).getSuccess().getDate();

        List<Long> applicationIds = applicationSummaryRestService.getAllSubmittedApplicationIds(competitionId, empty(), empty()).getSuccess();
        List<ApplicationResource> applications = new ArrayList<>();
        applicationIds.forEach(applicationId -> applications.add(applicationRestService.getApplicationById(applicationId).getSuccess()));
        List<ApplicationResource> submittedApplications = applications.stream().filter(ApplicationResource::isSubmitted).collect(Collectors.toList());

        return new AlwaysOpenCloseCompetitionViewModel(competitionId,
                competition.getName(),
                submissionDate,
                submittedApplications);
    }
}
