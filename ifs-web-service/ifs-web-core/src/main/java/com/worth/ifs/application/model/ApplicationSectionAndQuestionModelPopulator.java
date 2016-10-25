package com.worth.ifs.application.model;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
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
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class ApplicationSectionAndQuestionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    protected FormInputService formInputService;

    @Autowired
    protected FormInputResponseService formInputResponseService;

    @Autowired
    protected QuestionService questionService;

    @Autowired
    protected ProcessRoleService processRoleService;

    @Autowired
    protected InviteRestService inviteRestService;

    @Autowired
    protected SectionService sectionService;

    @Autowired
    protected OrganisationService organisationService;

    public void addMappedSectionsDetails(Model model, ApplicationResource application, CompetitionResource competition,
                                         Optional<SectionResource> currentSection,
                                         Optional<OrganisationResource> userOrganisation) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        Map<Long, SectionResource> sections =
                parentSections.stream().collect(Collectors.toMap(SectionResource::getId,
                        Function.identity()));

        userOrganisation.ifPresent(org -> model.addAttribute("completedSections", sectionService.getCompleted(application.getId(), org.getId())));

        List<QuestionResource> questions = questionService.findByCompetition(competition.getId());

        List<FormInputResource> formInputResources = formInputService.findApplicationInputsByCompetition(competition.getId());

        model.addAttribute("sections", sections);
        Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> getQuestionsBySection(s.getQuestions(), questions)
                ));
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream()
                .flatMap(a -> a.stream())
                .collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), formInputResources)));
        model.addAttribute("questionFormInputs", questionFormInputs);
        model.addAttribute("sectionQuestions", sectionQuestions);

        Map<Long, List<QuestionResource>> subsectionQuestions = new HashMap<>();
        if(currentSection.isPresent()){
            Map<Long, List<SectionResource>>  subSections = new HashMap<>();
            subSections.put(currentSection.get().getId(), getSectionsFromListByIdList(currentSection.get().getChildSections(), allSections));

            model.addAttribute("subSections", subSections);
            subsectionQuestions = subSections.get(currentSection.get().getId()).stream()
                    .collect(Collectors.toMap(SectionResource::getId,
                            ss -> getQuestionsBySection(ss.getQuestions(), questions)
                    ));
            model.addAttribute("subsectionQuestions", subsectionQuestions);
        }else{
            Map<Long, List<SectionResource>>   subSections = parentSections.stream()
                    .collect(Collectors.toMap(
                            SectionResource::getId, s -> getSectionsFromListByIdList(s.getChildSections(), allSections)
                    ));
            model.addAttribute("subSections", subSections);
            subsectionQuestions = parentSections.stream()
                    .collect(Collectors.toMap(SectionResource::getId,
                            ss -> getQuestionsBySection(ss.getQuestions(), questions)
                    ));
            model.addAttribute("subsectionQuestions", subsectionQuestions);
        }

        Map<Long, List<FormInputResource>> subSectionQuestionFormInputs = subsectionQuestions.values().stream().flatMap(a -> a.stream()).collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), formInputResources)));
        model.addAttribute("subSectionQuestionFormInputs", subSectionQuestionFormInputs);
    }

    public void addSectionDetails(Model model, Optional<SectionResource> currentSection) {
        model.addAttribute("currentSectionId", currentSection.map(SectionResource::getId).orElse(null));
        model.addAttribute("currentSection", currentSection.orElse(null));
        if(currentSection.isPresent()) {
            List<QuestionResource> questions = getQuestionsBySection(currentSection.get().getQuestions(), questionService.findByCompetition(currentSection.get().getCompetition()));
            questions.sort((QuestionResource q1, QuestionResource q2) -> q1.getPriority().compareTo(q2.getPriority()));
            Map<Long, List<QuestionResource>> sectionQuestions = new HashMap<>();
            sectionQuestions.put(currentSection.get().getId(), questions);
            Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream().flatMap(a -> a.stream()).collect(Collectors.toMap(q -> q.getId(), k -> formInputService.findApplicationInputsByQuestion(k.getId())));

            model.addAttribute("questionFormInputs", questionFormInputs);
            model.addAttribute("sectionQuestions", sectionQuestions);
            model.addAttribute("title", currentSection.get().getName());
        }
    }
    public void addQuestionsDetails(Model model, ApplicationResource application, Form form) {
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

    public void addAssignableDetails(Model model, ApplicationResource application, OrganisationResource userOrganisation,
                                     Long userId, Optional<SectionResource> currentSection, Optional<Long> currentQuestionId) {

        if (isApplicationInViewMode(model, application, userOrganisation))
            return;

        Map<Long, QuestionStatusResource> questionAssignees;
        if(currentQuestionId.isPresent()){
            QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(currentQuestionId.get(), application.getId(), userOrganisation.getId());
            questionAssignees = new HashMap<>();
            if(questionStatusResource != null) {
                questionAssignees.put(currentQuestionId.get(), questionStatusResource);
            }
        }else if(currentSection.isPresent()){
            SectionResource section = currentSection.get();
            questionAssignees = questionService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(section.getQuestions(), application.getId(), userOrganisation.getId());
        }else{
            questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.getId());
        }

        if(currentQuestionId.isPresent()) {
            QuestionStatusResource questionAssignee = questionAssignees.get(currentQuestionId.get());
            model.addAttribute("questionAssignee", questionAssignee);
        }

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);

        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        model.addAttribute("pendingAssignableUsers", pendingAssignableUsers);
        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }



    public void addCompletedDetails(Model model, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        model.addAttribute("markedAsComplete", markedAsComplete);

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = new TreeSet<>(completedSectionsByOrganisation.get(completedSectionsByOrganisation.keySet().stream().findFirst().get()));
        completedSectionsByOrganisation.forEach((key, values) -> sectionsMarkedAsComplete.retainAll(values));

        model.addAttribute("completedSectionsByOrganisation", completedSectionsByOrganisation);
        model.addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        model.addAttribute("allQuestionsCompleted", sectionService.allSectionsMarkedAsComplete(application.getId()));

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        boolean hasFinanceSection;
        Long financeSectionId;
        if(financeSection == null) {
            hasFinanceSection = false;
            financeSectionId = null;
        } else {
            hasFinanceSection = true;
            financeSectionId = financeSection.getId();
        }

        model.addAttribute("hasFinanceSection", hasFinanceSection);
        model.addAttribute("financeSectionId", financeSectionId);

        List<SectionResource> eachOrganisationFinanceSections = sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.ORGANISATION_FINANCES);
        Long eachCollaboratorFinanceSectionId;
        if(eachOrganisationFinanceSections.isEmpty()) {
            eachCollaboratorFinanceSectionId = null;
        } else {
            eachCollaboratorFinanceSectionId = eachOrganisationFinanceSections.get(0).getId();
        }
        model.addAttribute("eachCollaboratorFinanceSectionId", eachCollaboratorFinanceSectionId);
    }

    public Optional<SectionResource> getSectionByIds(Long competitionId, Optional<Long> sectionId, boolean selectFirstSectionIfNoneCurrentlySelected) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        return getSection(allSections, sectionId, selectFirstSectionIfNoneCurrentlySelected);
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    private Optional<SectionResource> getSection(List<SectionResource> sections, Optional<Long> sectionId, boolean selectFirstSectionIfNoneCurrentlySelected) {

        if (sectionId.isPresent()) {
            Long id = sectionId.get();

            // get the section that we want to show, so we can use this on to show the correct questions.
            return sections.stream().filter(x -> x.getId().equals(id)).findFirst();

        } else if (selectFirstSectionIfNoneCurrentlySelected) {
            return sections.isEmpty() ? Optional.empty() : Optional.ofNullable(sections.get(0));
        }

        return Optional.empty();
    }

    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, final List<SectionResource> allSections) {
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }
    private List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return formInputResponseService.getByApplication(application.getId());
    }

    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Long organisationId=0L;
        if(userOrganisation.isPresent()) {
            organisationId = userOrganisation.get().getId();
        }
        return questionService.getMarkedAsComplete(application.getId(), organisationId);
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

    private List<FormInputResource> findFormInputByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> input.getQuestion().equals(id));
    }


}
