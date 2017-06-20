package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Running data context for generating Competitions
 */
public class CompetitionData {

    private CompetitionResource competition;
    private List<MilestoneResource> originalMilestones = new ArrayList<>();

    public CompetitionData() {
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public void addOriginalMilestone(MilestoneResource milestone) {
        originalMilestones.add(milestone);
    }

    public List<MilestoneResource> getOriginalMilestones() {
        return originalMilestones;
    }
}
