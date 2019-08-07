package org.innovateuk.ifs.application.forms.sections.yourfinances.populator;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel.YourFinancesRowViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel.YourFinancesViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class YourFinancesModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationUrlHelper applicationUrlHelper;

    @Autowired
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    public YourFinancesViewModel populate(long applicationId, long sectionId, long organisationId, UserResource user) {
        SectionResource yourFinances = sectionRestService.getById(sectionId).getSuccess();
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<Long> completedSections = sectionStatusRestService.getCompletedSectionIds(applicationId, organisationId).getSuccess();

        List<YourFinancesRowViewModel> rows = yourFinances.getChildSections().stream().map(subSectionId -> {
            SectionResource subSection = sectionRestService.getById(subSectionId).getSuccess();
            return new YourFinancesRowViewModel(subSection.getName(),
                    applicationUrlHelper.getSectionUrl(subSection.getType(), subSectionId, applicationId, organisationId, application.getCompetition()).get(),
                    completedSections.contains(subSection.getId()));
        }).collect(toList());
        return new YourFinancesViewModel(applicationId, application.getName(), competition,
                applicationFinanceSummaryViewModelPopulator.populate(applicationId, user),
                rows);
    }
}
