package org.innovateuk.ifs.interview.viewmodel;


import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;

/**
 * Holder of model attributes for the Competition Interview Panel dashboard
 */
public class InterviewViewModel {
    private final long competitionId;
    private final String competitionName;
    private final CompetitionStatus competitionStatus;
    private final InterviewStatisticsResource keyStats;

    public InterviewViewModel(long competitionId, String competitionName, CompetitionStatus competitionStatus, InterviewStatisticsResource keyStats) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.keyStats = keyStats;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public InterviewStatisticsResource getKeyStats() {
        return keyStats;
    }
}
