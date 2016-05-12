package com.worth.ifs.application.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.worth.ifs.ViewModel;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.Model;

import static com.worth.ifs.application.AbstractApplicationController.FORM_MODEL_ATTRIBUTE;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;

public class ApplicationOverviewModel implements ViewModel{
    private static final Log LOG = LogFactory.getLog(ApplicationOverviewModel.class);
    private final Model model;
    private final Services s;

    public ApplicationOverviewModel(Long applicationId, Long userId, ApplicationForm form, Model model, Services s){
        this.model = model;
        this.s = s;
        ApplicationResource application = s.getApplicationService().getById(applicationId);
        CompetitionResource competition = s.getCompetitionService().getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = s.getProcessRoleService().findProcessRolesByApplicationId(applicationId);
        Optional<OrganisationResource> userOrganisation = s.getOrganisationService().getUserForOrganisation(userId, userApplicationRoles);

        if(form == null){
            form = new ApplicationForm();
        }
        form.setApplication(application);
        addUserDetails(model, application, userId);

        addAssignableDetails(model, application, userOrganisation.orElse(null), userId);
        addCompletedDetails(model, application, userOrganisation);
        addSections(competition);

        model.addAttribute(FORM_MODEL_ATTRIBUTE, form);
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
        model.addAttribute("completedQuestionsPercentage", s.getApplicationService().getCompleteQuestionsPercentage(application.getId()));
    }

    @Override
    public Model getModel(){
        return model;
    }
    
    private void addSections(CompetitionResource competition) {
        final List<SectionResource> allSections = s.getSectionService().getAllByCompetitionId(competition.getId());
        final List<SectionResource> parentSections = s.getSectionService().filterParentSections(allSections);
        final List<QuestionResource> questions = s.getQuestionService().findByCompetition(competition.getId());

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
        model.addAttribute("sections", sections);
        model.addAttribute("subSections", subSections);
        model.addAttribute("sectionQuestions", sectionQuestions);
    }
    
    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, final List<SectionResource> allSections) {
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    private void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = s.getUserService().isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = s.getUserService().getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = s.getUserService().findById(leadApplicantProcessRole.getUser());

        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", leadApplicant);
    }

    private void addAssignableDetails(Model model, ApplicationResource application, OrganisationResource userOrganisation,
        Long userId) {

        if (isApplicationInViewMode(model, application, userOrganisation))
            return;

        Map<Long, QuestionStatusResource> questionAssignees = s.getQuestionService().getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.getId());

        List<QuestionStatusResource> notifications = s.getQuestionService().getNotificationsForUser(questionAssignees.values(), userId);
        s.getQuestionService().removeNotifications(notifications);
        List<InviteResource> pendingAssignableUsers = pendingInvitations(application);

        model.addAttribute("assignableUsers", s.getProcessRoleService().findAssignableProcessRoles(application.getId()));
        model.addAttribute("pendingAssignableUsers", pendingAssignableUsers);
        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }

    private boolean isApplicationInViewMode(Model model, ApplicationResource application, OrganisationResource userOrganisation) {
        if(!application.isOpen() || userOrganisation == null){
            //Application Not open, so add empty lists
            model.addAttribute("assignableUsers", new ArrayList<ProcessRoleResource>());
            model.addAttribute("pendingAssignableUsers", new ArrayList<InviteResource>());
            model.addAttribute("questionAssignees", new HashMap<Long, QuestionStatusResource>());
            model.addAttribute("notifications", new ArrayList<QuestionStatusResource>());
            return true;
        }
        return false;
    }

    private List<InviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = s.getInviteRestService().getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatusConstants.ACCEPTED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private void addCompletedDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        final Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        final Map<Long, Set<Long>> completedSectionsByOrganisation = s.getSectionService().getCompletedSectionsByOrganisation(application.getId());
        final Set<Long> sectionsMarkedAsComplete = new TreeSet<>(completedSectionsByOrganisation.get(completedSectionsByOrganisation.keySet().stream().findFirst().get()));
        final List<SectionResource> financeSections = s.getSectionService().getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE);

        boolean hasFinanceSection;
        Long financeSectionId;
        if(financeSections.isEmpty()) {
            hasFinanceSection = false;
            financeSectionId = null;
        } else {
            hasFinanceSection = true;
            financeSectionId = financeSections.get(0).getId();
        }

        userOrganisation.ifPresent(org -> model.addAttribute("completedSections", s.getSectionService().getCompleted(application.getId(), org.getId())));
        model.addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        model.addAttribute("allQuestionsCompleted", s.getSectionService().allSectionsMarkedAsComplete(application.getId()));
        model.addAttribute("markedAsComplete", markedAsComplete);
        model.addAttribute("hasFinanceSection", hasFinanceSection);
        model.addAttribute("financeSectionId", financeSectionId);

    }

    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Long organisationId=0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        return s.getQuestionService().getMarkedAsComplete(application.getId(), organisationId);
    }
}
