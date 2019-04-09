package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.time.LocalDate;

public class ApplicationDetailsSummaryViewModel implements NewQuestionSummaryViewModel {

    private final String name;
    private final String competitionName;
    private final String applicationName;
    private final LocalDate startDate;
    private final Long duration;
    private final Boolean resubmission;
    private final boolean canSelectInnovationArea;
    private final String innovationAreaName;
    private final String previousApplicationNumber;
    private final String previousApplicationTitle;

    public ApplicationDetailsSummaryViewModel(QuestionResource question, ApplicationResource application, CompetitionResource competition) {
        this.name = question.getShortName();
        this.competitionName = competition.getName();
        this.applicationName = application.getName();
        this.startDate = application.getStartDate();
        this.duration = application.getDurationInMonths();
        this.resubmission = application.getResubmission();
        this.canSelectInnovationArea = competition.getInnovationAreas().size() > 1;
        this.innovationAreaName = application.getInnovationArea().getName();
        this.previousApplicationNumber = application.getPreviousApplicationNumber();
        this.previousApplicationTitle = application.getPreviousApplicationTitle();
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
    public String getName() {
        return name;
    }

    @Override
    public String getFragment() {
        return "application-details";
    }
}
