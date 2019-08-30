package org.innovateuk.ifs.status.viewmodel;

import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.project.internal.ProjectSetupStages;

import java.util.List;
import java.util.Set;

public abstract class BaseCompetitionStatusTableViewModel {

    private final long competitionId;
    private final String competitionName;
    private final Set<ProjectSetupStages> columns;
    private final List<InternalProjectSetupRow> rows;

    public BaseCompetitionStatusTableViewModel(long competitionId, String competitionName, Set<ProjectSetupStages> columns, List<InternalProjectSetupRow> rows) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.columns = columns;
        this.rows = rows;
    }

    public String getEmptyTableText() {
        return "There are currently no projects in this competition.";
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public Set<ProjectSetupStages> getColumns() {
        return columns;
    }

    public List<InternalProjectSetupRow> getRows() {
        return rows;
    }
}
