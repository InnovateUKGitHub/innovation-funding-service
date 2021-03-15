package org.innovateuk.ifs.application.forms.sections.yourfeccosts.populator;

import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfeccosts.viewmodel.YourFECViewModel;
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
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Component
public class YourFECViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private ProcessRoleRestService processRoleRestService;
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    public YourFECViewModel populate(long organisationId, long applicationId, long sectionId, UserResource user) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        ApplicationFinanceResource applicationFinanceResource = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        boolean sectionMarkedAsComplete = completedSectionIds.contains(sectionId);

        boolean userCanEdit = user.hasRole(Role.APPLICANT) && processRoleRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .orElse(false);
        boolean open = userCanEdit && application.isOpen() && competition.isOpen();

        return new YourFECViewModel(
                getYourFinancesUrl(applicationId, organisationId),
                application.getCompetitionName(),
                application.getName(),
                applicationId,
                sectionId,
                open,
                competition.isH2020(),
                sectionMarkedAsComplete,
                competition.isProcurement(),
                organisation.isInternational(),
                applicationFinanceResource.getId());
    }

    private String getYourFinancesUrl(long applicationId, long organisationId) {
        return String.format("%s%d/form/FINANCE/%d", APPLICATION_BASE_URL, applicationId, organisationId);
    }
}
