package org.innovateuk.ifs.questionnaire.response.viewmodel;

import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;

public class QuestionnaireWelcomeViewModel {

    private final String title;
    private final String subtitle;
    private final String description;
    private final String backLinkUrl;
    private final String backButtonText;

    public QuestionnaireWelcomeViewModel(QuestionnaireResource questionnaire) {
        this(questionnaire, null, null, null);
    }

    public QuestionnaireWelcomeViewModel(QuestionnaireResource questionnaire, String subtitle, String backLinkUrl, String backButtonText) {
        this.title = questionnaire.getTitle();
        this.subtitle = subtitle;
        this.description = questionnaire.getDescription();
        this.backLinkUrl = backLinkUrl;
        this.backButtonText = backButtonText;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public String getBackLinkUrl() {
        return backLinkUrl;
    }

    public String getBackButtonText() {
        return backButtonText;
    }
}
