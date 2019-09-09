package org.innovateuk.ifs.status.viewmodel;

import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;

import java.util.List;

import static org.innovateuk.ifs.project.internal.ProjectSetupStage.BANK_DETAILS;

public abstract class BaseCompetitionStatusTableViewModel {

    private final long competitionId;
    private final String competitionName;
    private final List<ProjectSetupStage> columns;
    private final List<InternalProjectSetupRow> rows;
    private boolean canExportBankDetails;

    public BaseCompetitionStatusTableViewModel(long competitionId, String competitionName, List<ProjectSetupStage> columns, List<InternalProjectSetupRow> rows, boolean projectFinanceUser) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.columns = columns;
        this.rows = rows;
        this.canExportBankDetails = projectFinanceUser && columns.contains(BANK_DETAILS);
    }

    public abstract String getEmptyTableText();

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ProjectSetupStage> getColumns() {
        return columns;
    }

    public List<InternalProjectSetupRow> getRows() {
        return rows;
    }

    public boolean isCanExportBankDetails() {
        return canExportBankDetails;
    }
}
