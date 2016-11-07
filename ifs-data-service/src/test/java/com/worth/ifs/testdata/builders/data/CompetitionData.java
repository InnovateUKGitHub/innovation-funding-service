package com.worth.ifs.testdata.builders.data;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO DW - document this class
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
