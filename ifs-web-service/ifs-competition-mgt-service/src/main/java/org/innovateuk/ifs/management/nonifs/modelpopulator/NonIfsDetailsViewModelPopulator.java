package org.innovateuk.ifs.management.nonifs.modelpopulator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.core.util.CompetitionSpecialSectors;
import org.innovateuk.ifs.management.nonifs.viewmodel.NonIfsDetailsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Populates a {@link org.innovateuk.ifs.management.nonifs.viewmodel.NonIfsDetailsViewModel}
 */
@Service
public class NonIfsDetailsViewModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    public NonIfsDetailsViewModel populate(CompetitionResource competition) {
        List<InnovationSectorResource> innovationSectorResourceList = categoryRestService.getInnovationSectors().getSuccess();
        removeOpenSector(innovationSectorResourceList);
        List<InnovationAreaResource> innovationAreaResources = categoryRestService.getInnovationAreas().getSuccess();

        return new NonIfsDetailsViewModel(competition, innovationSectorResourceList, innovationAreaResources, asList(FundingType.values()));
    }

    private void removeOpenSector(List<InnovationSectorResource> innovationSectorResourceList) {
        innovationSectorResourceList.removeIf(CompetitionSpecialSectors.sectorIsExcludedForNonIfs());
    }
}
