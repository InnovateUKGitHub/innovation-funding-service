package org.innovateuk.ifs.management.nonifs.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

/**
 * View model for the Non-IFS competition details page.
 */
public class NonIfsDetailsViewModel {

    private final CompetitionResource competition;
    private final List<InnovationSectorResource> innovationSectors;
    private final List<InnovationAreaResource> innovationAreas;
    private final List<FundingType> fundingTypes;

    public NonIfsDetailsViewModel(CompetitionResource competition, List<InnovationSectorResource> innovationSectors, List<InnovationAreaResource> innovationAreas, List<FundingType> fundingTypes) {
        this.competition = competition;
        this.innovationSectors = innovationSectors;
        this.innovationAreas = innovationAreas;
        this.fundingTypes = fundingTypes;
    }

    public CompetitionResource getCompetition() {
        return competition;
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
