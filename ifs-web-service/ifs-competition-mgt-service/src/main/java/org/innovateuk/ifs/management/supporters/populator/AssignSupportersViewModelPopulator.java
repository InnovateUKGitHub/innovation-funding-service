package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.AssignSupportersViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssignSupportersViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public AssignSupportersViewModel populateModel(long competitionId, long applicationId, String filter, int page) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        SupportersAvailableForApplicationPageResource supportersAvailableForApplicationPageResource = supporterAssignmentRestService.findAvailableSupportersForApplication(applicationId, filter, page - 1).getSuccess();
        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();

        return new AssignSupportersViewModel(competition, application, filter, supportersAvailableForApplicationPageResource, organisations);
    }
}
