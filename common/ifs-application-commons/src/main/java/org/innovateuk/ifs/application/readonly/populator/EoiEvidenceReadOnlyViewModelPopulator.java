package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.EoiEvidenceReadOnlyViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.competition.service.CompetitionEoiEvidenceConfigRestService;
import org.springframework.beans.factory.annotation.Autowired;

public class EoiEvidenceReadOnlyViewModelPopulator {

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Autowired
    private CompetitionEoiEvidenceConfigRestService competitionEoiEvidenceConfigRestService;

    public EoiEvidenceReadOnlyViewModel populate(ApplicationResource application) {
        return new EoiEvidenceReadOnlyViewModel(application.getId(), true, null, null);
    }
}
