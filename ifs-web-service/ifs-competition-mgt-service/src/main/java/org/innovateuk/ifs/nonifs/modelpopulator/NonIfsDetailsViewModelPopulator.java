package org.innovateuk.ifs.nonifs.modelpopulator;

import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.nonifs.viewmodel.NonIfsDetailsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Populates a {@link org.innovateuk.ifs.nonifs.viewmodel.NonIfsDetailsViewModel}
 */
@Service
public class NonIfsDetailsViewModelPopulator {

    @Autowired
    private CategoryService categoryService;

    public NonIfsDetailsViewModel populate() {
        NonIfsDetailsViewModel viewModel = new NonIfsDetailsViewModel();
        viewModel.setInnovationSectors(categoryService.getInnovationSectors());
        viewModel.setInnovationAreas(categoryService.getInnovationAreas());
        return viewModel;
    }
}
