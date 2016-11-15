package com.worth.ifs.application.model;

import com.worth.ifs.application.UserApplicationRole;
import com.worth.ifs.application.finance.view.FinanceHandler;
import com.worth.ifs.application.finance.view.FinanceOverviewModelManager;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.application.resource.SectionType.FINANCE;
import static com.worth.ifs.application.resource.SectionType.ORGANISATION_FINANCES;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Class for creating the model for the open section page.
 * These are rendered in the ApplicationFormController.applicationFormWithOpenSection method
 */
@Component
public class OpenSectionModelPopulator extends BaseSectionModelPopulator {

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private FinanceOverviewModelManager financeOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Override
    public void populateModel(final ApplicationForm form, final Model model, final ApplicationResource application, final SectionResource section, final UserResource user, final BindingResult bindingResult, final List<SectionResource> allSections){
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        addApplicationAndSections(application, competition, user.getId(), section, model, form, allSections);
        addOrganisationAndUserFinanceDetails(application.getCompetition(), application.getId(), user, model, form, allSections);
        addNavigation(section, application.getId(), model);

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());
        model.addAttribute("form", form);

        model.addAttribute("allReadOnly", calculateAllReadOnly(competition));
    }

    private Boolean calculateAllReadOnly(CompetitionResource competition) {
        return !competition.isOpen();
    }

    private void addApplicationDetails(ApplicationResource application,
        CompetitionResource competition,
        Long userId,
        SectionResource section,
        Model model,
        ApplicationForm form,
        List<ProcessRoleResource> userApplicationRoles,
        List<SectionResource> allSections) {
        Optional<OrganisationResource> userOrganisation = getUserOrganisation(userId, userApplicationRoles);


        if(form == null){
            form = new ApplicationForm();
        }
        form.setApplication(application);

        addQuestionsDetails(model, application, form);
        addUserDetails(model, application, userId);
        addApplicationFormDetailInputs(application, form);
        addMappedSectionsDetails(model, application, competition, section, userOrganisation, allSections);

        addAssignableDetails(model, application, userOrganisation.orElse(null), userId, section);
        addCompletedDetails(model, application, userOrganisation, allSections);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
    }

    private  void addApplicationFormDetailInputs(ApplicationResource application, Form form) {
        Map<String, String> formInputs = form.getFormInput();
        formInputs.put("application_details-title", application.getName());
        formInputs.put("application_details-duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() == null){
            formInputs.put("application_details-startdate_day", "");
            formInputs.put("application_details-startdate_month", "");
            formInputs.put("application_details-startdate_year", "");
        }else{
            formInputs.put("application_details-startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            formInputs.put("application_details-startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            formInputs.put("application_details-startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
        form.setFormInput(formInputs);
    }

    private void addOrganisationDetails(Model model, ApplicationResource application, List<ProcessRoleResource> userApplicationRoles) {
        SortedSet<OrganisationResource> organisations = getApplicationOrganisations(userApplicationRoles);
        model.addAttribute("academicOrganisations", getAcademicOrganisations(organisations));
        model.addAttribute("applicationOrganisations", organisations);

        List<String> activeApplicationOrganisationNames = organisations.stream().map(OrganisationResource::getName).collect(Collectors.toList());

        List<String> pendingOrganisationNames = pendingInvitations(application).stream()
            .map(ApplicationInviteResource::getInviteOrganisationName)
            .distinct()
            .filter(orgName -> StringUtils.hasText(orgName)
                && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        model.addAttribute("pendingOrganisationNames", pendingOrganisationNames);

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
            model.addAttribute("leadOrganisation", org)
        );
    }

    private void addQuestionsDetails(Model model, ApplicationResource application, Form form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        model.addAttribute("responses",mappedResponses);

        if(form == null){
            form = new Form();
        }
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
            values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
    }

    private List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return formInputResponseService.getByApplication(application.getId());
    }

    private void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", leadApplicant);
    }

    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Long organisationId=0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    private void addAssignableDetails(Model model, ApplicationResource application, OrganisationResource userOrganisation,
        Long userId, SectionResource currentSection) {

        if (isApplicationInViewMode(model, application, userOrganisation))
            return;

        Map<Long, QuestionStatusResource> questionAssignees;

        questionAssignees = questionService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(currentSection.getQuestions(), application.getId(), userOrganisation.getId());

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
            //com.worth.ifs.Application Not open, so add empty lists
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

    private void addMappedSectionsDetails(Model model, ApplicationResource application, CompetitionResource competition,
        SectionResource currentSection,
        Optional<OrganisationResource> userOrganisation,
        List<SectionResource> allSections) {
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        Map<Long, SectionResource> sections =
            parentSections.stream().collect(Collectors.toMap(SectionResource::getId,
                Function.identity()));

        List<QuestionResource> questions = questionService.findByCompetition(competition.getId());

        List<FormInputResource> formInputResources = formInputService.findApplicationInputsByCompetition(competition.getId());

        Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
            .collect(Collectors.toMap(
                SectionResource::getId,
                s -> getQuestionsBySection(s.getQuestions(), questions)
            ));
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream()
            .flatMap(a -> a.stream())
            .collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), formInputResources)));

        Map<Long, List<QuestionResource>> subsectionQuestions;
        Map<Long, List<SectionResource>>  subSections = new HashMap<>();
        subSections.put(currentSection.getId(), getSectionsFromListByIdList(currentSection.getChildSections(), allSections));

        subsectionQuestions = subSections.get(currentSection.getId()).stream()
            .collect(Collectors.toMap(SectionResource::getId,
                ss -> getQuestionsBySection(ss.getQuestions(), questions)
            ));

        Map<Long, List<FormInputResource>> subSectionQuestionFormInputs = subsectionQuestions.values().stream().flatMap(a -> a.stream()).collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), formInputResources)));

        userOrganisation.ifPresent(org -> model.addAttribute("completedSections", sectionService.getCompleted(application.getId(), org.getId())));
        model.addAttribute("sections", sections);
        model.addAttribute("questionFormInputs", questionFormInputs);
        model.addAttribute("sectionQuestions", sectionQuestions);
        model.addAttribute("subSections", subSections);
        model.addAttribute("subsectionQuestions", subsectionQuestions);
        model.addAttribute("subSectionQuestionFormInputs", subSectionQuestionFormInputs);
    }

    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, final List<SectionResource> allSections) {
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }

    private List<FormInputResource> findFormInputByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> input.getId().equals(id));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }


    private void addCompletedDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation, List<SectionResource> allSections) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids

        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = new TreeSet<>(completedSectionsByOrganisation.get(completedSectionsByOrganisation.keySet().stream().findFirst().get()));
        completedSectionsByOrganisation.forEach((key, values) -> sectionsMarkedAsComplete.retainAll(values));

        boolean hasFinanceSection = false;
        Long financeSectionId = null;
        if (!financeSections.isEmpty()) {
            hasFinanceSection = true;
            financeSectionId = financeSections.get(0).getId();
        }



        List<SectionResource> eachOrganisationFinanceSections = getSectionsByType(allSections, ORGANISATION_FINANCES);
        Long eachCollaboratorFinanceSectionId;
        if(eachOrganisationFinanceSections.isEmpty()) {
            eachCollaboratorFinanceSectionId = null;
        } else {
            eachCollaboratorFinanceSectionId = eachOrganisationFinanceSections.get(0).getId();
        }

        model.addAttribute("markedAsComplete", markedAsComplete);
        model.addAttribute("completedSectionsByOrganisation", completedSectionsByOrganisation);
        model.addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        model.addAttribute("allQuestionsCompleted", sectionService.allSectionsMarkedAsComplete(application.getId()));
        model.addAttribute("hasFinanceSection", hasFinanceSection);
        model.addAttribute("financeSectionId", financeSectionId);
        model.addAttribute("eachCollaboratorFinanceSectionId", eachCollaboratorFinanceSectionId);
    }

    private List<SectionResource> getSectionsByType(List<SectionResource> list, SectionType type){
        return simpleFilter(list, s -> type.equals(s.getType()));
    }

    private void addSectionDetails(Model model, SectionResource currentSection) {
        List<QuestionResource> questions = getQuestionsBySection(currentSection.getQuestions(), questionService.findByCompetition(currentSection.getCompetition()));
        questions.sort((QuestionResource q1, QuestionResource q2) -> q1.getPriority().compareTo(q2.getPriority()));
        Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
        sectionQuestions.put(currentSection.getId(), questions);
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream().flatMap(a -> a.stream()).collect(Collectors.toMap(q -> q.getId(), k -> formInputService.findApplicationInputsByQuestion(k.getId())));

        model.addAttribute("currentSectionId", currentSection.getId());
        model.addAttribute("currentSection", currentSection);
        model.addAttribute("questionFormInputs", questionFormInputs);
        model.addAttribute("sectionQuestions", sectionQuestions);
        model.addAttribute("title", currentSection.getName());
    }

    private void addApplicationAndSections(ApplicationResource application,
        CompetitionResource competition,
        Long userId,
        SectionResource section,
        Model model,
        ApplicationForm form,
        List<SectionResource> allSections) {

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        addOrganisationDetails(model, application, userApplicationRoles);
        addApplicationDetails(application, competition, userId, section, model, form, userApplicationRoles, allSections);

        addSectionDetails(model, section);
        model.addAttribute("completedQuestionsPercentage", application.getCompletion());
    }

    private void addOrganisationAndUserFinanceDetails(Long competitionId, Long applicationId, UserResource user,
        Model model, ApplicationForm form, List<SectionResource> allSections) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);

        boolean hasFinanceSection = !financeSections.isEmpty();

        if(hasFinanceSection) {
        	
            List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(financeSections.get(0).getId(), QuestionType.COST);
        	
            financeOverviewModelManager.addFinanceDetails(model, competitionId, applicationId);
            if(!form.isAdminMode()){
                String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
                if(competitionResource.isOpen()) {
                    financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, costsQuestions, user.getId(), form);
                }
            }
        }

        model.addAttribute("currentUser", user);
    }

    public SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRoleResource> userApplicationRoles) {
        Comparator<OrganisationResource> compareById =
            Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
            .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                || uar.getRoleName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
            .collect(Collectors.toCollection(supplier));
    }

    public SortedSet<OrganisationResource> getAcademicOrganisations(SortedSet<OrganisationResource> organisations) {
        Comparator<OrganisationResource> compareById =
            Comparator.comparingLong(OrganisationResource::getId);
        Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
            .filter(o -> OrganisationTypeEnum.ACADEMIC.getOrganisationTypeId().equals(o.getOrganisationType()))
            .collect(Collectors.toCollection(supplier));
    }

    public Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
            .findFirst();
    }

    public Optional<OrganisationResource> getUserOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getUser().equals(userId))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
            .findFirst();
    }
}
