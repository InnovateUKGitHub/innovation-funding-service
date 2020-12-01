package org.innovateuk.ifs.application.forms.questions.team.populator;

import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddOrganisationTypeViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationTeamAddHeukarPartnerOrganisationPopulator {

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    public ApplicationTeamAddOrganisationTypeViewModel populate(ApplicationResource applicationResource, long questionId, Long selectedId) {
        List<OrganisationTypeResource> organisationTypeResourceList = organisationTypeRestService.getAll().getSuccess();

//        List<HeukarOrganisationTypeResource> existingTypes =
//                organisationTypeRestService.getHeukarOrganisationTypesForApplicationWithId(applicationResource.getId()).getSuccess();

        return new ApplicationTeamAddOrganisationTypeViewModel(applicationResource, questionId, organisationTypeResourceList);
    }

}
