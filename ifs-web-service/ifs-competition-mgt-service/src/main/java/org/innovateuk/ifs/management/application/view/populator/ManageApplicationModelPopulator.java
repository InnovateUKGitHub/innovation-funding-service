package org.innovateuk.ifs.management.application.view.populator;

import org.innovateuk.ifs.application.common.populator.SummaryViewModelFragmentPopulator;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.populator.researchCategory.ApplicationResearchCategorySummaryModelPopulator;
import org.innovateuk.ifs.application.resource.AppendixResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.researchCategory.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.management.application.view.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.innovateuk.ifs.management.application.view.viewmodel.ManageApplicationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;

@Component
public class ManageApplicationModelPopulator {

    private SummaryViewModelFragmentPopulator summaryPopulator;
    private ApplicationOverviewIneligibilityModelPopulator applicationOverviewIneligibilityModelPopulator;
    private ApplicationResearchCategorySummaryModelPopulator applicationResearchCategorySummaryModelPopulator;

    public ManageApplicationModelPopulator(SummaryViewModelFragmentPopulator summaryPopulator,
                                           ApplicationOverviewIneligibilityModelPopulator applicationOverviewIneligibilityModelPopulator,
                                           ApplicationResearchCategorySummaryModelPopulator applicationResearchCategorySummaryModelPopulator) {
        this.summaryPopulator = summaryPopulator;
        this.applicationOverviewIneligibilityModelPopulator = applicationOverviewIneligibilityModelPopulator;
        this.applicationResearchCategorySummaryModelPopulator = applicationResearchCategorySummaryModelPopulator;
    }

    public ManageApplicationViewModel populate(ApplicationResource application,
                                               CompetitionResource competition,
                                               String backUrl,
                                               String queryParam,
                                               UserResource user,
                                               List<AppendixResource> appendices,
                                               ApplicationForm form) {

        boolean readOnly = user.hasRole(SUPPORT);
        boolean canReinstate = !(user.hasRole(SUPPORT) || user.hasRole(INNOVATION_LEAD));

        ApplicationOverviewIneligibilityViewModel ineligibilityViewModel = applicationOverviewIneligibilityModelPopulator.populateModel(application, competition);
        SummaryViewModel summaryViewModel = summaryPopulator.populate(application.getId(), user, form);
        ResearchCategorySummaryViewModel researchCategorySummaryViewModel = applicationResearchCategorySummaryModelPopulator.populate(application, user.getId(), false);

        return new ManageApplicationViewModel(
                summaryViewModel,
                backUrl,
                queryParam,
                readOnly,
                canReinstate,
                ineligibilityViewModel,
                researchCategorySummaryViewModel,
                appendices);
    }
}
