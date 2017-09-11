package org.innovateuk.ifs.competition.resource;


public class CompetitionCountResource {

    private Long liveCount;
    private Long projectSetupCount;
    private Long upcomingCount;
    private Long previousCount;
    private Long nonIfsCount;


    public CompetitionCountResource() {

    }

    public CompetitionCountResource(Long liveCount, Long projectSetupCount, Long upcomingCount, Long previousCount, Long nonIfsCount) {
        this.liveCount = liveCount;
        this.projectSetupCount = projectSetupCount;
        this.upcomingCount = upcomingCount;
        this.previousCount = previousCount;
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
    public Long getPreviousCount() {
        return previousCount;
    }
    public void setPreviousCount(Long completedCount) { this.previousCount = completedCount; }
    public Long getNonIfsCount() {
        return nonIfsCount;
    }

    public void setNonIfsCount(Long nonIfsCount) {
        this.nonIfsCount = nonIfsCount;
    }
}
