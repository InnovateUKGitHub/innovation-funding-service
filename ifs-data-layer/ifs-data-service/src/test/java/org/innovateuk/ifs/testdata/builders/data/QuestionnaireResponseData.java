package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireDecisionImplementation;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;


public class QuestionnaireResponseData {

    private ApplicationResource application;
    private String organisationName;
    private UserResource user;
    private CompetitionResource competition;
    private QuestionSetupType questionSetupType;
    private String questionnaireResponseUuid;
    private List<String> selectedOptions;
    private QuestionnaireDecisionImplementation outcome;

    public void setUser(UserResource user) {
        this.user = user;
    }

    public UserResource getUser() {
        return user;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public String getQuestionnaireResponseUuid() {
        return questionnaireResponseUuid;
    }

    public void setQuestionnaireResponseUuid(String questionnaireResponseUuid) {
        this.questionnaireResponseUuid = questionnaireResponseUuid;
    }

    public QuestionSetupType getQuestionSetupType() {
        return questionSetupType;
    }

    public void setQuestionSetupType(QuestionSetupType questionSetupType) {
        this.questionSetupType = questionSetupType;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public QuestionnaireDecisionImplementation getOutcome() {
        return outcome;
    }

    public void setOutcome(QuestionnaireDecisionImplementation outcome) {
        this.outcome = outcome;
    }
}
