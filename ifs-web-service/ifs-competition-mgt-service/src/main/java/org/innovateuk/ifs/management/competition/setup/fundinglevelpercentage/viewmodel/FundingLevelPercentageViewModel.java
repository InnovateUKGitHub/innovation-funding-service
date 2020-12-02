package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.viewmodel;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

public class FundingLevelPercentageViewModel extends CompetitionSetupViewModel {

    private final List<CategoryResource> categories;
    private final List<OrganisationSize> sizes;

    public FundingLevelPercentageViewModel(
            GeneralSetupViewModel generalSetupViewModel,
            List<CategoryResource> categories, List<OrganisationSize> sizes) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.categories = categories;
        this.sizes = sizes;
    }

    public List<CategoryResource> getCategories() {
        return categories;
    }

    public List<OrganisationSize> getSizes() {
        return sizes;
    }
}
