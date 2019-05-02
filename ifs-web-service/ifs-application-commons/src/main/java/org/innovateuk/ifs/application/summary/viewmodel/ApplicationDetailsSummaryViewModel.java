package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.time.LocalDate;

public class ApplicationDetailsSummaryViewModel extends AbstractQuestionSummaryViewModel implements ApplicationRowSummaryViewModel {

    private final String competitionName;
    private final String applicationName;
    private final LocalDate startDate;
    private final Long duration;
    private final Boolean resubmission;
    private final boolean canSelectInnovationArea;
    private final String innovationAreaName;
    private final String previousApplicationNumber;
    private final String previousApplicationTitle;

    public ApplicationDetailsSummaryViewModel(ApplicationSummaryData data, QuestionResource question) {
        super(data, question);
        this.competitionName = data.getCompetition().getName();
        this.applicationName = data.getApplication().getName();
        this.startDate = data.getApplication().getStartDate();
        this.duration = data.getApplication().getDurationInMonths();
        this.resubmission = data.getApplication().getResubmission();
        this.canSelectInnovationArea = data.getCompetition().getInnovationAreas().size() > 1;
        this.innovationAreaName = data.getApplication().getInnovationArea().getName();
        this.previousApplicationNumber = data.getApplication().getPreviousApplicationNumber();
        this.previousApplicationTitle = data.getApplication().getPreviousApplicationTitle();
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Long getDuration() {
        return duration;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public boolean isCanSelectInnovationArea() {
        return canSelectInnovationArea;
    }

    public String getInnovationAreaName() {
        return innovationAreaName;
    }

    public String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public String getPreviousApplicationTitle() {
        return previousApplicationTitle;
    }

    @Override
    public String getFragment() {
        return "application-details";
    }

}
