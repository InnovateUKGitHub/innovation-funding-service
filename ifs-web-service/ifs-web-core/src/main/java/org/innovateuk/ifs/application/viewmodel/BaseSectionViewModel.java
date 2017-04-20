package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceViewModel;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Generic ViewModel for common fields in SectionViewModels
 */
public abstract class BaseSectionViewModel {
    protected String title;

    protected SectionResource currentSection;
    protected Boolean hasFinanceSection;
    protected Boolean subFinanceSection;
    protected Long financeSectionId;

    protected Map<Long, FormInputResponseResource> responses;

    protected Boolean userIsLeadApplicant;
    protected UserResource leadApplicant;

    protected List<Long> completedSections;
    protected Map<Long, SectionResource> sections;
    protected Map<Long, List<FormInputResource>> questionFormInputs;
    protected Map<Long, List<QuestionResource>> sectionQuestions;
    protected Map<Long, List<SectionResource>> subSections;
    protected Map<Long, List<QuestionResource>> subsectionQuestions;
    protected Map<Long, List<FormInputResource>> subSectionQuestionFormInputs;
    protected Set<Long> sectionsMarkedAsComplete;

    protected BaseFinanceViewModel financeViewModel;
    protected BaseFinanceOverviewViewModel financeOverviewViewModel;
    protected SectionAssignableViewModel sectionAssignableViewModel;
    protected NavigationViewModel navigationViewModel;
    protected SectionApplicationViewModel sectionApplicationViewModel;

    protected UserResource currentUser;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCurrentSectionId() {
        if(null == currentSection) {
            return null;
        }
        return currentSection.getId();
    }

    public SectionResource getCurrentSection() {
        return currentSection;
    }

    public void setCurrentSection(SectionResource currentSection) {
        this.currentSection = currentSection;
    }

    public Boolean getHasFinanceSection() {
        return hasFinanceSection;
    }

    public void setHasFinanceSection(Boolean hasFinanceSection) {
        this.hasFinanceSection = hasFinanceSection;
    }

    public Long getFinanceSectionId() {
        return financeSectionId;
    }

    public void setFinanceSectionId(Long financeSectionId) {
        this.financeSectionId = financeSectionId;
    }

    public Map<Long, FormInputResponseResource> getResponses() {
        return responses;
    }

    public void setResponses(Map<Long, FormInputResponseResource> responses) {
        this.responses = responses;
    }

    public Boolean getUserIsLeadApplicant() {
        return userIsLeadApplicant;
    }

    public void setUserIsLeadApplicant(Boolean userIsLeadApplicant) {
        this.userIsLeadApplicant = userIsLeadApplicant;
    }

    public UserResource getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(UserResource leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public List<Long> getCompletedSections() {
        return completedSections;
    }

    public void setCompletedSections(List<Long> completedSections) {
        this.completedSections = completedSections;
    }

    public Map<Long, SectionResource> getSections() {
        return sections;
    }

    public void setSections(Map<Long, SectionResource> sections) {
        this.sections = sections;
    }

    public Map<Long, List<FormInputResource>> getQuestionFormInputs() {
        return questionFormInputs;
    }

    public void setQuestionFormInputs(Map<Long, List<FormInputResource>> questionFormInputs) {
        this.questionFormInputs = questionFormInputs;
    }

    public Map<Long, List<QuestionResource>> getSectionQuestions() {
        return sectionQuestions;
    }

    public void setSectionQuestions(Map<Long, List<QuestionResource>> sectionQuestions) {
        this.sectionQuestions = sectionQuestions;
    }

    public Map<Long, List<SectionResource>> getSubSections() {
        return subSections;
    }

    public void setSubSections(Map<Long, List<SectionResource>> subSections) {
        this.subSections = subSections;
    }

    public Map<Long, List<QuestionResource>> getSubsectionQuestions() {
        return subsectionQuestions;
    }

    public void setSubsectionQuestions(Map<Long, List<QuestionResource>> subsectionQuestions) {
        this.subsectionQuestions = subsectionQuestions;
    }

    public Map<Long, List<FormInputResource>> getSubSectionQuestionFormInputs() {
        return subSectionQuestionFormInputs;
    }

    public void setSubSectionQuestionFormInputs(Map<Long, List<FormInputResource>> subSectionQuestionFormInputs) {
        this.subSectionQuestionFormInputs = subSectionQuestionFormInputs;
    }

    public SectionAssignableViewModel getAssignable() {
        return getSectionAssignableViewModel();
    }

    public SectionAssignableViewModel getSectionAssignableViewModel() {
        return sectionAssignableViewModel;
    }

    public void setSectionAssignableViewModel(SectionAssignableViewModel sectionAssignableViewModel) {
        this.sectionAssignableViewModel = sectionAssignableViewModel;
    }

    public NavigationViewModel getNavigation() {
        return getNavigationViewModel();
    }

    public NavigationViewModel getNavigationViewModel() {
        return navigationViewModel;
    }

    public void setNavigationViewModel(NavigationViewModel navigationViewModel) {
        this.navigationViewModel = navigationViewModel;
    }

    public UserResource getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserResource currentUser) {
        this.currentUser = currentUser;
    }

    public SectionApplicationViewModel getApplication() {
        return getSectionApplicationViewModel();
    }

    public SectionApplicationViewModel getSectionApplicationViewModel() {
        return sectionApplicationViewModel;
    }

    public void setSectionApplicationViewModel(SectionApplicationViewModel sectionApplicationViewModel) {
        this.sectionApplicationViewModel = sectionApplicationViewModel;
    }

    public Object getCurrentQuestionFormInputs() {
        return null;
    }

    public Boolean getIsYourFinancesAndIsNotCompleted() {
        return getIsYourFinances() && !completedSections.contains(currentSection.getId());
    }

    public Boolean getIsYourFinances() {
        return null != currentSection && "Your finances".equals(currentSection.getName());
    }

    public Boolean getIsFinanceOverview() {
        return null != currentSection && "Finances overview".equals(currentSection.getName());
    }

    public Boolean getIsSection() {
        return Boolean.TRUE;
    }

    public Set<Long> getSectionsMarkedAsComplete() {
        return sectionsMarkedAsComplete;
    }

    public void setSectionsMarkedAsComplete(Set<Long> sectionsMarkedAsComplete) {
        this.sectionsMarkedAsComplete = sectionsMarkedAsComplete;
    }

    public Boolean isSubFinanceSection() {
        return subFinanceSection;
    }

    public void setSubFinanceSection(Boolean subFinanceSection) {
        this.subFinanceSection = subFinanceSection;
    }

    public Boolean getShowAgreeToStateAidOption() {
        return null != currentSection && SectionType.PROJECT_COST_FINANCES.equals(currentSection.getType()) && !getApplication().getAllReadOnly() && !isAcademicFinances();
    }

    public Boolean getShowAgreeToTermsOption() {
        return null != currentSection && SectionType.FUNDING_FINANCES.equals(currentSection.getType()) && !getApplication().getAllReadOnly();
    }

    public Boolean isShowReturnButtons() {
        return !isSubFinanceSection();
    }

    public BaseFinanceViewModel getFinanceViewModel() {
        return financeViewModel;
    }

    public void setFinanceViewModel(BaseFinanceViewModel financeViewModel) {
        this.financeViewModel = financeViewModel;
    }

    public BaseFinanceViewModel getFinance() {
        return getFinanceViewModel();
    }

    public BaseFinanceOverviewViewModel getFinanceOverviewViewModel() {
        return financeOverviewViewModel;
    }

    public void setFinanceOverviewViewModel(BaseFinanceOverviewViewModel financeOverviewViewModel) {
        this.financeOverviewViewModel = financeOverviewViewModel;
    }

    public BaseFinanceOverviewViewModel getFinanceOverview() {
        return getFinanceOverviewViewModel();
    }

    public Boolean getHasFinanceView() {
        if (null == financeViewModel) {
            return Boolean.FALSE;
        }

        if(null == getFinanceViewModel().getFinanceView()) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Boolean isOrgFinancialOverview(Long questionId) {
        return isSubFinanceSection()
                && (questionFormInputs.containsKey(questionId) && questionFormInputs.get(questionId).stream().anyMatch(formInputResource -> FormInputType.FINANCIAL_OVERVIEW_ROW.equals(formInputResource.getType())));
    }

    public List<FormInputResource> getFormInputsOrganisationSize(Long questionId) {
        return getFormInputsByType(questionId, FormInputType.ORGANISATION_SIZE);
    }

    public List<FormInputResource> getFormInputsFinancialOverview(Long questionId) {
        return getFormInputsByType(questionId, FormInputType.FINANCIAL_OVERVIEW_ROW);
    }

    public List<FormInputResource> getFormInputsFinancialEndYear(Long questionId) {
        return getFormInputsByType(questionId, FormInputType.FINANCIAL_YEAR_END);
    }

    public List<FormInputResource> getFormInputsFinancialStaffCount(Long questionId) {
        return getFormInputsByType(questionId, FormInputType.FINANCIAL_STAFF_COUNT);
    }

    private List<FormInputResource> getFormInputsByType(Long questionId, FormInputType formInputType) {
        if(questionFormInputs.containsKey(questionId)) {
            return questionFormInputs.get(questionId).stream().filter(formInputResource -> formInputType.equals(formInputResource.getType())).collect(toList());
        } 
        return Collections.emptyList();
    }

    private boolean isAcademicFinances() {
        return this instanceof OpenFinanceSectionViewModel && ((OpenFinanceSectionViewModel) this).getIsAcademicFinance();
    }
}
