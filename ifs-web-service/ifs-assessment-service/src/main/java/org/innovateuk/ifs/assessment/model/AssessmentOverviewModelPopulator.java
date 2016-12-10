package org.innovateuk.ifs.assessment.model;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.assessment.form.AssessmentOverviewForm;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.viewmodel.AssessmentOverviewRowViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

/**
 * Build the model for Assessment Overview view.
 */
@Component
public class AssessmentOverviewModelPopulator {
    private static final String MODEL_ATTRIBUTE_FORM = "form";
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;
    @Autowired
    private FormInputRestService formInputRestService;
    @Autowired
    private FormInputService formInputService;
    @Autowired
    private FileEntryRestService fileEntryRestService;
    @Autowired
    private FormInputResponseService formInputResponseService;

    public void populateModel(Long assessmentId, Long userId, AssessmentOverviewForm form, Model model) {
        final AssessmentResource assessment = getAssessment(assessmentId);
        final ApplicationResource application = getApplication(assessment.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);
        ProjectResource projectResource = projectService.getByApplicationId(application.getId());

        if (form == null) {
            form = new AssessmentOverviewForm();
        }

        addSections(model, competition, assessmentId);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentProject", projectResource);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
        model.addAttribute("completedQuestionsPercentage", applicationService.getCompleteQuestionsPercentage(application.getId()));
        model.addAttribute("daysLeftPercentage", competition.getAssessmentDaysLeftPercentage());
        model.addAttribute("daysLeft", competition.getAssessmentDaysLeft());
        model.addAttribute("assessmentId", assessmentId);

        List<FormInputResponseResource> applicantResponses = formInputResponseService.getByApplication(application.getId());
        addAppendices(application.getId(), applicantResponses, model);
    }

    private void addSections(Model model, CompetitionResource competition, Long assessmentId) {
        final List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        final List<SectionResource> parentSections = sectionService.filterParentSections(allSections);
        final List<QuestionResource> questions = questionService.findByCompetition(competition.getId());

        final Map<Long, SectionResource> sections =
                parentSections.stream().collect(toLinkedMap(SectionResource::getId,
                        Function.identity()));

        final Map<Long, List<SectionResource>> subSections = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId, s -> getSectionsFromListByIdList(s.getChildSections(), allSections)
                ));

        final Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> getQuestionsBySection(s.getQuestions(), questions)
                ));

        final List<SectionResource> financeSections = getFinanceSectionIds(parentSections);

        final List<AssessmentOverviewRowViewModel> rows = getOverviewRows(assessmentId, questions);
        final Map<Long, AssessmentOverviewRowViewModel> questionFeedback = rows.stream()
                .collect(Collectors.toMap(
                        AssessmentOverviewRowViewModel::getQuestion,
                        assessmentOverviewRowViewModel -> assessmentOverviewRowViewModel)
                );

        boolean hasFinanceSection = false;
        Long financeSectionId = null;
        if (!financeSections.isEmpty()) {
            hasFinanceSection = true;
            financeSectionId = financeSections.get(0).getId();
        }

        model.addAttribute("sections", sections);
        model.addAttribute("subSections", subSections);
        model.addAttribute("sectionQuestions", sectionQuestions);
        model.addAttribute("hasFinanceSection", hasFinanceSection);
        model.addAttribute("financeSectionId", financeSectionId);
        model.addAttribute("questionFeedback", questionFeedback);
    }

    private List<AssessmentOverviewRowViewModel> getOverviewRows(Long assessmentId, List<QuestionResource> questions) {
        List<AssessorFormInputResponseResource> inputResponseList = assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId);

        return questions.stream().map(question -> {
            List<FormInputResource> formInputs = formInputService.findAssessmentInputsByQuestion(question.getId());
            return new AssessmentOverviewRowViewModel(question, formInputs, inputResponseList);
        }).collect(Collectors.toList());
    }

    private AssessmentResource getAssessment(final Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(final Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private List<SectionResource> getFinanceSectionIds(List<SectionResource> sections) {
        return sections.stream()
                .filter(s -> SectionType.FINANCE.equals(s.getType()))
                .collect(Collectors.toList());
    }

    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, final List<SectionResource> allSections) {
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    private void addAppendices(Long applicationId, List<FormInputResponseResource> responses, Model model) {
        final List<AppendixResource> appendices = responses.stream().filter(fir -> fir.getFileEntry() != null).
                map(fir -> {
                    FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccessObject();
                    FileEntryResource fileEntryResource = fileEntryRestService.findOne(fir.getFileEntry()).getSuccessObject();
                    QuestionResource question = questionService.getById(formInputResource.getQuestion());
                    String title = question.getShortName() == null ? question.getName() : question.getShortName();
                    return new AppendixResource(applicationId, formInputResource.getId(), title, fileEntryResource);
                }).
                collect(Collectors.toList());
        model.addAttribute("appendices", appendices);
    }
}
