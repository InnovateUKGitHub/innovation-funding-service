package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

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
    private FormInputRestService formInputRestService;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    public abstract BaseSectionViewModel populateModel(ApplicationForm form, Model model, ApplicationResource application, SectionResource section, UserResource user, BindingResult bindingResult, List<SectionResource> allSections, Long organisationId);

    protected NavigationViewModel addNavigation(SectionResource section, Long applicationId) {
        return applicationNavigationPopulator.addNavigation(section, applicationId);
    }

    protected Boolean calculateAllReadOnly(CompetitionResource competition, Long currentSectionId, Set<Long> markedAsCompleteSections) {
        return (null != competition && !competition.isOpen()) ||
                (null != markedAsCompleteSections && markedAsCompleteSections.contains(currentSectionId));
    }

    protected void addUserDetails(BaseSectionViewModel viewModel, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        viewModel.setUserIsLeadApplicant(userIsLeadApplicant);
        viewModel.setLeadApplicant(leadApplicant);
    }

    protected List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccessObjectOrThrowException();
    }

    protected Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Long organisationId = 0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    protected void addMappedSectionsDetails(BaseSectionViewModel viewModel,
                                            ApplicationResource application, CompetitionResource competition,
                                            SectionResource currentSection, Optional<OrganisationResource> userOrganisation,
                                            List<SectionResource> allSections, List<FormInputResource> inputs,
                                            List<SectionResource> parentSections) {

        Map<Long, SectionResource> sections =
                parentSections.stream().collect(Collectors.toMap(SectionResource::getId,
                    Function.identity()));

        List<QuestionResource> questions = questionService.findByCompetition(competition.getId());

        Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
            .collect(Collectors.toMap(
                SectionResource::getId,
                s -> getQuestionsBySection(s.getQuestions(), questions)
            ));

        Map<Long, List<QuestionResource>> subsectionQuestions;
        Map<Long, List<SectionResource>> subSections = new HashMap<>();
        subSections.put(currentSection.getId(), getSectionsFromListByIdList(currentSection.getChildSections(), allSections));

        subsectionQuestions = subSections.get(currentSection.getId()).stream()
                .collect(Collectors.toMap(SectionResource::getId,
                        ss -> getQuestionsBySection(ss.getQuestions(), questions)
                ));

        Map<Long, List<FormInputResource>> subSectionQuestionFormInputs = subsectionQuestions.values().stream().flatMap(a -> a.stream()).collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), inputs)));

        userOrganisation.ifPresent(organisationResource -> viewModel.setCompletedSections(sectionService.getCompleted(application.getId(), organisationResource.getId())));
        viewModel.setSections(sections);
        viewModel.setSectionQuestions(sectionQuestions);
        viewModel.setSubSections(subSections);
        viewModel.setSubsectionQuestions(subsectionQuestions);
        viewModel.setSubSectionQuestionFormInputs(subSectionQuestionFormInputs);
    }

    protected List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    protected void addSectionDetails(BaseSectionViewModel viewModel, SectionResource currentSection) {
        List<QuestionResource> questions = getQuestionsBySection(currentSection.getQuestions(), questionService.findByCompetition(currentSection.getCompetition()));
        questions.sort((QuestionResource q1, QuestionResource q2) -> q1.getPriority().compareTo(q2.getPriority()));
        Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
        sectionQuestions.put(currentSection.getId(), questions);
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(QuestionResource::getId, question ->
                        formInputRestService.getByQuestionIdAndScope(question.getId(), APPLICATION).getSuccessObjectOrThrowException()));

        viewModel.setQuestionFormInputs(questionFormInputs);
        viewModel.setCurrentSection(currentSection);
        viewModel.setSectionQuestions(sectionQuestions);
        viewModel.setTitle(currentSection.getName());
    }

    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, final List<SectionResource> allSections) {
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }

    private List<FormInputResource> findFormInputByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> input.getQuestion().equals(id));
    }
}
