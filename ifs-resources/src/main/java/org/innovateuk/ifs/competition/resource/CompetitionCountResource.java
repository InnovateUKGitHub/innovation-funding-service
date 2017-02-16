package org.innovateuk.ifs.competition.resource;


public class CompetitionCountResource {

    private Long liveCount;
    private Long projectSetupCount;
    private Long upcomingCount;
    private Long completedCount;
    private Long nonIfsCount;


    public CompetitionCountResource() {

    }

    public CompetitionCountResource(Long liveCount, Long projectSetupCount, Long upcomingCount, Long completedCount, Long nonIfsCount) {
        this.liveCount = liveCount;
        this.projectSetupCount = projectSetupCount;
        this.upcomingCount = upcomingCount;
        this.completedCount = completedCount;
        this.nonIfsCount = nonIfsCount;
    }

    public Long getLiveCount() {
        return liveCount;
    }
    public void setLiveCount(Long liveCount) { this.liveCount = liveCount; }
    public Long getProjectSetupCount() {
        return projectSetupCount;
    }
    public void setProjectSetupCount(Long projectSetupCount) { this.projectSetupCount = projectSetupCount; }
    public Long getUpcomingCount() {
        return upcomingCount;
    }
    public void setUpcomingCount(Long upcomingCount) { this.upcomingCount = upcomingCount; }
    public Long getCompletedCount() {
        return completedCount;
    }
    public void setCompletedCount(Long completedCount) { this.completedCount = completedCount; }
    public Long getNonIfsCount() {
        return nonIfsCount;
    }

    public void setNonIfsCount(Long nonIfsCount) {
        this.nonIfsCount = nonIfsCount;
    }
}
