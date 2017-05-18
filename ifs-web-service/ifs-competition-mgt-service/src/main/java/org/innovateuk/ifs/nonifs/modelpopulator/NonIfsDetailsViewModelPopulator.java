package org.innovateuk.ifs.nonifs.modelpopulator;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competitionsetup.utils.CompetitionSpecialSectors;
import org.innovateuk.ifs.nonifs.viewmodel.NonIfsDetailsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Populates a {@link org.innovateuk.ifs.nonifs.viewmodel.NonIfsDetailsViewModel}
 */
@Service
public class NonIfsDetailsViewModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    public NonIfsDetailsViewModel populate() {
        NonIfsDetailsViewModel viewModel = new NonIfsDetailsViewModel();
        List<InnovationSectorResource> innovationSectorResourceList = categoryRestService.getInnovationSectors().getSuccessObjectOrThrowException();
        removeOpenSector(innovationSectorResourceList);

        viewModel.setInnovationSectors(innovationSectorResourceList);
        viewModel.setInnovationAreas(categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException());
        return viewModel;
    }

    private void removeOpenSector(List<InnovationSectorResource> innovationSectorResourceList) {
        innovationSectorResourceList.removeIf(CompetitionSpecialSectors.sectorIsExcludedForNonIfs());
    }
}
