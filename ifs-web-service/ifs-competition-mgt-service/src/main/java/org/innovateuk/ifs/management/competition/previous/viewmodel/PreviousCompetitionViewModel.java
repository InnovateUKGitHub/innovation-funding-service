package org.innovateuk.ifs.management.competition.previous.viewmodel;

import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.status.viewmodel.BaseCompetitionStatusTableViewModel;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

public class PreviousCompetitionViewModel extends BaseCompetitionStatusTableViewModel {

    private final String competitionType;
    private final String fundingBody;
    private final ZonedDateTime applicationDeadline;
    private final String innovationSector;
    private final boolean competitionCanHaveProjects;
    private final List<PreviousApplicationResource> applications;

    public PreviousCompetitionViewModel(CompetitionResource competition,
                                            List<PreviousApplicationResource> applications,
                                            List<InternalProjectSetupRow> rows,
                                            boolean hasProjectFinanceRole,
                                            boolean ifsAdmin,
                                            boolean externalFinanceUser) {
        super(competition, rows, hasProjectFinanceRole, externalFinanceUser, ifsAdmin);
        this.competitionType = competition.getCompetitionTypeName();
        this.fundingBody = "Innovate UK";
        this.applicationDeadline = toUkTimeZone(competition.getEndDate());
        this.innovationSector = competition.getInnovationSectorName();
        this.competitionCanHaveProjects = CompetitionCompletionStage.PROJECT_SETUP.equals(competition.getCompletionStage());
        this.applications = applications;
    }

    @Override
    public String getEmptyTableText() {
        return "There are currently no completed projects in this competition.";
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

    public boolean isCompetitionCanHaveProjects() {
        return competitionCanHaveProjects;
    }
}
