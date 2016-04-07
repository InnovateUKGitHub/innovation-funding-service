package com.worth.ifs.application.resource;

import com.worth.ifs.competition.resource.CompetitionResource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a high-level overview of an application.
 */
public class CompetitionSummaryResource {
    private Long competitionId;
	private String competitionName;
	private CompetitionResource.Status competitionStatus;
	private LocalDateTime applicationDeadline;
	private Long totalNumberOfApplications;
	private Long applicationsStarted;
	private Long applicationsInProgress;
	private Long applicationsSubmitted;
	private Long applicationsNotSubmitted;

	public Long getCompetitionId() {
		return competitionId;
	}

	public void setCompetitionId(Long competitionId) {
		this.competitionId = competitionId;
	}

	public String getCompetitionName() {
		return competitionName;
	}

	public void setCompetitionName(String competitionName) {
		this.competitionName = competitionName;
	}

	public LocalDateTime getApplicationDeadline() {
		return applicationDeadline;
	}

	public void setApplicationDeadline(LocalDateTime applicationDeadline) {
		this.applicationDeadline = applicationDeadline;
	}

	public String getFormattedApplicationDeadline(){
		if(applicationDeadline == null){
			return "";
		}
		return applicationDeadline.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
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

	public Long getApplicationsNotSubmitted() {
		return applicationsNotSubmitted;
	}

	public void setApplicationsNotSubmitted(Long applicationsNotSubmitted) {
		this.applicationsNotSubmitted = applicationsNotSubmitted;
	}

	public CompetitionResource.Status getCompetitionStatus() {
		return competitionStatus;
	}

	public void setCompetitionStatus(CompetitionResource.Status competitionStatus) {
		this.competitionStatus = competitionStatus;
	}
}
