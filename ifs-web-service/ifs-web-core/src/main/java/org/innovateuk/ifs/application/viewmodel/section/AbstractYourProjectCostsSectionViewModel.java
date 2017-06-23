package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;
import java.util.Optional;

/**
 * Abstract view model for project costs section.
 */
public abstract class AbstractYourProjectCostsSectionViewModel extends AbstractSectionViewModel {
    private List<ApplicantQuestionResource> costQuestions;
    private ApplicantQuestionResource applicantQuestion;
    private boolean complete;

    public AbstractYourProjectCostsSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId, boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    public abstract String getFinanceView();

    public List<ApplicantQuestionResource> getCostQuestions() {
        return costQuestions;
    }

    public void setCostQuestions(List<ApplicantQuestionResource> costQuestions) {
        this.costQuestions = costQuestions;
    }

    public ApplicantQuestionResource getApplicantQuestion() {
        return applicantQuestion;
    }

    public void setApplicantQuestion(ApplicantQuestionResource applicantQuestion) {
        this.applicantQuestion = applicantQuestion;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public QuestionResource getQuestion() {
        return applicantQuestion.getQuestion();
    }

    public boolean getShowTerms() {
        return !(getCurrentApplicant().isResearch() || isAllReadOnly());
    }
}

