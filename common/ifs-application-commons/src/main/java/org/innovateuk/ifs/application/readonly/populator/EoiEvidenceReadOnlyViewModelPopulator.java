package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.EoiEvidenceReadOnlyViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionEoiEvidenceConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
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
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Autowired
    private CompetitionEoiEvidenceConfigRestService competitionEoiEvidenceConfigRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    public EoiEvidenceReadOnlyViewModel populate(ApplicationResource application) {
        CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource = competitionEoiEvidenceConfigRestService.findByCompetitionId(application.getCompetition()).getSuccess();
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = applicationEoiEvidenceResponseRestService.findOneByApplicationId(application.getId()).getSuccess().get();
        FileEntryResource fileEntryresource = fileEntryRestService.findOne(applicationEoiEvidenceResponseResource.getFileEntryId()).getSuccess();

        return new EoiEvidenceReadOnlyViewModel(application.getId(), application.isEnabledForExpressionOfInterest(), competitionEoiEvidenceConfigResource.getEvidenceTitle(), fileEntryresource);
    }
}
