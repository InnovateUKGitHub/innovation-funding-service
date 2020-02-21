package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.populator.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Component
public class ApplicationPrintPopulator {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private QuestionRestService questionRestService;
    @Autowired
    private FormInputResponseService formInputResponseService;
    @Autowired
    private FormInputResponseRestService formInputResponseRestService;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private FormInputRestService formInputRestService;
    @Autowired
    private ApplicantRestService applicantRestService;
//    @Autowired
//    private FormInputViewModelGenerator formInputViewModelGenerator;
    @Autowired
    private UserService userService;
    @Autowired
    private FinanceService financeService;
    @Autowired
    private FileEntryRestService fileEntryRestService;
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;
    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    public String print(final Long applicationId,
                        Model model, UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("researchCategoryRequired", researchCategoryRequired(competition.getId()));

        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        Optional<OrganisationResource> userOrganisation = getUserOrganisation(user.getId(), userApplicationRoles);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());

        populateModel(model, application.getId(), userApplicationRoles);
        addQuestionsDetails(model, application, null);
        addUserDetails(model, user, userApplicationRoles);
        addMappedSectionsDetails(model, application, competition, Optional.empty(), userOrganisation, user.getId(), completedSectionsByOrganisation, Optional.empty());
        addFinanceDetails(model, competition, applicationId);

        ApplicationReadOnlyViewModel applicationReadOnlyViewModel = applicationReadOnlyViewModelPopulator.populate(application, competition, user, ApplicationReadOnlySettings.defaultSettings());
        model.addAttribute("model", applicationReadOnlyViewModel);
        return "application/print";
    }

    private void addFinanceSections(Long competitionId, Model model) {
        SectionResource section = sectionService.getFinanceSection(competitionId);

        if (section == null) {
            return;
        }
        model.addAttribute("financeSection", section);
    }

    private void addFinanceDetails(Model model, CompetitionResource competition, Long applicationId) {
        addFinanceSections(competition.getId(), model);
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );
        boolean fundingLevelFirst = competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE);

        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType(competition));
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        model.addAttribute("organisationFinances", organisationFinances);
        model.addAttribute("academicFileEntries", organisationFinanceOverview.getAcademicOrganisationFileEntries());
        model.addAttribute("totalFundingSought", organisationFinanceOverview.getTotalFundingSought());
        model.addAttribute("totalContribution", organisationFinanceOverview.getTotalContribution());
        model.addAttribute("totalOtherFunding", organisationFinanceOverview.getTotalOtherFunding());
        model.addAttribute("fundingLevelFirst", fundingLevelFirst);
        model.addAttribute("fundingSoughtFirst", !fundingLevelFirst);
        model.addAttribute(
                "researchParticipationPercentage",
                applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccess()
        );
        model.addAttribute("isApplicant", true);
        model.addAttribute("isVatRegistered", isVatRegistered(organisationFinances));
    }

    private boolean isVatRegistered(Map<Long, BaseFinanceResource> organisationFinances) {
        Optional<BaseFinanceResource> financeResource = organisationFinances.values()
                .stream()
                .findFirst();

        if (financeResource.isPresent()) {
            return financeResource.get().isVatRegistered();
        }

        return false;
    }

    private void addSubSections(Optional<SectionResource> currentSection, Model model, List<SectionResource> parentSections,
                                List<SectionResource> allSections, List<QuestionResource> questions, List<FormInputResource> formInputResources) {
        Map<Long, List<QuestionResource>> subsectionQuestions;
        if (currentSection.isPresent()) {
            Map<Long, List<SectionResource>>  subSections = new HashMap<>();
            subSections.put(currentSection.get().getId(), getSectionsFromListByIdList(currentSection.get().getChildSections(), allSections));

            model.addAttribute("subSections", subSections);
            subsectionQuestions = subSections.get(currentSection.get().getId()).stream()
                    .collect(Collectors.toMap(SectionResource::getId,
                            ss -> getQuestionsBySection(ss.getQuestions(), questions)
                    ));
            model.addAttribute("subsectionQuestions", subsectionQuestions);
        } else {
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

        Map<Long, List<FormInputResource>> subSectionQuestionFormInputs = subsectionQuestions.values().stream().flatMap(Collection::stream).collect(Collectors.toMap(QuestionResource::getId, k -> findFormInputByQuestion(k.getId(), formInputResources)));
        model.addAttribute("subSectionQuestionFormInputs", subSectionQuestionFormInputs);
    }

    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, final List<SectionResource> allSections) {
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }

    private void addMappedSectionsDetails(Model model, ApplicationResource application, CompetitionResource competition,
                                         Optional<SectionResource> currentSection,
                                         Optional<OrganisationResource> userOrganisation,
                                         Long userId,
                                         Map<Long, Set<Long>> completedSectionsByOrganisation,
                                         Optional<Boolean> markAsCompleteEnabled) {

        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        Map<Long, SectionResource> sections =
                parentSections.stream().collect(CollectionFunctions.toLinkedMap(SectionResource::getId,
                        Function.identity()));

        userOrganisation.ifPresent(org -> {
            Set<Long> completedSectionsForThisOrganisation = completedSectionsByOrganisation.get(userOrganisation);
            model.addAttribute("completedSections", completedSectionsForThisOrganisation);
        });

        List<QuestionResource> questions = questionRestService.findByCompetition(competition.getId()).getSuccess();
        markAsCompleteEnabled.ifPresent(markAsCompleteEnabledBoolean ->
                questions.forEach(questionResource -> questionResource.setMarkAsCompletedEnabled(markAsCompleteEnabledBoolean))
        );

        List<FormInputResource> formInputResources = formInputRestService.getByCompetitionIdAndScope(
                competition.getId(), APPLICATION).getSuccess();

        model.addAttribute("sections", sections);

        Optional<SectionResource> financeSection = allSections.stream()
                .filter(section -> section.getType() == SectionType.FUNDING_FINANCES)
                .findFirst();
        model.addAttribute("fundingFinancesSection", financeSection.orElse(null));

        Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> getQuestionsBySection(s.getQuestions(), questions)
                ));
        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(QuestionResource::getId, k -> findFormInputByQuestion(k.getId(), formInputResources)));
        model.addAttribute("questionFormInputs", questionFormInputs);
        model.addAttribute("sectionQuestions", sectionQuestions);


        //Comp admin user doesn't have user organisation
        long applicantId;
        if (!userOrganisation.isPresent())  {
            ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRole(application.getId());
            applicantId = leadApplicantProcessRole.getUser();
        } else {
            applicantId = userId;
        }
//
//        Map<Long, AbstractFormInputViewModel> formInputViewModels = sectionQuestions.values().stream().flatMap(List::stream)
//                .map(question -> applicantRestService.getQuestion(applicantId, application.getId(), question.getId()))
//                .map(applicationQuestion -> formInputViewModelGenerator.fromQuestion(applicationQuestion, new ApplicationForm()))
//                .flatMap(List::stream)
//                .collect(Collectors.toMap(viewModel -> viewModel.getFormInput().getId(), Function.identity()));
//        model.addAttribute("formInputViewModels", formInputViewModels);
//        formInputViewModels.values().forEach(viewModel -> {
//            viewModel.setClosed(!(competition.isOpen() && application.isOpen()));
//            viewModel.setReadonly(true);
//            viewModel.setSummary(true);
//        });


        addSubSections(currentSection, model, parentSections, allSections, questions, formInputResources);
    }


    private List<FormInputResource> findFormInputByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> input.getQuestion().equals(id));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return questions.stream()
                .filter(q -> questionIds.contains(q.getId()))
                .sorted()
                .collect(toList());
    }

    private void addUserDetails(Model model, UserResource user, List<ProcessRoleResource> userApplicationRoles) {

        ProcessRoleResource leadApplicantProcessRole =
                simpleFindFirst(userApplicationRoles, role -> role.getRoleName().equals(Role.LEADAPPLICANT.getName())).get();

        boolean userIsLeadApplicant = leadApplicantProcessRole.getUser().equals(user.getId());

        UserResource leadApplicant = userIsLeadApplicant ? user : userRestService.retrieveUserById(leadApplicantProcessRole.getUser()).getSuccess();

        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", leadApplicant);
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
        model.addAttribute("form", form);
    }

    private List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccess();
    }

    private Optional<OrganisationResource> getUserOrganisation(Long userId,
                                                       List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
                .filter(uar -> uar.getUser().equals(userId) && uar.getOrganisationId() != null)
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .findFirst();
    }

    private boolean researchCategoryRequired(long competitionId) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY)
                .isSuccess();
    }

    private void populateModel(final Model model, final Long applicationId, final List<ProcessRoleResource> userApplicationRoles) {
        final List<OrganisationResource> organisations = getApplicationOrganisations(applicationId);
        final List<OrganisationResource> academicOrganisations = getAcademicOrganisations(organisations);
        final List<Long> academicOrganisationIds = academicOrganisations.stream().map(ao -> ao.getId()).collect(Collectors.toList());
        model.addAttribute("academicOrganisations", academicOrganisations);
        model.addAttribute("applicationOrganisations", organisations);
        Map<Long, Boolean> applicantOrganisationsAreAcademic = organisations.stream().collect(Collectors.toMap(o -> o.getId(), o -> academicOrganisationIds.contains(o.getId())));
        model.addAttribute("applicantOrganisationIsAcademic", applicantOrganisationsAreAcademic);

        final List<String> activeApplicationOrganisationNames = simpleMap(organisations, OrganisationResource::getName);

        final List<String> pendingOrganisationNames = inviteService.getPendingInvitationsByApplicationId(applicationId).stream()
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .filter(orgName -> StringUtils.hasText(orgName)
                        && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        model.addAttribute("pendingOrganisationNames", pendingOrganisationNames);

        final Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles, organisations);
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );
    }


    private List<OrganisationResource> getAcademicOrganisations(final List<OrganisationResource> organisations) {
        return simpleFilter(organisations, o -> OrganisationTypeEnum.RESEARCH.getId() == o.getOrganisationType());
    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(final List<ProcessRoleResource> userApplicationRoles, List<OrganisationResource> organisations) {

        Optional<ProcessRoleResource> leadApplicantRole =
                simpleFindFirst(userApplicationRoles, uar -> uar.getRoleName().equals(Role.LEADAPPLICANT.getName()));

        return leadApplicantRole.flatMap(role -> simpleFindFirst(organisations, org -> org.getId().equals(role.getOrganisationId())));
    }
}
