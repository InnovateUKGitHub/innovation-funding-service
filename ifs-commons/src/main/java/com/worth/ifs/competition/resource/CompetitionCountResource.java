package com.worth.ifs.competition.resource;


public class CompetitionCountResource {

    private Long liveCount;
    private Long projectSetupCount;
    private Long upcomingCount;
    private Long completedCount;


    public CompetitionCountResource() {

    }

    public CompetitionCountResource(Long liveCount, Long projectSetupCount, Long upcomingCount, Long completedCount) {
        this.liveCount = liveCount;
        this.projectSetupCount = projectSetupCount;
        this.upcomingCount = upcomingCount;
        this.completedCount = completedCount;
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



}