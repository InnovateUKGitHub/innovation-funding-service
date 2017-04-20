package org.innovateuk.ifs.application.populator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.ApplicationFormController.MODEL_ATTRIBUTE_FORM;
import static org.innovateuk.ifs.application.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * view model for the application overview page
 */

@Component
public class ApplicationOverviewModelPopulator {

    private static final Log LOG = LogFactory.getLog(ApplicationOverviewModelPopulator.class);

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
    private CategoryService categoryService;
    
    public void populateModel(Long applicationId, Long userId, ApplicationForm form, Model model){
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(applicationId);
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);
        ProjectResource projectResource = projectService.getByApplicationId(applicationId);

        if(form == null){
            form = new ApplicationForm();
        }
        form.setApplication(application);
        addUserDetails(model, application, userId);

        addAssignableDetails(model, application, userOrganisation.orElse(null), userId);
        addCompletedDetails(model, application, userOrganisation);
        addSections(model, competition);
        addYourFinancesStatus(model, application);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentProject", projectResource);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
        model.addAttribute("completedQuestionsPercentage", application.getCompletion() == null ? 0 : application.getCompletion().intValue());

        FileDetailsViewModel assessorFeedbackViewModel = getAssessorFeedbackViewModel(application);
        model.addAttribute("assessorFeedback", assessorFeedbackViewModel);
        model.addAttribute("researchCategories", categoryService.getResearchCategories());
    }
    
    private void addSections(Model model, CompetitionResource competition) {
        final List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        final List<SectionResource> parentSections = sectionService.filterParentSections(allSections);
        final List<QuestionResource> questions = questionService.findByCompetition(competition.getId());

        final SortedMap<Long, SectionResource> sections = CollectionFunctions.toSortedMap(parentSections, SectionResource::getId,
                Function.identity());

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
    }

    private List<SectionResource> getFinanceSectionIds(List<SectionResource> sections){
        return sections.stream()
            .filter(s -> SectionType.FINANCE.equals(s.getType()))
            .collect(Collectors.toList());
    }
    
    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, List<SectionResource> allSections) {
        allSections.sort(Comparator.comparing(SectionResource::getPriority, Comparator.nullsLast(Comparator.naturalOrder())));
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, List<QuestionResource> questions) {
        questions.sort(Comparator.comparing(QuestionResource::getPriority, Comparator.nullsLast(Comparator.naturalOrder())));
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    private void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", leadApplicant);
        model.addAttribute("ableToSubmitApplication", userIsLeadApplicant && application.isSubmittable());
    }

    private void addAssignableDetails(Model model, ApplicationResource application, OrganisationResource userOrganisation,
        Long userId) {

        if (isApplicationInViewMode(model, application, userOrganisation))
            return;

        Map<Long, QuestionStatusResource> questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.getId());

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);
        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        model.addAttribute("pendingAssignableUsers", pendingAssignableUsers);
        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }

    private boolean isApplicationInViewMode(Model model, ApplicationResource application, OrganisationResource userOrganisation) {
        if(!application.isOpen() || userOrganisation == null){
            //Application Not open, so add empty lists
            model.addAttribute("assignableUsers", new ArrayList<ProcessRoleResource>());
            model.addAttribute("pendingAssignableUsers", new ArrayList<ApplicationInviteResource>());
            model.addAttribute("questionAssignees", new HashMap<Long, QuestionStatusResource>());
            model.addAttribute("notifications", new ArrayList<QuestionStatusResource>());
            return true;
        }
        return false;
    }

    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private void addCompletedDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {

        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);

        model.addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        model.addAttribute("allQuestionsCompleted", sectionService.allSectionsMarkedAsComplete(application.getId()));
        model.addAttribute("markedAsComplete", markedAsComplete);

        userOrganisation.ifPresent(org -> model.addAttribute("completedSections", completedSectionsByOrganisation.get(org.getId())));
        Boolean userFinanceSectionCompleted = isUserFinanceSectionCompleted(model, application, userOrganisation, completedSectionsByOrganisation);
        model.addAttribute("userFinanceSectionCompleted", userFinanceSectionCompleted);

    }

    private Set<Long> getCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = new HashSet<>();

        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));

        return combinedMarkedAsComplete;
    }


    private Boolean isUserFinanceSectionCompleted(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation, Map<Long, Set<Long>> completedSectionsByOrganisation) {

        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        List<SectionResource> eachOrganisationFinanceSections = getSectionsByType(allSections, FINANCE);

        Long eachCollaboratorFinanceSectionId = null;
        if(!eachOrganisationFinanceSections.isEmpty()) {
            eachCollaboratorFinanceSectionId = eachOrganisationFinanceSections.get(0).getId();
        }
        return completedSectionsByOrganisation.get(userOrganisation.get().getId()).contains(eachCollaboratorFinanceSectionId);
    }

    private void addYourFinancesStatus(Model model, ApplicationResource application) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);

        Long financeSectionId=null;
        if (!financeSections.isEmpty()) {
            financeSectionId = financeSections.get(0).getId();
        }
        model.addAttribute("financeSectionId", financeSectionId);
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

    private FileDetailsViewModel getAssessorFeedbackViewModel(ApplicationResource application) {

        if (!application.hasPublishedAssessorFeedback()) {
            return null;
        }

        RestResult<FileEntryResource> fileEntryResult = assessorFeedbackRestService.getAssessorFeedbackFileDetails(application.getId());

        if (fileEntryResult.isFailure()) {
            LOG.error("Should have been able to find FileEntry " + application.getAssessorFeedbackFileEntry() +
                    " for Assessor Feedback for application " + application.getId() + " - returning null");
            return null;
        }

        FileEntryResource fileEntry = fileEntryResult.getSuccessObject();
        return new FileDetailsViewModel(fileEntry);
    }
}
