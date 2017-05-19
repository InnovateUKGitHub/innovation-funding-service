package org.innovateuk.ifs.nonifs.modelpopulator;

import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.nonifs.viewmodel.NonIfsDetailsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Populates a {@link org.innovateuk.ifs.nonifs.viewmodel.NonIfsDetailsViewModel}
 */
@Service
public class NonIfsDetailsViewModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    public NonIfsDetailsViewModel populate() {
        NonIfsDetailsViewModel viewModel = new NonIfsDetailsViewModel();
        viewModel.setInnovationSectors(categoryRestService.getInnovationSectors().getSuccessObjectOrThrowException());
        viewModel.setInnovationAreas(categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException());
        return viewModel;
    }
}
