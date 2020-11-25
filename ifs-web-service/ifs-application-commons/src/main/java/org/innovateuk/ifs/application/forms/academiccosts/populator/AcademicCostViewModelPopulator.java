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
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD_WITH_VAT;

@Component
public class AcademicCostViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;
    @Autowired
    private UserRestService userRestService;

    public AcademicCostViewModel populate(long organisationId, long applicationId, long sectionId, UserResource user) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        boolean includeVat = STANDARD_WITH_VAT.equals(competition.getApplicationFinanceType());

        boolean complete = completedSectionIds.contains(sectionId);

        boolean userCanEdit = user.hasRole(Role.APPLICANT) && userRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .orElse(false);
        boolean open = userCanEdit && application.isOpen() && competition.isOpen();

        return new AcademicCostViewModel(
                getYourFinancesUrl(applicationId, organisationId, userCanEdit),
                application.getName(),
                competition.getName(),
                organisation.getName(),
                applicationId,
                sectionId,
                organisationId,
                finance.getId(),
                user.isInternalUser(),
                includeVat,
                open,
                complete);
    }


    private String getYourFinancesUrl(long applicationId, long organisationId, boolean applicant) {
        return applicant ?
                String.format("/application/%d/form/FINANCE", applicationId) :
                String.format("/application/%d/form/FINANCE/%d", applicationId, organisationId);
    }
}
