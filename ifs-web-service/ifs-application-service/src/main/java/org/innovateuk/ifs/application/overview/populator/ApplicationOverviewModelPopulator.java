package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.common.populator.AbstractApplicationModelPopulator;
import org.innovateuk.ifs.application.overview.viewmodel.*;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;


/**
 * view model for the application overview page
 */

@Component
public class ApplicationOverviewModelPopulator extends AbstractApplicationModelPopulator {

    private AssignButtonsPopulator assignButtonsPopulator;
    private CompetitionService competitionService;
    private ProcessRoleService processRoleService;
    private OrganisationService organisationService;
    private UserService userService;
    private InviteRestService inviteRestService;
    private ProjectService projectService;
    private ApplicantRestService applicantRestService;
    private SectionService sectionService;
    private QuestionService questionService;

    public ApplicationOverviewModelPopulator(SectionService sectionService,
                                             QuestionService questionService,
                                             AssignButtonsPopulator assignButtonsPopulator,
                                             CompetitionService competitionService,
                                             ProcessRoleService processRoleService,
                                             OrganisationService organisationService,
                                             UserService userService,
                                             InviteRestService inviteRestService,
                                             ProjectService projectService,
                                             ApplicantRestService applicantRestService) {
        super(sectionService, questionService);
        this.questionService = questionService;
        this.sectionService = sectionService;
        this.assignButtonsPopulator = assignButtonsPopulator;
        this.competitionService = competitionService;
        this.processRoleService = processRoleService;
        this.organisationService = organisationService;
        this.userService = userService;
        this.inviteRestService = inviteRestService;
        this.projectService = projectService;
        this.applicantRestService = applicantRestService;
    }
    
    public ApplicationOverviewViewModel populateModel(ApplicationResource application, Long userId){
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);
        ProjectResource projectResource = projectService.getByApplicationId(application.getId());
        boolean projectWithdrawn = (projectResource != null && projectResource.isWithdrawn());

        ApplicationOverviewUserViewModel userViewModel = getUserDetails(application, userId);
        ApplicationOverviewAssignableViewModel assignableViewModel = getAssignableDetails(application, userOrganisation, userId);
        ApplicationOverviewCompletedViewModel completedViewModel = getCompletedDetails(application, userOrganisation);
        ApplicationOverviewSectionViewModel sectionViewModel = getSections(competition, application, userId);
        Long yourFinancesSectionId = getYourFinancesSectionId(application);

        int completedQuestionsPercentage = application.getCompletion() == null ? 0 : application.getCompletion().intValue();

        return new ApplicationOverviewViewModel(application, projectResource, projectWithdrawn, competition, userOrganisation.orElse(null),
                completedQuestionsPercentage, yourFinancesSectionId, userViewModel, assignableViewModel, completedViewModel, sectionViewModel);
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
        parentApplicantSections.forEach(applicantSectionResource ->
            applicantSectionResource.getApplicantQuestions().forEach(questionResource ->
                assignButtonViewModels.put(questionResource.getQuestion().getId(), assignButtonsPopulator.populate(applicantSectionResource, questionResource, questionResource.isCompleteByApplicant(applicantSectionResource.getCurrentApplicant())))
            )
        );

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

    private ApplicationOverviewUserViewModel getUserDetails(ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application.getId());
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        return new ApplicationOverviewUserViewModel(userIsLeadApplicant, leadApplicant,
                userIsLeadApplicant && application.isSubmittable());
    }

    private ApplicationOverviewAssignableViewModel getAssignableDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation,
                                                                        Long userId) {
        if (isApplicationInViewMode(application, userOrganisation)) {
            return new ApplicationOverviewAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.get().getId());

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);
        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        Future<List<ProcessRoleResource>> assignableUsers = processRoleService.findAssignableProcessRoles(application.getId());

        return new ApplicationOverviewAssignableViewModel(assignableUsers, pendingAssignableUsers, questionAssignees, notifications);
    }

    private boolean isApplicationInViewMode(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        return !application.isOpen() || !userOrganisation.isPresent();
    }

    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private Long getYourFinancesSectionId(ApplicationResource application) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .findFirst()
                .map(SectionResource::getId)
                .orElse(null);
    }

}
