package org.innovateuk.ifs.application.forms.questions.grantagreement.model;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

/**
 * View model for the grant agreement upload for Horizon competitions.
 */
public class GrantAgreementViewModel implements BaseAnalyticsViewModel {

    private final String applicationName;
    private final String competitionName;
    private final long questionId;
    private final long applicationId;
    private final String filename;
    private final boolean open;
    private final boolean complete;
    private final String competitionType;

    public GrantAgreementViewModel(long applicationId,
                                   String competitionName,
                                   String applicationName,
                                   long questionId,
                                   String filename,
                                   boolean open,
                                   boolean complete,
                                   String competitionType) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.filename = filename;
        this.open = open;
        this.complete = complete;
        this.competitionType = competitionType;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getFilename() {
        return filename;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    /* view logic */
    public boolean isUploaded() {
        return filename != null;
    }

    public boolean isReadonly() {
        return complete || !open;
    }
}
