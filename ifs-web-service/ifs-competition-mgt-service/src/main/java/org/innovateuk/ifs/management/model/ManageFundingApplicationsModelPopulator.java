package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.form.ManageFundingApplicationsQueryForm;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.ManageFundingApplicationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageFundingApplicationsModelPopulator {

    private static int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private CompetitionService competitionService;

    public ManageFundingApplicationViewModel populate(ManageFundingApplicationsQueryForm queryForm, long competitionId){
        ApplicationSummaryPageResource results = applicationSummaryService.getWithFundingDecisionApplications(competitionId, queryForm.getSortField(), queryForm.getPage(), DEFAULT_PAGE_SIZE);
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return new ManageFundingApplicationViewModel(results, queryForm.getSortField(), queryForm.getFilter(), competitionId, competitionResource.getName());
    }
}
