package org.innovateuk.ifs.application.forms.sections.common.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * A populator to build a CommonYourFinancesViewModel
 */
@Component
public class CommonYourFinancesViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private UserRestService userRestService;

    public CommonYourProjectFinancesViewModel populate(long organisationId, long applicationId, long sectionId, UserResource user) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        boolean sectionMarkedAsComplete = completedSectionIds.contains(sectionId);

        boolean userCanEdit = !user.isInternalUser() || !user.hasRole(Role.EXTERNAL_FINANCE) || userRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() == null || role.getOrganisationId().equals(organisationId))
                .orElse(false);
        boolean open = userCanEdit && application.isOpen() && competition.isOpen();

        return new CommonYourProjectFinancesViewModel(
                getYourFinancesUrl(applicationId, organisationId),
                application.getCompetitionName(),
                application.getName(),
                applicationId,
                sectionId,
                open,
                competition.isH2020(),
                sectionMarkedAsComplete,
                competition.isProcurement(),
                organisation.isInternational());
    }

    private String getYourFinancesUrl(long applicationId, long organisationId) {
        return String.format("%s%d/form/FINANCE/%d", APPLICATION_BASE_URL, applicationId, organisationId);
    }
}
