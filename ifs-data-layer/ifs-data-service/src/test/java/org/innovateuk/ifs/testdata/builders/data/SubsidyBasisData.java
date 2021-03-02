package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireDecisionImplementation;
import org.innovateuk.ifs.user.resource.UserResource;


public class SubsidyBasisData {
    private CompetitionResource competition;
    private ApplicationResource application;
    private String organisationName;
    private UserResource user;
    private String questionnaireResponseUuid;
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

    public String getQuestionnaireResponseUuid() {
        return questionnaireResponseUuid;
    }

    public void setQuestionnaireResponseUuid(String questionnaireResponseUuid) {
        this.questionnaireResponseUuid = questionnaireResponseUuid;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public QuestionnaireDecisionImplementation getOutcome() {
        return outcome;
    }

    public void setOutcome(QuestionnaireDecisionImplementation outcome) {
        this.outcome = outcome;
    }
}
