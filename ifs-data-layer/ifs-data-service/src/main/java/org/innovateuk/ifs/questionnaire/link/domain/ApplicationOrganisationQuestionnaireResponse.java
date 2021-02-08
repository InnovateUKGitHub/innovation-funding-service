package org.innovateuk.ifs.questionnaire.link.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;

import javax.persistence.*;

@Entity
public class ApplicationOrganisationQuestionnaireResponse {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireResponseId", referencedColumnName="id")
    private QuestionnaireResponse questionnaireResponse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    public QuestionnaireResponse getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(QuestionnaireResponse questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}
