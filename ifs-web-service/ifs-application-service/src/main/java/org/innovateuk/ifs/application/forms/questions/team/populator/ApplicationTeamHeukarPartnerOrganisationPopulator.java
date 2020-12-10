package org.innovateuk.ifs.application.forms.questions.team.populator;

import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamHeukarPartnerOrganisationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum;
import org.innovateuk.ifs.heukar.service.HeukarPartnerOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationTeamHeukarPartnerOrganisationPopulator {

    @Autowired
    private HeukarPartnerOrganisationRestService heukarPartnerOrganisationRestService;

    public ApplicationTeamHeukarPartnerOrganisationViewModel populate(ApplicationResource applicationResource, long questionId) {
        List<HeukarPartnerOrganisationTypeEnum> organisationTypeResourceList =
                heukarPartnerOrganisationRestService.getAllHeukarPartnerOrganisationTypes().getSuccess();

        return new ApplicationTeamHeukarPartnerOrganisationViewModel(applicationResource, questionId, organisationTypeResourceList);
    }

}
