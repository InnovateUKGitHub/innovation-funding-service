package org.innovateuk.ifs.application.forms.sections.common.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * A populator to build a CommonYourFinancesViewModel
 */
@Component
public class CommonYourFinancesViewModelPopulator {

    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private SectionService sectionService;

    @Autowired
    public CommonYourFinancesViewModelPopulator(
            ApplicationRestService applicationRestService,
            CompetitionRestService competitionRestService,
            SectionService sectionService) {

        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.sectionService = sectionService;
    }

    public CommonYourFinancesViewModel populate(long organisationId, long applicationId, long sectionId, boolean internalUser) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        boolean sectionMarkedAsComplete = completedSectionIds.contains(sectionId);

        boolean open = !internalUser && isOpen(application);

        return new CommonYourFinancesViewModel(
                getYourFinancesUrl(applicationId, organisationId, internalUser),
                application.getName(),
                applicationId,
                sectionId,
                open,
                sectionMarkedAsComplete);
    }

    private boolean isOpen(ApplicationResource application) {
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        return competition.isOpen() && application.isOpen();
    }

    private String getYourFinancesUrl(long applicationId, long organisationId, boolean internalUser) {
        // IFS-4848 - we're constructing this URL in a few places - maybe a NavigationUtil?
        return internalUser ?
                String.format("%s%d/form/FINANCE/%d", APPLICATION_BASE_URL, applicationId, organisationId) :
                String.format("%s%d/form/FINANCE", APPLICATION_BASE_URL, applicationId);
    }
}
