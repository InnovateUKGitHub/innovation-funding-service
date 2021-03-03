package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.negate;

@Component
public class KtpGrantOfferLetterTemplatePopulator {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceNotesRestService projectFinanceNotesRestService;

    @Autowired
    private KtpFinanceModelPopulator ktpFinanceModelPopulator;

    public KtpGrantOfferLetterTemplateViewModel populate(ProjectResource project, CompetitionResource competition) {
        String projectName = project.getName();
        long applicationId = project.getApplication();
        ProjectUserResource projectUserResource = projectRestService.getProjectManager(project.getId()).getSuccess();
        List<PartnerOrganisationResource> organisations = partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();
        PartnerOrganisationResource leadOrg = organisations.stream().filter(PartnerOrganisationResource::isLeadOrganisation).findFirst().orElseThrow(ObjectNotFoundException::new);
        String partnerOrgName = organisations.stream().filter(negate(PartnerOrganisationResource::isLeadOrganisation)).map(PartnerOrganisationResource::getOrganisationName).findFirst().orElse("");
        UserResource user = userRestService.retrieveUserById(projectUserResource.getUser()).getSuccess();
        String projectManagerFirstName = user.getFirstName();
        String projectManagerLastName = user.getLastName();
        List<ProjectFinanceResource> allProjectFinances = projectFinanceRestService.getProjectFinances(project.getId()).getSuccess();
        List<NoteResource> allProjectNotes = new ArrayList<>();
        allProjectFinances.forEach(projectFinance ->
                                           projectFinanceNotesRestService.findAll(projectFinance.getId())
                                                   .ifSuccessful(allProjectNotes::addAll)
        );
        ProjectFinanceResource leadFinances = allProjectFinances.stream().filter(f -> f.getOrganisation().equals(leadOrg.getOrganisation())).findFirst().orElseThrow(ObjectNotFoundException::new);
        KtpFinanceModel ktpFinanceModel = ktpFinanceModelPopulator.populate(project, leadFinances);

        return new KtpGrantOfferLetterTemplateViewModel(applicationId,
                                                     projectManagerFirstName,
                                                     projectManagerLastName,
                                                     getAddressLines(project),
                                                     project.getCompetitionName(),
                                                     projectName,
                                                     leadOrg.getOrganisationName(),
                                                     partnerOrgName,
                                                     allProjectNotes,
                                                     ktpFinanceModel);
    }

    private List<String> getAddressLines(ProjectResource project) {
        List<String> addressLines = new ArrayList<>();
        if (project.getAddress() != null) {
            AddressResource address = project.getAddress();
            addressLines.add(address.getAddressLine1() != null ? address.getAddressLine1() : "");
            addressLines.add(address.getAddressLine2() != null ? address.getAddressLine2() : "");
            addressLines.add((address.getAddressLine3() != null ? address.getAddressLine3() : ""));
            addressLines.add(address.getTown() != null ? address.getTown() : "");
            addressLines.add(address.getCountry() != null ? address.getCountry() : "");
            addressLines.add(address.getPostcode() != null ? address.getPostcode() : "");
        }
        return addressLines;
    }
}
