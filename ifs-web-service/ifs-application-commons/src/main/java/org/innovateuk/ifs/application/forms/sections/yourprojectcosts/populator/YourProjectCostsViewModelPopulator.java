package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD_WITH_VAT;

@Component
public class YourProjectCostsViewModelPopulator {

    private CompetitionRestService competitionRestService;
    private OrganisationRestService organisationRestService;
    private ApplicationRestService applicationRestService;
    private SectionService sectionService;

    YourProjectCostsViewModelPopulator() {
    }

    @Autowired
    public YourProjectCostsViewModelPopulator(CompetitionRestService competitionRestService,
                                              OrganisationRestService organisationRestService,
                                              ApplicationRestService applicationRestService,
                                              SectionService sectionService) {
        this.competitionRestService = competitionRestService;
        this.organisationRestService = organisationRestService;
        this.applicationRestService = applicationRestService;
        this.sectionService = sectionService;
    }

    public YourProjectCostsViewModel populate(long applicationId, long sectionId, long organisationId, boolean internalUser, String originQuery) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        boolean open = !internalUser && competition.isOpen() && application.isOpen();

        boolean complete = completedSectionIds.contains(sectionId);

        boolean includeVat = STANDARD_WITH_VAT.equals(competition.getApplicationFinanceType());

        boolean procurementCompetition = FundingType.PROCUREMENT == competition.getFundingType();

        return new YourProjectCostsViewModel(applicationId,
                sectionId,
                competition.getId(),
                organisationId,
                complete,
                open,
                includeVat,
                application.getName(),
                organisation.getName(),
                getYourFinancesUrl(applicationId, organisationId, internalUser, originQuery),
                procurementCompetition,
                competition.isH2020());
    }

    private String getYourFinancesUrl(long applicationId, long organisationId, boolean internalUser, String originQuery) {
        // IFS-4848 - we're constructing this URL in a few places - maybe a NavigationUtil?
        return internalUser ?
                String.format("/application/%d/form/FINANCE/%d%s", applicationId, organisationId, originQuery) :
                String.format("/application/%d/form/FINANCE", applicationId);
    }
}
