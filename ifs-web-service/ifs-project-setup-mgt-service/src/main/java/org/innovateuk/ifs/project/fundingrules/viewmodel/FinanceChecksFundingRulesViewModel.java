package org.innovateuk.ifs.project.fundingrules.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

public class FinanceChecksFundingRulesViewModel {

    private String projectName;
    private Long projectId;
    private Long applicationId;
    private CompetitionResource competition;
    private Long organisationId;
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private FundingRules fundingRules;
    private List<QuestionnaireQuestionAnswerViewModel> questionsAndAnswers;
    private boolean readOnly;
    private boolean editMode;

    public FinanceChecksFundingRulesViewModel(ProjectResource project, CompetitionResource competition, OrganisationResource organisation,
                                              boolean leadPartnerOrganisation, FundingRules fundingRules,
                                              List<QuestionnaireQuestionAnswerViewModel> questionsAndAnswers, boolean readOnly,
                                              boolean editMode) {
        this.projectName = project.getName();
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.competition = competition;
        this.organisationName = organisation.getName();
        this.organisationId = organisation.getId();
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.fundingRules = fundingRules;
        this.questionsAndAnswers = questionsAndAnswers;
        this.readOnly = readOnly;
        this.editMode = editMode;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLeadPartnerOrganisation() {
        return leadPartnerOrganisation;
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public FundingRules getOtherFundingRules() {
        switch (fundingRules) {
            case STATE_AID:
                return FundingRules.SUBSIDY_CONTROL;
            case SUBSIDY_CONTROL:
                return FundingRules.STATE_AID;
            default: return null;
        }
    }

    public List<QuestionnaireQuestionAnswerViewModel> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isEditMode() {
        return editMode;
    }
}