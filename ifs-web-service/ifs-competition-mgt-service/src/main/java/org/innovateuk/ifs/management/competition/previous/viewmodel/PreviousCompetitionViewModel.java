package org.innovateuk.ifs.management.competition.previous.viewmodel;

import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.security.StatusPermission;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusTableViewModel;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

public class PreviousCompetitionViewModel implements CompetitionStatusTableViewModel {

    private final long competitionId;
    private final String competitionName;
    private final String competitionType;
    private final String fundingBody;
    private final ZonedDateTime applicationDeadline;
    private final String innovationSector;
    private final boolean ifsAdmin;
    private final boolean competitionCanHaveProjects;

    private final List<PreviousApplicationResource> applications;
    private final CompetitionProjectsStatusResource competitionProjectsStatusResource;
    private Map<Long, StatusPermission> statusPermissions;

    public PreviousCompetitionViewModel(CompetitionResource competition, List<PreviousApplicationResource> applications, CompetitionProjectsStatusResource competitionProjectsStatusResource, Map<Long, StatusPermission> statusPermissions, boolean ifsAdmin) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.competitionType = competition.getCompetitionTypeName();
        this.fundingBody = "Innovate UK";
        this.applicationDeadline = toUkTimeZone(competition.getEndDate());
        this.innovationSector = competition.getInnovationSectorName();
        this.competitionCanHaveProjects = CompetitionCompletionStage.PROJECT_SETUP.equals(competition.getCompletionStage());
        this.applications = applications;
        this.competitionProjectsStatusResource = competitionProjectsStatusResource;
        this.statusPermissions = statusPermissions;
        this.ifsAdmin = ifsAdmin;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    public String getFundingBody() {
        return fundingBody;
    }

    public ZonedDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public String getInnovationSector() {
        return innovationSector;
    }

    public List<PreviousApplicationResource> getApplications() {
        return applications;
    }

    public boolean isIfsAdmin() {
        return ifsAdmin;
    }

    public boolean isCompetitionCanHaveProjects() {
        return competitionCanHaveProjects;
    }

    @Override
    public CompetitionProjectsStatusResource getCompetitionProjectsStatusResource() {
        return competitionProjectsStatusResource;
    }

    @Override
    public Map<Long, StatusPermission> getStatusPermissions() {
        return statusPermissions;
    }

    @Override
    public boolean isCanExportBankDetails() {
        return false;
    }

    @Override
    public String getEmptyTableText() {
        return "There are currently no completed projects in this competition.";
    }
}
