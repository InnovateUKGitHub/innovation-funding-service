package com.worth.ifs.application.resource;

import java.time.LocalDateTime;

/**
 * Represents a high-level overview of an application.
 */
public class CompetitionSummaryResource {
    private Long id;
	private LocalDateTime applicationDeadline;
	private Long totalNumberOfApplications;
	private Long applicationsStarted;
	private Long applicationsInProgress;
	private Long applicationsSubmitted;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getApplicationDeadline() {
		return applicationDeadline;
	}

	public void setApplicationDeadline(LocalDateTime applicationDeadline) {
		this.applicationDeadline = applicationDeadline;
	}

	public Long getTotalNumberOfApplications() {
		return totalNumberOfApplications;
	}

	public void setTotalNumberOfApplications(Long totalNumberOfApplications) {
		this.totalNumberOfApplications = totalNumberOfApplications;
	}

	public Long getApplicationsStarted() {
		return applicationsStarted;
	}

	public void setApplicationsStarted(Long applicationsStarted) {
		this.applicationsStarted = applicationsStarted;
	}

	public Long getApplicationsInProgress() {
		return applicationsInProgress;
	}

	public void setApplicationsInProgress(Long applicationsInProgress) {
		this.applicationsInProgress = applicationsInProgress;
	}

	public Long getApplicationsSubmitted() {
		return applicationsSubmitted;
	}

	public void setApplicationsSubmitted(Long applicationsSubmitted) {
		this.applicationsSubmitted = applicationsSubmitted;
	}

}
