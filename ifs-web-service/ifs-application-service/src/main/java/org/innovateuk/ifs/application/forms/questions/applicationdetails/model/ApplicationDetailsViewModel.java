package org.innovateuk.ifs.application.forms.questions.applicationdetails.model;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.LocalDate;
import java.util.Set;

/**
 * View model for application details.
 */
public class ApplicationDetailsViewModel implements BaseAnalyticsViewModel {

    private ApplicationResource application;

    private boolean competitionIsClosingSoon;

    private int minProjectDuration;
    private int maxProjectDuration;
    private boolean minProjectDurationDictatedByCompetition;

    private Set<Long> competitionInnovationAreas;
    private String selectedInnovationAreaName;

    private boolean procurementCompetition;

    private boolean open;
    private boolean complete;

    private boolean ktpCompetition;
    private boolean canResubmit;

    public ApplicationDetailsViewModel(ApplicationResource application, CompetitionResource competition, boolean open, boolean complete, int maxMilestoneMonth) {
        this.application = application;
        this.competitionIsClosingSoon = competition.isClosingSoon();
        this.competitionInnovationAreas = competition.getInnovationAreas();
        this.minProjectDuration = Math.max(maxMilestoneMonth, competition.getMinProjectDuration());
        this.minProjectDurationDictatedByCompetition = minProjectDuration == competition.getMinProjectDuration();
        this.maxProjectDuration = competition.getMaxProjectDuration();
        this.selectedInnovationAreaName = application.getInnovationArea().getName();
        this.procurementCompetition = competition.isProcurement();
        this.open = open;
        this.complete = complete;
        this.ktpCompetition = competition.isKtp();
        this.canResubmit = competition.getResubmission();
    }

    @Override
    public Long getApplicationId() {
        return application.getId();
    }

    @Override
    public String getCompetitionName() {
        return application.getCompetitionName();
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public boolean isCompetitionIsClosingSoon() {
        return competitionIsClosingSoon;
    }

    public int getMinProjectDuration() {
        return minProjectDuration;
    }

    public int getMaxProjectDuration() {
        return maxProjectDuration;
    }

    public boolean isMinProjectDurationDictatedByCompetition() {
        return minProjectDurationDictatedByCompetition;
    }

    public Set<Long> getCompetitionInnovationAreas() {
        return competitionInnovationAreas;
    }

    public String getSelectedInnovationAreaName() {
        return selectedInnovationAreaName;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    /* view logic. */
    public boolean isReadonly() {
        return !open || complete;
    }

    public String getInnovationAreaText() {
        return  selectedInnovationAreaName != null ? "Change your innovation area" : "Choose your innovation area";
    }

    public boolean isCanSelectInnovationArea() {
        return competitionInnovationAreas.size() > 1;
    }

    public boolean isInnovationAreaHasBeenSelected() {
        return application.getNoInnovationAreaApplicable() || selectedInnovationAreaName != null;
    }

    public boolean isNoInnovationAreaApplicable() {
        return application.getNoInnovationAreaApplicable();
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isCanResubmit() {
        return canResubmit;
    }
}
