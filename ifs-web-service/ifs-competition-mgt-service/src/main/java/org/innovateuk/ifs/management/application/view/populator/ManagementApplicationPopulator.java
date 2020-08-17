package org.innovateuk.ifs.management.application.view.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.management.application.view.viewmodel.AppendixViewModel;
import org.innovateuk.ifs.management.application.view.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.innovateuk.ifs.management.application.view.viewmodel.ManagementApplicationViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.form.resource.FormInputType.TEMPLATE_DOCUMENT;


@Component
public class ManagementApplicationPopulator {

    @Autowired
    private ApplicationOverviewIneligibilityModelPopulator applicationOverviewIneligibilityModelPopulator;

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationSummaryViewModelPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;


    public ManagementApplicationViewModel populate(long applicationId,
                                                   UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<String> feedback = assessmentRestService.getApplicationFeedback(applicationId).getSuccess().getFeedback();

        ApplicationReadOnlySettings settings = defaultSettings();
        boolean support = user.hasRole(Role.SUPPORT);
        if (support && application.isOpen()) {
            settings.setIncludeStatuses(true);
        }

//        1. loop through the sections within applicationReadOnlyViewModel
//        2. for each question within the section;
//          1. retrieve the question type (questionRestService.findById(questionId).getSuccess();)
//          2. retrieve the feedback for the question via (extracted from AssessorQuestionFeedbackPopulator) AssessmentFeedbackAggregateResource aggregateResource = assessorFormInputResponseRestService
//                .getAssessmentAggregateFeedback(applicationId, questionResource.getId())
//                .getSuccess();
//        3. log out

        List<SectionResource> allByCompetitionId = sectionService.getAllByCompetitionId(competition.getId());

        ApplicationReadOnlyViewModel applicationReadOnlyViewModel = applicationSummaryViewModelPopulator.populate(application, competition, user, settings);
        ApplicationOverviewIneligibilityViewModel ineligibilityViewModel = applicationOverviewIneligibilityModelPopulator.populateModel(application);

        allByCompetitionId.forEach((section) -> {
            section.getQuestions().forEach((questionId) -> {
                QuestionResource questionResource = questionRestService.findById(questionId).getSuccess();
                AssessmentFeedbackAggregateResource aggregateResource = assessorFormInputResponseRestService.getAssessmentAggregateFeedback(applicationId, questionResource.getId())
                .getSuccess();
                System.out.println("loopada question man" + aggregateResource.getFeedback().toString());
            });
        });

        Long projectId = null;
        if (application.getApplicationState() == ApplicationState.APPROVED) {
            projectId = projectRestService.getByApplicationId(applicationId).getOptionalSuccessObject().map(ProjectResource::getId).orElse(null);
        }

        return new ManagementApplicationViewModel(
                application,
                competition,
                ineligibilityViewModel,
                applicationReadOnlyViewModel,
                getAppendices(applicationId),
                canMarkAsIneligible(application, user),
                user.hasAnyRoles(Role.PROJECT_FINANCE, Role.COMP_ADMIN),
                support,
                projectId,
                user.hasRole(Role.EXTERNAL_FINANCE),
                feedback
        );

    }

    private List<AppendixViewModel> getAppendices(Long applicationId) {
        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        return responses.stream().filter(fir -> fir.getFileEntries() != null && !fir.getFileEntries().isEmpty())
                .flatMap(fir -> fir.getFileEntries().stream()
                    .map(file -> {
                        FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccess();
                        String title = fileTitle(formInputResource, file);
                        return new AppendixViewModel(applicationId, formInputResource.getId(), title, file);
                    }))
                .collect(Collectors.toList());
    }

    private static String fileTitle(FormInputResource formInputResource, FileEntryResource fileEntryResource) {
        if (TEMPLATE_DOCUMENT.equals(formInputResource.getType())) {
            return format("Uploaded %s", formInputResource.getDescription());
        } else if (FILEUPLOAD.equals(formInputResource.getType())) {
            return "Appendix";
        }
        return formInputResource.getDescription() != null ? formInputResource.getDescription() : fileEntryResource.getName();
    }

    private boolean canMarkAsIneligible(ApplicationResource application, UserResource user) {
        return application.getApplicationState() == SUBMITTED
                && user.hasAnyRoles(Role.PROJECT_FINANCE, Role.COMP_ADMIN, Role.INNOVATION_LEAD);
    }
}
