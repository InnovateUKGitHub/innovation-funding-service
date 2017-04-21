package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionAssignableViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public abstract class BaseOpenFinanceSectionModelPopulator extends BaseSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FormInputRestService formInputRestService;

    protected void populateSubSectionMenuOptions(OpenFinanceSectionViewModel viewModel, final List<SectionResource> allSections, Long userOrganisationId, Integer organisationGrantClaimPercentage) {
        QuestionResource applicationDetailsQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(viewModel.getApplication().getCurrentApplication().getCompetition(), FormInputType.APPLICATION_DETAILS).getSuccessObjectOrThrowException();
        Map<Long, QuestionStatusResource> questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(viewModel.getApplication().getCurrentApplication().getId(), userOrganisationId);
        QuestionStatusResource applicationDetailsStatus = questionStatuses.get(applicationDetailsQuestion.getId());

        boolean organisationSizeComplete = false;
        if (viewModel.getSectionsMarkedAsComplete() != null) {
            organisationSizeComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
        }
        boolean applicationDetailsComplete = applicationDetailsStatus != null && applicationDetailsStatus.getMarkedAsComplete();

        viewModel.setFundingSectionLocked(!(organisationSizeComplete && applicationDetailsComplete));
        viewModel.setApplicationDetailsQuestionId(applicationDetailsQuestion.getId());
        viewModel.setYourOrganisationSectionId(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).findFirst().map(SectionResource::getId).orElse(null));


        boolean yourFundingComplete = false;
        if (viewModel.getSectionsMarkedAsComplete() != null) {
            yourFundingComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.FUNDING_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
        }
        viewModel.setNotRequestingFunding(yourFundingComplete && organisationSizeComplete && organisationGrantClaimPercentage != null && organisationGrantClaimPercentage == 0);
    }


    protected Boolean isSubFinanceSection(SectionResource section) {
        return SectionType.FINANCE.equals(section.getType().getParent().orElse(null));
    }

    private void addApplicationDetails(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicationResource application,
                                       CompetitionResource competition, Long userId, SectionResource section,
                                       ApplicationForm form,
                                       List<SectionResource> allSections, List<FormInputResource> inputs,
                                       Optional<OrganisationResource> userOrganisation) {

        form = initializeApplicationForm(form);
        form.setApplication(application);

        //Parent finance section has no assignable or question details.
        if (!SectionType.FINANCE.equals(section.getType())) {
            addQuestionsDetails(viewModel, application, form);
        }
        addUserDetails(viewModel, application, userId);
        if(null != competition) {
            addMappedSectionsDetails(viewModel, application, competition, section, userOrganisation, allSections, inputs, singletonList(section));
        }

        if (!SectionType.FINANCE.equals(section.getType())) {
            viewModel.setSectionAssignableViewModel(addAssignableDetails(application, userOrganisation, userId, section));
        }
        addCompletedDetails(sectionApplicationViewModel, application, userOrganisation);

        sectionApplicationViewModel.setUserOrganisation(userOrganisation.orElse(null));
    }

    protected void addQuestionsDetails(OpenFinanceSectionViewModel viewModel, ApplicationResource application, Form form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);

        viewModel.setResponses(mappedResponses);

        if(form == null){
            form = new Form();
        }
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
            values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
    }

    private SectionAssignableViewModel addAssignableDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation, Long userId, SectionResource currentSection) {

        if (isApplicationInViewMode(application, userOrganisation)) {
            return new SectionAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees;

        questionAssignees = questionService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(currentSection.getQuestions(), application.getId(), getUserOrganisationId(userOrganisation));

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);

        return new SectionAssignableViewModel(questionAssignees, notifications);
    }

    private void addCompletedDetails(SectionApplicationViewModel sectionApplicationViewModel, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        sectionApplicationViewModel.setMarkedAsComplete(markedAsComplete);
    }

    protected void addApplicationAndSections(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
                                           ApplicationResource application,
                                            CompetitionResource competition,
                                            Long userId,
                                            SectionResource section,
                                            ApplicationForm form,
                                            List<SectionResource> allSections, Optional<OrganisationResource> userOrganisation) {
        List<FormInputResource> inputs = formInputRestService.getByCompetitionIdAndScope(application.getCompetition(), APPLICATION).getSuccessObjectOrThrowException();
        addSectionsMarkedAsComplete(viewModel, application, userOrganisation);
        addApplicationDetails(viewModel, sectionApplicationViewModel, application, competition, userId, section, form, allSections, inputs, userOrganisation);

        addSectionDetails(viewModel, section);
    }

    protected void addSectionsMarkedAsComplete(OpenFinanceSectionViewModel viewModel, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = completedSectionsByOrganisation.get(userOrganisation.map(OrganisationResource::getId)
                .orElse(completedSectionsByOrganisation.keySet().stream().findFirst().orElse(-1L)));

        viewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
    }

    protected void addFundingSection(OpenFinanceSectionViewModel viewModel, Long competitionId) {
        viewModel.setFundingSection(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).stream().findFirst().orElse(null));
    }
}