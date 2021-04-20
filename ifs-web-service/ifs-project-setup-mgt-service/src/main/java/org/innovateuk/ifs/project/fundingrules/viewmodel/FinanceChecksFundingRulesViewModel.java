package org.innovateuk.ifs.project.fundingrules.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.FundingRulesResource;
import org.innovateuk.ifs.project.finance.resource.FundingRulesState;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnswerTableViewModel;

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
    private AnswerTableViewModel questionsAndAnswers;
    private boolean readOnly;
    private boolean editMode;
    private String updatedBy;

    public FinanceChecksFundingRulesViewModel(ProjectResource project, CompetitionResource competition, OrganisationResource organisation,
                                              boolean leadPartnerOrganisation, FundingRulesResource fundingRulesResource,
                                              AnswerTableViewModel questionsAndAnswers,
                                              boolean editMode) {
        this.projectName = project.getName();
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.competition = competition;
        this.organisationName = organisation.getName();
        this.organisationId = organisation.getId();
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.fundingRules = fundingRulesResource.getFundingRules();
        this.readOnly = fundingRulesResource.getFundingRulesState() == FundingRulesState.APPROVED;
        this.questionsAndAnswers = questionsAndAnswers;
        this.editMode = editMode;
        if (fundingRulesResource.getFundingRulesInternalUserLastName() != null) {
            this.updatedBy = String.format("%s %s", fundingRulesResource.getFundingRulesInternalUserFirstName(), fundingRulesResource.getFundingRulesInternalUserLastName());
        }
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

    public AnswerTableViewModel getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public boolean isShowUpdatedMessage() {
        return !isReadOnly() && updatedBy != null;
    }

}