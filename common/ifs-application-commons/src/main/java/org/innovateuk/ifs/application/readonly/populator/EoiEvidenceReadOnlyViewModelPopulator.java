package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.EoiEvidenceReadOnlyViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionEoiEvidenceConfigRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.springframework.beans.factory.annotation.Autowired;

public class EoiEvidenceReadOnlyViewModelPopulator {

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Autowired
    private CompetitionEoiEvidenceConfigRestService competitionEoiEvidenceConfigRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    public EoiEvidenceReadOnlyViewModel populate(ApplicationResource application) {
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = applicationEoiEvidenceResponseRestService.findOneByApplicationId(application.getId()).getSuccess();
        CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource = competitionEoiEvidenceConfigRestService.findOneByCompetitionId(application.getCompetition()).getSuccess();
        FileEntryResource fileEntryresource = fileEntryRestService.findOne(applicationEoiEvidenceResponseResource.getFileEntryId()).getSuccess();

        return new EoiEvidenceReadOnlyViewModel(application.getId(),
                true,
                competitionEoiEvidenceConfigResource.getEvidenceTitle(),
                fileEntryresource);
    }
}
