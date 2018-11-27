package org.innovateuk.ifs.application.forms.academiccosts.populator;

import org.innovateuk.ifs.application.forms.academiccosts.viewmodel.AcademicCostViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD_WITH_VAT;

@Component
public class AcademicCostViewModelPopulator {

    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private SectionService sectionService;
    private OrganisationRestService organisationRestService;
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    public AcademicCostViewModelPopulator(ApplicationRestService applicationRestService, CompetitionRestService competitionRestService, SectionService sectionService, OrganisationRestService organisationRestService, ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.sectionService = sectionService;
        this.organisationRestService = organisationRestService;
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public AcademicCostViewModel populate(long organisationId, long applicationId, long sectionId, boolean internalUser) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        boolean includeVat = STANDARD_WITH_VAT.equals(competition.getApplicationFinanceType());

        boolean complete = completedSectionIds.contains(sectionId);

        boolean open = !internalUser && competition.isOpen() && application.isOpen();

        return new AcademicCostViewModel(
                getYourFinancesUrl(applicationId, organisationId, internalUser),
                application.getName(),
                organisation.getName(),
                applicationId,
                sectionId,
                organisationId,
                finance.getId(),
                includeVat,
                open,
                complete);
    }


    private String getYourFinancesUrl(long applicationId, long organisationId, boolean internalUser) {
        return internalUser ?
                String.format("%s%d/form/FINANCE/%d", APPLICATION_BASE_URL, applicationId, organisationId) :
                String.format("%s%d/form/FINANCE", APPLICATION_BASE_URL, applicationId);
    }
}
