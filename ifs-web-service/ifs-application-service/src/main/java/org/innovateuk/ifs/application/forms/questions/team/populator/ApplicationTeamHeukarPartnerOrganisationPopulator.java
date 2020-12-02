package org.innovateuk.ifs.application.forms.questions.team.populator;

import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamHeukarPartnerOrganisationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationTeamHeukarPartnerOrganisationPopulator {

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    public ApplicationTeamHeukarPartnerOrganisationViewModel populate(ApplicationResource applicationResource, long questionId) {
        List<OrganisationTypeResource> organisationTypeResourceList = organisationTypeRestService.getAll().getSuccess();
        return new ApplicationTeamHeukarPartnerOrganisationViewModel(applicationResource, questionId, organisationTypeResourceList);
    }

}
