package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionStatusResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionAssignableViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

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

    private void addApplicationDetails(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
                                       ApplicationForm form, ApplicantSectionResource applicantSection) {

        form = initializeApplicationForm(form);
        form.setApplication(applicantSection.getApplication());

        //Parent finance section has no assignable or question details.
        if (!SectionType.FINANCE.equals(applicantSection.getSection().getType())) {
            addQuestionsDetails(viewModel, applicantSection, form);
        }
        addUserDetails(viewModel, applicantSection);
        addMappedSectionsDetails(viewModel, applicantSection);

        if (!SectionType.FINANCE.equals(applicantSection.getSection().getType())) {
            viewModel.setSectionAssignableViewModel(addAssignableDetails(applicantSection));
        }
        addCompletedDetails(sectionApplicationViewModel, applicantSection);

        sectionApplicationViewModel.setUserOrganisation(applicantSection.getCurrentApplicant().getOrganisation());
    }

    private SectionAssignableViewModel addAssignableDetails(ApplicantSectionResource applicantSection) {

        if (isApplicationInViewMode(applicantSection.getApplication(), Optional.of(applicantSection.getCurrentApplicant().getOrganisation()))) {
            return new SectionAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees;

        questionAssignees = simpleToMap(applicantSection.allQuestionStatuses().filter(status -> status.getAssignee().isSameUser(applicantSection.getCurrentApplicant())).collect(Collectors.toList()),
            status -> status.getStatus().getQuestion(), ApplicantQuestionStatusResource::getStatus);

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), applicantSection.getCurrentApplicant().getUser().getId());
        questionService.removeNotifications(notifications);

        return new SectionAssignableViewModel(questionAssignees, notifications);
    }

    private void addCompletedDetails(SectionApplicationViewModel sectionApplicationViewModel, ApplicantSectionResource applicantSection) {
        Set<Long> markedAsComplete = applicantSection.allQuestionStatuses()
                .filter(status -> status.getMarkedAsCompleteBy().hasSameOrganisation(applicantSection.getCurrentApplicant()))
                .map(status -> status.getStatus().getQuestion())
                .collect(Collectors.toSet());
        sectionApplicationViewModel.setMarkedAsComplete(markedAsComplete);
    }

    protected void addApplicationAndSections(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicantSectionResource applicantSection,
                                            ApplicationForm form) {
        addSectionsMarkedAsComplete(viewModel, applicantSection);
        addApplicationDetails(viewModel, sectionApplicationViewModel, form, applicantSection);

        addSectionDetails(viewModel, applicantSection);
    }

    protected void addFundingSection(OpenFinanceSectionViewModel viewModel, Long competitionId) {
        viewModel.setFundingSection(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).stream().findFirst().orElse(null));
    }
}