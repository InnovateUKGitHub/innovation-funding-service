package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.AssignCofundersViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssignCofundersViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public AssignCofundersViewModel populateModel(long competitionId, long applicationId, String filter, int page) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CofundersAvailableForApplicationPageResource cofundersAvailableForApplicationPageResource = cofunderAssignmentRestService.findAvailableCofundersForApplication(applicationId, filter, page - 1).getSuccess();
        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();

        return new AssignCofundersViewModel(competition, application, filter, cofundersAvailableForApplicationPageResource, organisations);
    }
}
