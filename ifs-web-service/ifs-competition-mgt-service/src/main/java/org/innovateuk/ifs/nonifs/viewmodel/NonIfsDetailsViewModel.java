package org.innovateuk.ifs.nonifs.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.util.List;

/**
 * View model for the Non-IFS competition details page.
 */
public class NonIfsDetailsViewModel {

    private final List<InnovationSectorResource> innovationSectors;
    private final List<InnovationAreaResource> innovationAreas;
    private final List<FundingType> fundingTypes;

    public NonIfsDetailsViewModel(List<InnovationSectorResource> innovationSectors, List<InnovationAreaResource> innovationAreas, List<FundingType> fundingTypes) {
        this.innovationSectors = innovationSectors;
        this.innovationAreas = innovationAreas;
        this.fundingTypes = fundingTypes;
    }

    public List<InnovationSectorResource> getInnovationSectors() {
        return innovationSectors;
    }

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public List<FundingType> getFundingTypes() {
        return fundingTypes;
    }
}
