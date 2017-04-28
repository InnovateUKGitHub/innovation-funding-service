package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionAssignableViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * class with methods that are used on every model for sectionPages
 * these pages are rendered by the ApplicationFormController.applicationFormWithOpenSection method
 */
abstract class BaseSectionModelPopulator extends BaseModelPopulator {
    protected static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    public abstract BaseSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection);

    protected NavigationViewModel addNavigation(SectionResource section, Long applicationId) {
        return applicationNavigationPopulator.addNavigation(section, applicationId);
    }

    protected Boolean calculateAllReadOnly(BaseSectionViewModel sectionViewModel, ApplicantSectionResource applicantSectionResource) {
        return (null != applicantSectionResource.getCompetition() && !applicantSectionResource.getCompetition().isOpen()) ||
                (null != sectionViewModel.getCompletedSections() && sectionViewModel.getCompletedSections().contains(applicantSectionResource.getSection().getId()));
    }

    protected void addUserDetails(BaseSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
        Boolean userIsLeadApplicant = applicantSection.getCurrentApplicant().getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName());
        UserResource leadApplicant = applicantSection.getApplicants().stream()
                .filter(applicantResource -> applicantResource.getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ApplicantResource::getUser)
                .findAny().orElse(null);
        viewModel.setUserIsLeadApplicant(userIsLeadApplicant);
        viewModel.setLeadApplicant(leadApplicant);
    }

    protected List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccessObjectOrThrowException();
    }

    protected void addMappedSectionsDetails(BaseSectionViewModel viewModel, ApplicantSectionResource applicantSection) {

        Map<Long, SectionResource> sections = new HashMap<>();
        Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
        Map<Long, List<SectionResource>> subSections = new HashMap<>();
        Map<Long, List<QuestionResource>> subsectionQuestions = new HashMap<>();
        Map<Long, List<FormInputResource>> subSectionQuestionFormInputs;

        sections.put(applicantSection.getSection().getId(), applicantSection.getSection());
        sectionQuestions.put(applicantSection.getSection().getId(), applicantSection.getQuestions().stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()));
        subSections.put(applicantSection.getSection().getId(), applicantSection.getChildSections().stream().map(ApplicantSectionResource::getSection).collect(Collectors.toList()));
        List<ApplicantQuestionResource> subsectionApplicantQuestions = applicantSection.getChildSections().stream().map(ApplicantSectionResource::getQuestions).flatMap(List::stream).collect(Collectors.toList());
        subsectionQuestions.put(applicantSection.getSection().getId(), subsectionApplicantQuestions.stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()));
        subSectionQuestionFormInputs = simpleToMap(subsectionApplicantQuestions, applicantQuestion -> applicantQuestion.getQuestion().getId(),
                applicantQuestion -> applicantQuestion.getFormInputs().stream().map(ApplicantFormInputResource::getFormInput).collect(Collectors.toList()));

        viewModel.setCompletedSections(sectionService.getCompleted(applicantSection.getApplication().getId(), applicantSection.getCurrentApplicant().getOrganisation().getId()));
        viewModel.setSections(sections);
        viewModel.setSectionQuestions(sectionQuestions);
        viewModel.setSubSections(subSections);
        viewModel.setSubsectionQuestions(subsectionQuestions);
        viewModel.setSubSectionQuestionFormInputs(subSectionQuestionFormInputs);
    }

    protected void addSectionDetails(BaseSectionViewModel viewModel, ApplicantSectionResource sectionResource) {
        List<QuestionResource> questions = sectionResource.allQuestions().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList());
        questions.sort((QuestionResource q1, QuestionResource q2) -> q1.getPriority().compareTo(q2.getPriority()));
        Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
        sectionQuestions.put(sectionResource.getSection().getId(), questions);
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(QuestionResource::getId, question ->
                        formInputRestService.getByQuestionIdAndScope(question.getId(), APPLICATION).getSuccessObjectOrThrowException()));

        viewModel.setQuestionFormInputs(questionFormInputs);
        viewModel.setCurrentSection(sectionResource.getSection());
        viewModel.setSectionQuestions(sectionQuestions);
        viewModel.setTitle(sectionResource.getSection().getName());
    }


    protected void addQuestionsDetails(BaseSectionViewModel viewModel, ApplicantSectionResource applicantSection, Form form) {
        List<FormInputResponseResource> responses = applicantSection.allResponses()
                .map(ApplicantFormInputResponseResource::getResponse)
                .collect(Collectors.toList());
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);

        viewModel.setResponses(mappedResponses);

        if(form == null){
            form = new Form();
        }
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
    }

    protected void addSectionsMarkedAsComplete(BaseSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(applicantSection.getApplication().getId());
        Set<Long> sectionsMarkedAsComplete = completedSectionsByOrganisation.get(applicantSection.getCurrentApplicant().getOrganisation().getId());
        viewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
    }

    protected SectionAssignableViewModel addAssignableDetails(ApplicantSectionResource applicantSection) {

        if (isApplicationInViewMode(applicantSection.getApplication(), Optional.of(applicantSection.getCurrentApplicant().getOrganisation()))) {
            return new SectionAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees;

        questionAssignees = simpleToMap(applicantSection.allQuestionStatuses().filter(status -> status.getAssignee().isSameUser(applicantSection.getCurrentApplicant())).collect(Collectors.toList()),
                status -> status.getStatus().getQuestion(), ApplicantQuestionStatusResource::getStatus);

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), applicantSection.getCurrentApplicant().getUser().getId());
        questionService.removeNotifications(notifications);

        return new SectionAssignableViewModel(questionAssignees, notifications);
    }

}
