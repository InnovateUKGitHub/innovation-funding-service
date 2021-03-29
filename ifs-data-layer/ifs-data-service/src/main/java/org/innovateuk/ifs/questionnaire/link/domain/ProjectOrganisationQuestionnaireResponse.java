package org.innovateuk.ifs.questionnaire.link.domain;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;

import javax.persistence.*;

@Entity
public class ProjectOrganisationQuestionnaireResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireResponseId", referencedColumnName="id")
    private QuestionnaireResponse questionnaireResponse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="projectId", referencedColumnName="id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionnaireResponse getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(QuestionnaireResponse questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}
