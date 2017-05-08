package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.AbstractSectionViewModel;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

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
    private FormInputResponseService formInputResponseService;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    public abstract AbstractSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection);

//    protected NavigationViewModel addNavigation(SectionResource section, Long applicationId) {
//        return applicationNavigationPopulator.addNavigation(section, applicationId);
//    }
//
//    protected Boolean calculateAllReadOnly(AbstractSectionViewModel sectionViewModel, ApplicantSectionResource applicantSectionResource) {
//        return (null != applicantSectionResource.getCompetition() && !applicantSectionResource.getCompetition().isOpen()) ||
//                (null != sectionViewModel.getCompletedSections() && sectionViewModel.getCompletedSections().contains(applicantSectionResource.getSection().getId()));
//    }
//
//    protected void addUserDetails(AbstractSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
//        Boolean userIsLeadApplicant = applicantSection.getCurrentApplicant().getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName());
//        UserResource leadApplicant = applicantSection.getApplicants().stream()
//                .filter(ApplicantResource::isLead)
//                .map(ApplicantResource::getUser)
//                .findAny().orElse(null);
//        viewModel.setUserIsLeadApplicant(userIsLeadApplicant);
//        viewModel.setLeadApplicant(leadApplicant);
//    }
//
//    protected void addMappedSectionsDetails(AbstractSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
//
//        Map<Long, SectionResource> sections = new HashMap<>();
//        Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
//        Map<Long, List<SectionResource>> subSections = new HashMap<>();
//        Map<Long, List<QuestionResource>> subsectionQuestions;
//        Map<Long, List<FormInputResource>> subSectionQuestionFormInputs;
//
//        sections.put(applicantSection.getSection().getId(), applicantSection.getSection());
//        sectionQuestions.put(applicantSection.getSection().getId(), applicantSection.getApplicantQuestions().stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()));
//        subSections.put(applicantSection.getSection().getId(), applicantSection.getApplicantChildrenSections().stream().map(ApplicantSectionResource::getSection).collect(Collectors.toList()));
//        subsectionQuestions = simpleToMap(applicantSection.getApplicantChildrenSections(),
//                childSection -> childSection.getSection().getId(),
//                childSection -> childSection.getApplicantQuestions().stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()));
//        subSectionQuestionFormInputs = simpleToMap(applicantSection.getApplicantChildrenSections().stream().map(ApplicantSectionResource::getApplicantQuestions).flatMap(List::stream).collect(Collectors.toList()),
//                applicantQuestion -> applicantQuestion.getQuestion().getId(),
//                applicantQuestion -> applicantQuestion.getApplicantFormInputs().stream().map(ApplicantFormInputResource::getFormInput).collect(Collectors.toList()));
//
//        viewModel.setCompletedSections(sectionService.getCompleted(applicantSection.getApplication().getId(), applicantSection.getCurrentApplicant().getOrganisation().getId()));
//        viewModel.setSections(sections);
//        viewModel.setSectionQuestions(sectionQuestions);
//        viewModel.setSubSections(subSections);
//        viewModel.setSubsectionQuestions(subsectionQuestions);
//        viewModel.setSubSectionQuestionFormInputs(subSectionQuestionFormInputs);
//    }
//
//    protected void addSectionDetails(AbstractSectionViewModel viewModel, ApplicantSectionResource sectionResource) {
//        List<ApplicantQuestionResource> questions = sectionResource.getApplicantQuestions();
//        Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
//        sectionQuestions.put(sectionResource.getSection().getId(), questions.stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()));
//        Map<Long, List<FormInputResource>> questionFormInputs = simpleToMap(questions,
//                question -> question.getQuestion().getId(),
//                question -> question.getApplicantFormInputs().stream().map(ApplicantFormInputResource::getFormInput).collect(Collectors.toList()));
//
//        viewModel.setQuestionFormInputs(questionFormInputs);
//        viewModel.setCurrentSection(sectionResource.getSection());
//        viewModel.setSectionQuestions(sectionQuestions);
//        viewModel.setTitle(sectionResource.getSection().getName());
//    }
//
//
//    protected void addQuestionsDetails(AbstractSectionViewModel viewModel, ApplicantSectionResource applicantSection, Form form) {
//        List<FormInputResponseResource> responses = applicantSection.allResponses()
//                .map(ApplicantFormInputResponseResource::getResponse)
//                .collect(Collectors.toList());
//        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
//
//        viewModel.setResponses(mappedResponses);
//
//        if(form == null){
//            form = new Form();
//        }
//        Map<String, String> values = form.getFormInput();
//        mappedResponses.forEach((k, v) ->
//                values.put(k.toString(), v.getValue())
//        );
//        form.setFormInput(values);
//    }
//
//    protected void addSectionsMarkedAsComplete(AbstractSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
//        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(applicantSection.getApplication().getId());
//        Set<Long> sectionsMarkedAsComplete = completedSectionsByOrganisation.get(applicantSection.getCurrentApplicant().getOrganisation().getId());
//        viewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
//    }
//
//    protected SectionAssignableViewModel addAssignableDetails(ApplicantSectionResource applicantSection) {
//
//        if (isApplicationInViewMode(applicantSection.getApplication(), Optional.of(applicantSection.getCurrentApplicant().getOrganisation()))) {
//            return new SectionAssignableViewModel();
//        }
//
//        Map<Long, QuestionStatusResource> questionAssignees;
//
//        questionAssignees = simpleToMap(applicantSection.allAssignedQuestionStatuses().filter(status -> status.getAssignee().isSameUser(applicantSection.getCurrentApplicant())).collect(Collectors.toList()),
//                status -> status.getStatus().getQuestion(), ApplicantQuestionStatusResource::getStatus);
//
//        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), applicantSection.getCurrentApplicant().getUser().getId());
//        questionService.removeNotifications(notifications);
//
//        return new SectionAssignableViewModel(questionAssignees, notifications);
//    }

}
