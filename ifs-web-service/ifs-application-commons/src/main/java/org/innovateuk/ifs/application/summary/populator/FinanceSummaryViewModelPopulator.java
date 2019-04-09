package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.summary.viewmodel.FinanceSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.UserResource;
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
    @Autowired
    private CompetitionRestService competitionRestService;

    public FinanceSummaryViewModel populate(ApplicationResource application, UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        SectionResource financeSection = sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.FINANCE).getSuccess().get(0);
        return new FinanceSummaryViewModel(
                application.getId(),
                competition.isFullyFunded(),
                financeSection.getId(),
                applicationFinanceSummaryViewModelPopulator.populate(application.getId(), user),
                applicationResearchParticipationViewModelPopulator.populate(application.getId()),
                applicationFundingBreakdownViewModelPopulator.populate(application.getId(), user),
                application.isCollaborativeProject());
    }
}
