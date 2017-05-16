package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;

/**
 * View model for showing the Live competitions
 */
public class NonIFSDashboardViewModel {
    private List<CompetitionSearchResultItem> competitions;
    private CompetitionCountResource counts;
    private List<InnovationAreaResource> innovateAreas;

    public NonIFSDashboardViewModel(List<CompetitionSearchResultItem> competitions,
                                    CompetitionCountResource counts,
                                    List<InnovationAreaResource> innovateAreas) {
        this.competitions = competitions;
        this.counts = counts;
        this.innovateAreas = innovateAreas;
    }

    public List<CompetitionSearchResultItem> getCompetitions() {
        return competitions;
    }

    public CompetitionCountResource getCounts() {
        return counts;
    }

    public List<InnovationAreaResource> getInnovateAreas() {
        return innovateAreas;
    }
}
