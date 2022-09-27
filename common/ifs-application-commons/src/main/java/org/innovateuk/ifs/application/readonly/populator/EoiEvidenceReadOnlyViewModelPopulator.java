package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.EoiEvidenceReadOnlyViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionEoiEvidenceConfigRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EoiEvidenceReadOnlyViewModelPopulator {

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Autowired
    private CompetitionEoiEvidenceConfigRestService competitionEoiEvidenceConfigRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    public EoiEvidenceReadOnlyViewModel populate(ApplicationResource application, UserResource user) {
        List<ProcessRoleResource> processRoleResources = processRoleRestService.findProcessRole(application.getId()).getSuccess();
        CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource = competitionEoiEvidenceConfigRestService.findOneByCompetitionId(application.getCompetition()).getSuccess();
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = applicationEoiEvidenceResponseRestService.findOneByApplicationId(application.getId()).getSuccess();
        FileEntryResource fileEntryresource = fileEntryRestService.findOne(applicationEoiEvidenceResponseResource.getFileEntryId()).getSuccess();

        boolean partner = processRoleResources.stream()
                .anyMatch(pr -> pr.getUser().equals(user.getId())
                        && !pr.getOrganisationId().equals(application.getLeadOrganisationId()));

        return new EoiEvidenceReadOnlyViewModel(application.getId(), application.isEnabledForExpressionOfInterest(), partner,
                competitionEoiEvidenceConfigResource.getEvidenceTitle(), fileEntryresource);
    }
}
