package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcurementGrantOfferLetterTemplatePopulator {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    public ProcurementGrantOfferLetterTemplateViewModel populate(ProjectResource project, CompetitionResource competition) {
        long applicationId = project.getApplication();
        List<PartnerOrganisationResource> organisations = partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();
        return new ProcurementGrantOfferLetterTemplateViewModel(applicationId, organisations.get(0).getOrganisationName());
    }
}
