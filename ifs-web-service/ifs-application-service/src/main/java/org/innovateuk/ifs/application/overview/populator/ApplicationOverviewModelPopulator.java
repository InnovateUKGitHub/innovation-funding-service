package org.innovateuk.ifs.application.overview.populator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewAssignableViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewCompletedViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewUserViewModel;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
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

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;


/**
 * view model for the application overview page
 */

@Component
public class ApplicationOverviewModelPopulator {

    private static final Log LOG = LogFactory.getLog(ApplicationOverviewModelPopulator.class);

    @Autowired
    private AssignButtonsPopulator assignButtonsPopulator;

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
    private ProjectService projectService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private ApplicantRestService applicantRestService;
    
    public ApplicationOverviewViewModel populateModel(ApplicationResource application, Long userId){
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);
        ProjectResource projectResource = projectService.getByApplicationId(application.getId());

        ApplicationOverviewUserViewModel userViewModel = getUserDetails(application, userId);
        ApplicationOverviewAssignableViewModel assignableViewModel = getAssignableDetails(application, userOrganisation.orElse(null), userId);
        ApplicationOverviewCompletedViewModel completedViewModel = getCompletedDetails(application, userOrganisation);
        ApplicationOverviewSectionViewModel sectionViewModel = getSections(competition, application, userId);
        Long yourFinancesSectionId = getYourFinancesSectionId(application);

        int completedQuestionsPercentage = application.getCompletion() == null ? 0 : application.getCompletion().intValue();

        List<ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccessObjectOrThrowException();

        return new ApplicationOverviewViewModel(application, projectResource, competition, userOrganisation.orElse(null),
                completedQuestionsPercentage, yourFinancesSectionId, userViewModel, assignableViewModel, completedViewModel, sectionViewModel,
                researchCategories);
    }
    
    private ApplicationOverviewSectionViewModel getSections(CompetitionResource competition, ApplicationResource application, Long userId) {
        final List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        final List<SectionResource> parentSections = sectionService.filterParentSections(allSections);
        final List<ApplicantSectionResource> parentApplicantSections = parentSections.stream().map(sectionResource -> applicantRestService.getSection(userId, application.getId(), sectionResource.getId())).collect(Collectors.toList());

        final SortedMap<Long, SectionResource> sections = CollectionFunctions.toSortedMap(parentSections, SectionResource::getId,
                Function.identity());

        final Map<Long, List<SectionResource>> subSections = parentSections.stream()
            .collect(Collectors.toMap(
                SectionResource::getId, s -> getSectionsFromListByIdList(s.getChildSections(), allSections)
            ));

        final Map<Long, List<QuestionResource>> sectionQuestions = parentApplicantSections.stream()
            .collect(Collectors.toMap(
                s -> s.getSection().getId(),
                s -> s.getApplicantQuestions().stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()))
            );

        final List<SectionResource> financeSections = getFinanceSectionIds(parentSections);

        boolean hasFinanceSection = false;
        Long financeSectionId = null;
        if (!financeSections.isEmpty()) {
            hasFinanceSection = true;
            financeSectionId = financeSections.get(0).getId();
        }

        Map<Long, AssignButtonsViewModel> assignButtonViewModels = new HashMap<>();
        parentApplicantSections.forEach(applicantSectionResource -> {
            applicantSectionResource.getApplicantQuestions().forEach(questionResource -> {
                assignButtonViewModels.put(questionResource.getQuestion().getId(), assignButtonsPopulator.populate(applicantSectionResource, questionResource, questionResource.isCompleteByApplicant(applicantSectionResource.getCurrentApplicant())));
            });
        });

        return new ApplicationOverviewSectionViewModel(sections, subSections, sectionQuestions, financeSections, hasFinanceSection, financeSectionId, assignButtonViewModels);
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

    private ApplicationOverviewUserViewModel getUserDetails(ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        return new ApplicationOverviewUserViewModel(userIsLeadApplicant, leadApplicant,
                userIsLeadApplicant && application.isSubmittable());
    }

    private ApplicationOverviewAssignableViewModel getAssignableDetails(ApplicationResource application, OrganisationResource userOrganisation,
                                                                        Long userId) {
        if (isApplicationInViewMode(application, userOrganisation)) {
            return new ApplicationOverviewAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.getId());

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);
        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        Future<List<ProcessRoleResource>> assignableUsers = processRoleService.findAssignableProcessRoles(application.getId());

        return new ApplicationOverviewAssignableViewModel(assignableUsers, pendingAssignableUsers, questionAssignees, notifications);
    }

    private boolean isApplicationInViewMode(ApplicationResource application, OrganisationResource userOrganisation) {
        return !application.isOpen() || userOrganisation == null;
    }

    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private ApplicationOverviewCompletedViewModel getCompletedDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);

        boolean allQuestionsCompleted = sectionService.allSectionsMarkedAsComplete(application.getId());
        boolean userFinanceSectionCompleted = isUserFinanceSectionCompleted(application, userOrganisation.get(), completedSectionsByOrganisation);

        ApplicationOverviewCompletedViewModel viewModel = new ApplicationOverviewCompletedViewModel(sectionsMarkedAsComplete, allQuestionsCompleted, markedAsComplete, userFinanceSectionCompleted);
        userOrganisation.ifPresent(org -> viewModel.setCompletedSections(completedSectionsByOrganisation.get(org.getId())));

        return viewModel;
    }

    private Set<Long> getCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = new HashSet<>();

        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));

        return combinedMarkedAsComplete;
    }


    private boolean isUserFinanceSectionCompleted(ApplicationResource application, OrganisationResource userOrganisation, Map<Long, Set<Long>> completedSectionsByOrganisation) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .map(SectionResource::getId)
                .anyMatch(id -> completedSectionsByOrganisation.get(userOrganisation.getId()).contains(id));
    }

    private Long getYourFinancesSectionId(ApplicationResource application) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .findFirst()
                .map(SectionResource::getId)
                .orElse(null);
    }


    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {

        Long organisationId = userOrganisation
                .map(OrganisationResource::getId)
                .orElse(0L);

        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }
}
