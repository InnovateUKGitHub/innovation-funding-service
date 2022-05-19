package org.innovateuk.ifs.supporter.dashboard.viewmodel;

import lombok.Getter;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationResource;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

@Getter
public class SupporterCompetitionDashboardViewModel {

    private final long competitionId;
    private final String competitionName;
    private final ZonedDateTime deadline;
    private final List<SupporterDashboardApplicationResource> applications;
    private final boolean assessmentClosed;
    private final PaginationViewModel pagination;
    private String hash;

    public SupporterCompetitionDashboardViewModel(SupporterDashboardApplicationPageResource pageResource, CompetitionResource competition, String hash) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.deadline = toUkTimeZone(competition.getAssessorDeadlineDate());
        this.applications = pageResource.getContent();
        this.assessmentClosed = competition.getCompetitionStatus().isLaterThan(CompetitionStatus.IN_ASSESSMENT);
        this.pagination = new PaginationViewModel(pageResource);
        this.hash = hash;
    }

}
