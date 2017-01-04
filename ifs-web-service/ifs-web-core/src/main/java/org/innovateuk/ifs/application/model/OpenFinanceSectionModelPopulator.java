package org.innovateuk.ifs.application.model;

import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.view.FinanceOverviewModelManager;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public class OpenFinanceSectionModelPopulator extends BaseSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private QuestionService questionService;

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
    private FinanceOverviewModelManager financeOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Override
    public void populateModel(final ApplicationForm form, final Model model, final ApplicationResource application, final SectionResource section, final UserResource user, final BindingResult bindingResult, final List<SectionResource> allSections){
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        addApplicationAndSections(application, competition, user.getId(), section, model, form, allSections);
        
        List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(section.getId(), QuestionType.COST);

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(user.getId(), userApplicationRoles);
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = completedSectionsByOrganisation.get(userOrganisation.map(OrganisationResource::getId).orElse(null));

        addOrganisationAndUserFinanceDetails(application.getCompetition(), application.getId(), costsQuestions, user, model, form);
        addNavigation(section, application.getId(), model);

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        boolean allReadOnly = !competition.getCompetitionStatus().equals(CompetitionStatus.OPEN)
                || SectionType.FINANCE.equals(section.getType());

        populateSubSectionMenuOptions(model, competition.getId(), allSections, sectionsMarkedAsComplete, userOrganisation.map(OrganisationResource::getId).orElse(null), application.getId());
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("currentSectionId", section.getId());
        model.addAttribute("currentSection", section);
        model.addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        model.addAttribute("hasFinanceSection", true);
        model.addAttribute("financeSectionId", section.getId());
        model.addAttribute("allReadOnly", allReadOnly);
        model.addAttribute("isSubFinanceSection", isSubFinanceSection(section));
        model.addAttribute("form", form);
    }

    private void populateSubSectionMenuOptions(Model model, Long competitionId, final List<SectionResource> allSections, Set<Long> sectionsMarkedAsComplete, Long userOrganisationId, Long applicationId) {
        QuestionResource applicationDetailsQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.APPLICATION_DETAILS).getSuccessObjectOrThrowException();
        Map<Long, QuestionStatusResource>  questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(applicationId, userOrganisationId);
        QuestionStatusResource applicationDetailsStatus = questionStatuses.get(applicationDetailsQuestion.getId());

        boolean organisationSizeComplete = sectionsMarkedAsComplete.contains(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(null));
        boolean applicationDetailsComplete = applicationDetailsStatus != null && applicationDetailsStatus.getMarkedAsComplete();

        model.addAttribute("isFundingSectionLocked", !(organisationSizeComplete && applicationDetailsComplete));
        model.addAttribute("applicationDetailsQuestionId", applicationDetailsQuestion.getId());
        model.addAttribute("yourOrganisationSectionId", allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).findFirst().map(SectionResource::getId).orElse(null));
    }

    private Boolean isSubFinanceSection(SectionResource section) {
        return SectionType.FINANCE.equals(section.getType().getParent().orElse(null));
    }

    private void addApplicationDetails(ApplicationResource application,
        CompetitionResource competition,
        Long userId,
        SectionResource section,
        Model model,
        ApplicationForm form,
        List<ProcessRoleResource> userApplicationRoles,
        List<SectionResource> allSections,
        List<FormInputResource> inputs) {
        Optional<OrganisationResource> userOrganisation = getUserOrganisation(userId, userApplicationRoles);


        if(form == null){
            form = new ApplicationForm();
        }
        form.setApplication(application);

        addUserDetails(model, application, userId);
        addMappedSectionsDetails(model, application, competition, section, userOrganisation, allSections, inputs);

        //Parent finance section has no assignable or question details.
        if (!SectionType.FINANCE.equals(section.getType())) {
            addQuestionsDetails(model, application, form);
            addAssignableDetails(model, application, userOrganisation.orElse(null), userId, section);
        }

        addCompletedDetails(model, application, userOrganisation);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        model.addAttribute("userOrganisation", userOrganisation.orElse(null));
    }

    private void addQuestionsDetails(Model model, ApplicationResource application, Form form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        model.addAttribute("responses",mappedResponses);

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

        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }

    private boolean isApplicationInViewMode(Model model, ApplicationResource application, OrganisationResource userOrganisation) {
        if(!application.isOpen() || userOrganisation == null){
            //Application Not open, so add empty lists
            model.addAttribute("questionAssignees", new HashMap<Long, QuestionStatusResource>());
            model.addAttribute("notifications", new ArrayList<QuestionStatusResource>());
            return true;
        }
        return false;
    }

    private void addMappedSectionsDetails(Model model, ApplicationResource application, CompetitionResource competition,
        SectionResource currentSection,
        Optional<OrganisationResource> userOrganisation,
        List<SectionResource> allSections,
        List<FormInputResource> inputs) {
        List<SectionResource> parentSections = singletonList(currentSection);

        Map<Long, SectionResource> sections =
            parentSections.stream().collect(Collectors.toMap(SectionResource::getId,
                Function.identity()));

        List<QuestionResource> questions = questionService.findByCompetition(competition.getId());

        Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
            .collect(Collectors.toMap(
                SectionResource::getId,
                s -> getQuestionsBySection(s.getQuestions(), questions)
            ));
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream()
            .flatMap(a -> a.stream())
            .collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), inputs)));

        Map<Long, List<QuestionResource>> subsectionQuestions;
        Map<Long, List<SectionResource>>  subSections = new HashMap<>();
        subSections.put(currentSection.getId(), getSectionsFromListByIdList(currentSection.getChildSections(), allSections));

        subsectionQuestions = subSections.get(currentSection.getId()).stream()
            .collect(Collectors.toMap(SectionResource::getId,
                ss -> getQuestionsBySection(ss.getQuestions(), questions)
            ));

        Map<Long, List<FormInputResource>> subSectionQuestionFormInputs = subsectionQuestions.values().stream().flatMap(a -> a.stream()).collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), inputs)));

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
        return simpleFilter(list, input -> input.getQuestion().equals(id));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }


    private void addCompletedDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        model.addAttribute("markedAsComplete", markedAsComplete);
    }

    private void addSectionDetails(Model model, SectionResource currentSection, List<FormInputResource> inputs) {
        List<QuestionResource> questions = getQuestionsBySection(currentSection.getQuestions(), questionService.findByCompetition(currentSection.getCompetition()));
        questions.sort((QuestionResource q1, QuestionResource q2) -> q1.getPriority().compareTo(q2.getPriority()));
        Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
        sectionQuestions.put(currentSection.getId(), questions);
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream().flatMap(a -> a.stream()).collect(Collectors.toMap(q -> q.getId(), k -> findByQuestion(inputs, k.getId())));

        model.addAttribute("questionFormInputs", questionFormInputs);
        model.addAttribute("sectionQuestions", sectionQuestions);
        model.addAttribute("title", currentSection.getName());
    }

    private List<FormInputResource> findByQuestion(List<FormInputResource> inputs, Long questionId){
        return simpleFilter(inputs, i -> questionId.equals(i.getQuestion()));
    }

    private void addApplicationAndSections(ApplicationResource application,
        CompetitionResource competition,
        Long userId,
        SectionResource section,
        Model model,
        ApplicationForm form,
        List<SectionResource> allSections) {
        List<FormInputResource> inputs = formInputService.findApplicationInputsByCompetition(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        addApplicationDetails(application, competition, userId, section, model, form, userApplicationRoles, allSections, inputs);

        addSectionDetails(model, section, inputs);
    }

    private void addOrganisationAndUserFinanceDetails(Long competitionId, Long applicationId, List<QuestionResource> costsQuestions, UserResource user,
        Model model, ApplicationForm form) {

        financeOverviewModelManager.addFinanceDetails(model, competitionId, applicationId);
        String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, costsQuestions, user.getId(), form);

        model.addAttribute("currentUser", user);
    }

    private Optional<OrganisationResource> getUserOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getUser().equals(userId))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
            .findFirst();
    }
}
