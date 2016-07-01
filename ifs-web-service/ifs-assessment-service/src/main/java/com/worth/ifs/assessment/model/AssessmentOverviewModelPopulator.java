package com.worth.ifs.assessment.model;


import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.*;
import com.worth.ifs.assessment.form.AssessmentOverviewForm;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileEntryRestService;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputRestService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.worth.ifs.application.AbstractApplicationController.FORM_MODEL_ATTRIBUTE;
import static com.worth.ifs.application.resource.SectionType.ORGANISATION_FINANCES;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class AssessmentOverviewModelPopulator {
    private static final Log LOG = LogFactory.getLog(AssessmentOverviewModelPopulator.class);

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private AssessorFeedbackRestService assessorFeedbackRestService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;
    @Autowired
    private FormInputRestService formInputRestService;
    @Autowired
    private FileEntryRestService fileEntryRestService;
    @Autowired
    private FormInputResponseService formInputResponseService;


    public void populateModel(Long assessmentId, Long userId, AssessmentOverviewForm form, Model model) throws InterruptedException, ExecutionException {
        final ApplicationResource application = getApplicationForAssessment(assessmentId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);
        ProjectResource projectResource = projectService.getByApplicationId(application.getId());

        if(form == null){
            form = new AssessmentOverviewForm();
        }

        addSections(model, competition, assessmentId);

        model.addAttribute(FORM_MODEL_ATTRIBUTE, form);
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentProject", projectResource);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
        model.addAttribute("completedQuestionsPercentage", applicationService.getCompleteQuestionsPercentage(application.getId()));
        model.addAttribute(("daysLeftPercentage"),competition.getAssessmentDaysLeftPercentage());
        model.addAttribute(("daysLeft"),competition.getAssessmentDaysLeft());

        List<FormInputResponseResource> responses = formInputResponseService.getByApplication(application.getId());
        addAppendices(application.getId(), responses, model);
    }

    private void addSections(Model model, CompetitionResource competition, Long assessmentId) {
        final List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        final List<SectionResource> parentSections = sectionService.filterParentSections(allSections);
        final List<QuestionResource> questions = questionService.findByCompetition(competition.getId());
        final List<AssessmentFeedbackResource> feedback = assessmentFeedbackService.getAllAssessmentFeedback(assessmentId);

        final Map<Long, SectionResource> sections =
                parentSections.stream().collect(Collectors.toMap(SectionResource::getId,
                        Function.identity()));

        final Map<Long, List<SectionResource>>   subSections = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId, s -> getSectionsFromListByIdList(s.getChildSections(), allSections)
                ));

        final Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> getQuestionsBySection(s.getQuestions(), questions)
                ));

        final List<SectionResource> financeSections = getFinanceSectionIds(parentSections);

        final Map<Long, AssessmentFeedbackResource> questionFeedback = feedback.stream()
                .collect(Collectors.toMap(
                        AssessmentFeedbackResource::getQuestion,
                        assessmentFeedbackResource -> assessmentFeedbackResource)
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

    private ApplicationResource getApplicationForAssessment(final Long assessmentId) throws InterruptedException, ExecutionException {
        return getApplication(getApplicationIdForProcessRole(getProcessRoleForAssessment(getAssessment(assessmentId))));
    }

    private AssessmentResource getAssessment(final Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(final Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private Future<ProcessRoleResource> getProcessRoleForAssessment(final AssessmentResource assessment) {
        return processRoleService.getById(assessment.getId());
    }

    private Long getApplicationIdForProcessRole(final Future<ProcessRoleResource> processRoleResource) throws InterruptedException, ExecutionException {
        return processRoleResource.get().getApplication();
    }

    private List<SectionResource> getFinanceSectionIds(List<SectionResource> sections){
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

    private void addCompletedDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {

        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = new TreeSet<>(completedSectionsByOrganisation.get(completedSectionsByOrganisation.keySet().stream().findFirst().get()));

        completedSectionsByOrganisation.forEach((key, values) -> sectionsMarkedAsComplete.retainAll(values));
        model.addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        model.addAttribute("allQuestionsCompleted", sectionService.allSectionsMarkedAsComplete(application.getId()));
        model.addAttribute("markedAsComplete", markedAsComplete);

        userOrganisation.ifPresent(org -> model.addAttribute("completedSections", completedSectionsByOrganisation.get(org.getId())));
        Boolean userFinanceSectionCompleted = isUserFinanceSectionCompleted(model, application, userOrganisation, completedSectionsByOrganisation);
        model.addAttribute("userFinanceSectionCompleted", userFinanceSectionCompleted);

    }

    private Boolean isUserFinanceSectionCompleted(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation, Map<Long, Set<Long>> completedSectionsByOrganisation) {

        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        List<SectionResource> eachOrganisationFinanceSections = getSectionsByType(allSections, ORGANISATION_FINANCES);

        Long eachCollaboratorFinanceSectionId = null;
        if(!eachOrganisationFinanceSections.isEmpty()) {
            eachCollaboratorFinanceSectionId = eachOrganisationFinanceSections.get(0).getId();
        }
        return completedSectionsByOrganisation.get(userOrganisation.get().getId()).contains(eachCollaboratorFinanceSectionId);
    }

    private List<SectionResource> getSectionsByType(List<SectionResource> list, SectionType type){
        return simpleFilter(list, s -> type.equals(s.getType()));
    }

    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Long organisationId=0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    private void addAppendices(Long applicationId, List<FormInputResponseResource> responses, Model model) {
        final List<AppendixResource> appendices = responses.stream().filter(fir -> fir.getFileEntry() != null).
                map(fir -> {
                    FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccessObject();
                    FileEntryResource fileEntryResource = fileEntryRestService.findOne(fir.getFileEntry()).getSuccessObject();
                    String title = formInputResource.getDescription() != null ? formInputResource.getDescription() : fileEntryResource.getName();
                    return new AppendixResource(applicationId, formInputResource.getId(), title, fileEntryResource);
                }).
                collect(Collectors.toList());
        model.addAttribute("appendices", appendices);
    }
}
