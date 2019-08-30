package org.innovateuk.ifs.management.competition.previous.viewmodel;

import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.project.internal.ProjectSetupStages;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

public class PreviousCompetitionViewModel {

    private final long competitionId;
    private final String competitionName;
    private final String competitionType;
    private final String fundingBody;
    private final ZonedDateTime applicationDeadline;
    private final String innovationSector;
    private final boolean ifsAdmin;
    private final boolean competitionCanHaveProjects;
    private final List<PreviousApplicationResource> applications;
    private final List<InternalProjectSetupRow> rows;
    private final Set<ProjectSetupStages> columns;

    public PreviousCompetitionViewModel(CompetitionResource competition,
                                        List<PreviousApplicationResource> applications,
                                        List<InternalProjectSetupRow> rows,
                                        boolean ifsAdmin) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.competitionType = competition.getCompetitionTypeName();
        this.fundingBody = "Innovate UK";
        this.applicationDeadline = toUkTimeZone(competition.getEndDate());
        this.innovationSector = competition.getInnovationSectorName();
        this.competitionCanHaveProjects = CompetitionCompletionStage.PROJECT_SETUP.equals(competition.getCompletionStage());
        this.applications = applications;
        this.rows = rows;
        this.columns = competition.getProjectSetupStages();
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

    public List<InternalProjectSetupRow> getRows() {
        return rows;
    }

    public Set<ProjectSetupStages> getColumns() {
        return columns;
    }
}
