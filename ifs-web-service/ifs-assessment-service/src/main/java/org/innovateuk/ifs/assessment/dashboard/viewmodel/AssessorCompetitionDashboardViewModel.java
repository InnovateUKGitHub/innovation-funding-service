package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition Dashboard.
 */
@Getter
public class AssessorCompetitionDashboardViewModel {

    private long competitionId;
    private String competitionTitle;
    private String leadTechnologist;
    private boolean openEndCompetition;
    private Long batchIndex;
    private ZonedDateTime acceptDeadline;
    private ZonedDateTime submitDeadline;
    private List<AssessorCompetitionDashboardApplicationViewModel> submitted;
    private List<AssessorCompetitionDashboardApplicationViewModel> outstanding;
    private boolean submitVisible;

    public AssessorCompetitionDashboardViewModel(long competitionId, String competitionTitle, String leadTechnologist, boolean openEndCompetition, Long batchIndex, ZonedDateTime acceptDeadline, ZonedDateTime submitDeadline, List<AssessorCompetitionDashboardApplicationViewModel> submitted, List<AssessorCompetitionDashboardApplicationViewModel> outstanding, boolean submitVisible) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.leadTechnologist = leadTechnologist;
        this.openEndCompetition = openEndCompetition;
        this.batchIndex = batchIndex;
        this.acceptDeadline = acceptDeadline;
        this.submitDeadline = submitDeadline;
        this.submitted = submitted;
        this.outstanding = outstanding;
        this.submitVisible = submitVisible;
    }

}
