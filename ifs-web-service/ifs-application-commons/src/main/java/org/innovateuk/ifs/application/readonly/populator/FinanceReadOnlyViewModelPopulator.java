package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationResearchParticipationViewModel;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.FinanceReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class FinanceReadOnlyViewModelPopulator extends AsyncAdaptor {

    private final ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;
    private final ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;
    private final ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator;
    private final SectionRestService sectionRestService;

    public FinanceReadOnlyViewModelPopulator(ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator,
                                             ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator,
                                             ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator,
                                             SectionRestService sectionRestService) {
        this.applicationFinanceSummaryViewModelPopulator = applicationFinanceSummaryViewModelPopulator;
        this.applicationFundingBreakdownViewModelPopulator = applicationFundingBreakdownViewModelPopulator;
        this.applicationResearchParticipationViewModelPopulator = applicationResearchParticipationViewModelPopulator;
        this.sectionRestService = sectionRestService;
    }

    public FinanceReadOnlyViewModel populate(ApplicationReadOnlyData data) {
        CompetitionResource competition = data.getCompetition();
        ApplicationResource application = data.getApplication();
        Future<SectionResource> financeSection = async(() -> sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.FINANCE).getSuccess().get(0));
        Future<ApplicationFinanceSummaryViewModel> applicationFinanceSummaryViewModel = async(() -> applicationFinanceSummaryViewModelPopulator.populate(application.getId(), data.getUser()));
        Future<ApplicationResearchParticipationViewModel> applicationResearchParticipationViewModel = async(() -> applicationResearchParticipationViewModelPopulator.populate(application.getId()));
        Future<ApplicationFundingBreakdownViewModel> applicationFundingBreakdownViewModel = async(() -> applicationFundingBreakdownViewModelPopulator.populate(application.getId(), data.getUser()));

        return new FinanceReadOnlyViewModel(
                application.getId(),
                competition.isFullyFunded(),
                resolve(financeSection).getId(),
                resolve(applicationFinanceSummaryViewModel),
                resolve(applicationResearchParticipationViewModel),
                resolve(applicationFundingBreakdownViewModel),
                application.isCollaborativeProject(),
                competition.isKtp());
    }
}
