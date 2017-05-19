package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * Abstract view model for sharing attributes that are on all dashboards
 */
public abstract class DashboardViewModel {
    protected Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions;
    protected CompetitionCountResource counts;

    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getCompetitions() {
        return competitions;
    }

    public CompetitionCountResource getCounts() {
        return counts;
    }
}
