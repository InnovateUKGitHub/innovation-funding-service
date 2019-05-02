package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.viewmodel.FinanceSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FinanceSummaryViewModelPopulator {

    @Autowired
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;
    @Autowired
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;
    @Autowired
    private ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator;
    @Autowired
    private SectionRestService sectionRestService;

    public FinanceSummaryViewModel populate(ApplicationSummaryData data) {
        CompetitionResource competition = data.getCompetition();
        ApplicationResource application = data.getApplication();
        SectionResource financeSection = sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.FINANCE).getSuccess().get(0);
        return new FinanceSummaryViewModel(
                application.getId(),
                competition.isFullyFunded(),
                financeSection.getId(),
                applicationFinanceSummaryViewModelPopulator.populate(application.getId(), data.getUser()),
                applicationResearchParticipationViewModelPopulator.populate(application.getId()),
                applicationFundingBreakdownViewModelPopulator.populate(application.getId(), data.getUser()),
                application.isCollaborativeProject(),
                data.getApplication().isOpen() && data.getCompetition().isOpen());
    }
}
