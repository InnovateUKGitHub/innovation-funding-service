package org.innovateuk.ifs.application.forms.yourprojectcosts.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.ManagementYourProjectCostsViewModel;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YourProjectCostsViewModelPopulator {

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    public YourProjectCostsViewModel populate(long applicationId, long sectionId, UserResource user) {

        ApplicantSectionResource section = applicantRestService.getSection(user.getId(), applicationId, sectionId);

        boolean complete = section.isComplete(section.getCurrentApplicant());
        boolean open = section.getApplication().isOpen() &&
                section.getCompetition().isOpen();

        return new YourProjectCostsViewModel(applicationId,
                section.getSection().getId(),
                section.getCompetition().getId(),
                complete,
                open,
                section.getApplication().getName(),
                section.getCurrentApplicant().getOrganisation().getName(),
                String.format("/application/%d/form/FINANCE", applicationId));
    }

    public ManagementYourProjectCostsViewModel populateManagement(long applicationId, long sectionId, long organisationId, String originQuery) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return new ManagementYourProjectCostsViewModel(applicationId, sectionId, application.getCompetition(), application.getName(),
                organisation.getName(), String.format("/application/%d/form/FINANCE/%d%s", applicationId, organisationId, originQuery));

    }

}
