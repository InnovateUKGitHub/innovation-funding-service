package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResourceAssembler;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.MathFunctions.percentage;
import static org.innovateuk.ifs.util.StringFunctions.plainTextToHtml;
import static org.innovateuk.ifs.util.StringFunctions.stripHtml;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class ApplicationServiceImpl extends BaseTransactionalService implements ApplicationService {
    enum Notifications {
        APPLICATION_SUBMITTED,
        APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
        APPLICATION_INELIGIBLE
    }

    @Autowired
    private FileService fileService;
    @Autowired
    private FormInputResponseRepository formInputResponseRepository;
    @Autowired
    private FormInputRepository formInputRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SystemNotificationSource systemNotificationSource;
    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private NotificationSender notificationSender;
    @Autowired
    private ApplicationWorkflowHandler applicationWorkflowHandler;
    @Autowired
    private ActivityStateRepository activityStateRepository;


    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, Long competitionId, Long userId) {
        return find(user(userId), competition(competitionId)).andOnSuccess((user, competition) -> createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, user, competition));
    }

    private void generateProcessRolesForApplication(User user, Role role, Application application) {
        List<ProcessRole> usersProcessRoles = processRoleRepository.findByUser(user);
        List<Organisation> usersOrganisations = organisationRepository.findByUsers(user);
        Long userOrganisationId = usersProcessRoles.size() != 0
                ? usersProcessRoles.get(0).getOrganisationId()
                : usersOrganisations.get(0).getId();
        ProcessRole processRole = new ProcessRole(user, application.getId(), role, userOrganisationId);
        processRoleRepository.save(processRole);
        List<ProcessRole> processRoles = new ArrayList<>();
        processRoles.add(processRole);
        application.setProcessRoles(processRoles);
        applicationRepository.save(application);
    }

    private ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, User user, Competition competition) {
        ActivityState createdActivityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.CREATED);

        Application application = new Application(applicationName, createdActivityState);
        application.setStartDate(null);

        application.setDurationInMonths(3L);
        application.setCompetition(competition);
        setInnovationArea(application, competition);

        return getRole(LEADAPPLICANT).andOnSuccess(role -> {
            Application savedApplication = applicationRepository.save(application);
            generateProcessRolesForApplication(user, role, savedApplication);
            savedApplication = applicationRepository.findOne(savedApplication.getId());
            return serviceSuccess(applicationMapper.mapToResource(savedApplication));
        });
    }

    // Default to the competition's innovation area if only one set.
    private void setInnovationArea(Application application, Competition competition) {
        if (competition.getInnovationAreas().size() == 1) {
            application.setInnovationArea(competition.getInnovationAreas().stream().findFirst().orElse(null));
            application.setNoInnovationAreaApplicable(false);
        }
    }

    @Override
    @Transactional
    public ServiceResult<FormInputResponseFileEntryResource> createFormInputResponseFileUpload(FormInputResponseFileEntryResource formInputResponseFile, Supplier<InputStream> inputStreamSupplier) {

        long applicationId = formInputResponseFile.getCompoundId().getApplicationId();
        long processRoleId = formInputResponseFile.getCompoundId().getProcessRoleId();
        long formInputId = formInputResponseFile.getCompoundId().getFormInputId();

        return getOpenApplication(applicationId).andOnSuccess(application -> {
            FormInputResponse existingResponse = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(applicationId, processRoleId, formInputId);

            // Removing and replacing if file already exists here
            if (existingResponse != null && existingResponse.getFileEntry() != null) {
                FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
                final ServiceResult<FormInputResponse> deleteResult = deleteFormInputResponseFileUpload(formInputResponseFileEntryId);

                if (deleteResult.isFailure()) {
                    return serviceFailure(new Error(FILES_UNABLE_TO_DELETE_FILE, existingResponse.getFileEntry().getId()));
                }
            }

            return fileService.createFile(formInputResponseFile.getFileEntryResource(), inputStreamSupplier).andOnSuccess(successfulFile ->
                    createFormInputResponseFileUpload(successfulFile, existingResponse, processRoleId, applicationId, formInputId, formInputResponseFile)
            );
        });
    }

    private ServiceResult<FormInputResponseFileEntryResource> createFormInputResponseFileUpload(Pair<File, FileEntry> successfulFile, FormInputResponse existingResponse, long processRoleId, long applicationId, long formInputId, FormInputResponseFileEntryResource formInputResponseFile) {
        FileEntry fileEntry = successfulFile.getValue();

        if (existingResponse != null) {

            existingResponse.setFileEntry(fileEntry);
            formInputResponseRepository.save(existingResponse);
            FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(FileEntryResourceAssembler.valueOf(fileEntry), formInputResponseFile.getCompoundId());
            return serviceSuccess(fileEntryResource);

        }

        return find(processRole(processRoleId), () -> getFormInput(formInputId), application(applicationId)).andOnSuccess((processRole, formInput, application) -> {

            FormInputResponse newFormInputResponse = new FormInputResponse(ZonedDateTime.now(), fileEntry, processRole, formInput, application);
            formInputResponseRepository.save(newFormInputResponse);
            FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(FileEntryResourceAssembler.valueOf(fileEntry), formInputId, applicationId, processRoleId);
            return serviceSuccess(fileEntryResource);
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateFormInputResponseFileUpload(FormInputResponseFileEntryResource formInputResponseFile, Supplier<InputStream> inputStreamSupplier) {

        ServiceResult<FormInputResponseFileAndContents> existingFileResult =
                getFormInputResponseFileUpload(formInputResponseFile.getCompoundId());

        return existingFileResult.andOnSuccess(existingFile -> {

            FormInputResponseFileEntryResource existingFormInputResource = existingFile.getFormInputResponseFileEntry();

            FileEntryResource existingFileResource = existingFormInputResource.getFileEntryResource();
            FileEntryResource updatedFileDetails = formInputResponseFile.getFileEntryResource();
            FileEntryResource updatedFileDetailsWithId = new FileEntryResource(existingFileResource.getId(), updatedFileDetails.getName(), updatedFileDetails.getMediaType(), updatedFileDetails.getFilesizeBytes());

            return fileService.updateFile(updatedFileDetailsWithId, inputStreamSupplier).andOnSuccessReturnVoid();
        });
    }

    @Override
    @Transactional
    public ServiceResult<FormInputResponse> deleteFormInputResponseFileUpload(FormInputResponseFileEntryId fileEntry) {
        return getOpenApplication(fileEntry.getApplicationId()).andOnSuccess(application -> deleteFormInputResponseFileUploadonGetApplicationAndSuccess(fileEntry));
    }

    private ServiceResult<FormInputResponse> deleteFormInputResponseFileUploadonGetApplicationAndSuccess(FormInputResponseFileEntryId fileEntry) {
        return find(formInputRepository.findOne(fileEntry.getFormInputId()), notFoundError(FormInput.class, fileEntry.getFormInputId())).andOnSuccess(
                formInput -> getFormInputResponseFileEntryResource(fileEntry, formInput).
                        andOnSuccess(formInputResponseFileEntryResource -> {
                            boolean questionHasMultipleStatuses = questionHasMultipleStatuses(formInput);
                            return fileService.deleteFileIgnoreNotFound(formInputResponseFileEntryResource.getFileEntryResource().getId()).
                                    andOnSuccess(deletedFile -> {
                                        if (questionHasMultipleStatuses) {
                                            return getFormInputResponse(formInputResponseFileEntryResource.getCompoundId());
                                        } else {
                                            return getFormInputResponseForQuestionAssignee(formInputResponseFileEntryResource.getCompoundId());
                                        }
                                    }).andOnSuccess(this::unlinkFileEntryFromFormInputResponse);
                        })
        );
    }

    private boolean questionHasMultipleStatuses(@NotNull FormInput formInput) {
        Question question = formInput.getQuestion();
        return question.hasMultipleStatuses();
    }

    @Override
    public ServiceResult<FormInputResponseFileAndContents> getFormInputResponseFileUpload(FormInputResponseFileEntryId fileEntry) {
        return find(formInputRepository.findOne(fileEntry.getFormInputId()), notFoundError(FormInput.class, fileEntry.getFormInputId())).
                andOnSuccess(formInput -> getAppropriateFormInputResponse(fileEntry, formInput).
                        andOnSuccess(formInputResponse ->
                                fileService.getFileByFileEntryId(formInputResponse.getFileEntry().getId()).
                                        andOnSuccessReturn(inputStreamSupplier -> {
                                            FormInputResponseFileEntryResource formInputResponseFileEntry = formInputResponseFileEntryResource(formInputResponse.getFileEntry(), fileEntry);
                                            return new FormInputResponseFileAndContents(formInputResponseFileEntry, inputStreamSupplier);
                                        })
                        ));
    }

    private ServiceResult<FormInputResponseFileEntryResource> getFormInputResponseFileEntryResource(FormInputResponseFileEntryId fileEntry, FormInput formInput){
        return getAppropriateFormInputResponse(fileEntry, formInput).andOnSuccess(formInputResponse -> serviceSuccess(formInputResponseFileEntryResource(formInputResponse.getFileEntry(), fileEntry)));
    }

    private ServiceResult<FormInputResponse> getAppropriateFormInputResponse(FormInputResponseFileEntryId fileEntry, FormInput formInput){
        boolean hasMultipleStatuses = questionHasMultipleStatuses(formInput);

        if (hasMultipleStatuses) {
            return getFormInputResponse(fileEntry);
        } else {
            return getFormInputResponseForQuestionAssignee(fileEntry);
        }
    }

    private ServiceResult<FormInputResponse> unlinkFileEntryFromFormInputResponse(FormInputResponse formInputResponse) {
        formInputResponse.setFileEntry(null);
        FormInputResponse unlinkedResponse = formInputResponseRepository.save(formInputResponse);
        formInputResponseRepository.delete(formInputResponse);
        return serviceSuccess(unlinkedResponse);
    }

    private ServiceResult<FormInput> getFormInput(long formInputId) {
        return find(formInputRepository.findOne(formInputId), notFoundError(FormInput.class, formInputId));
    }

    private FormInputResponseFileEntryResource formInputResponseFileEntryResource(FileEntry fileEntry, FormInputResponseFileEntryId fileEntryId) {
        FileEntryResource fileEntryResource = FileEntryResourceAssembler.valueOf(fileEntry);
        return new FormInputResponseFileEntryResource(fileEntryResource, fileEntryId.getFormInputId(), fileEntryId.getApplicationId(), fileEntryId.getProcessRoleId());
    }

    private ServiceResult<FormInputResponse> getFormInputResponse(FormInputResponseFileEntryId fileEntry) {
        Error formInputResponseNotFoundError = notFoundError(FormInputResponse.class, fileEntry.getApplicationId(), fileEntry.getProcessRoleId(), fileEntry.getFormInputId());
        return find(formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(
                fileEntry.getApplicationId(),
                fileEntry.getProcessRoleId(),
                fileEntry.getFormInputId()),
                formInputResponseNotFoundError);
    }

    /**
     * Use this method for finding a form input response when a question has single status (shared across application)
     *
     * @param fileEntry - in this case the FormInputResponseFileEntryId will contain the id of person to whom the question is assigned.
     */
    private ServiceResult<FormInputResponse> getFormInputResponseForQuestionAssignee(FormInputResponseFileEntryId fileEntry) {
        Error formInputResponseNotFoundError = notFoundError(FormInputResponse.class, fileEntry.getApplicationId(), fileEntry.getProcessRoleId(), fileEntry.getFormInputId());
        List<FormInputResponse> formInputResponses = formInputResponseRepository.findByApplicationIdAndFormInputId(fileEntry.getApplicationId(), fileEntry.getFormInputId());
        if (formInputResponses != null && !formInputResponses.isEmpty()) {
            return serviceSuccess(formInputResponses.get(0));
        }
        return serviceFailure(formInputResponseNotFoundError);
    }

    @Override
    public ServiceResult<ApplicationResource> getApplicationById(final Long id) {
        return getApplication(id).andOnSuccessReturn(applicationMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ApplicationResource>> findAll() {
        return serviceSuccess(applicationsToResources(applicationRepository.findAll()));
    }

    @Override
    public ServiceResult<List<ApplicationResource>> findByUserId(final Long userId) {
        return getUser(userId).andOnSuccessReturn(user -> {
            List<ProcessRole> roles = processRoleRepository.findByUser(user);
            List<Application> applications = simpleMap(roles, processRole -> {
                Long appId = processRole.getApplicationId();
                return appId != null ? applicationRepository.findOne(appId) : null;
            });
            return applicationsToResources(applications);
        });
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> saveApplicationDetails(final Long id, ApplicationResource application) {
        if (!applicationBelongsToOpenCompetition(id)) {
            return serviceFailure(COMPETITION_NOT_OPEN);
        }

        return getApplication(id).andOnSuccessReturn(existingApplication -> {

            existingApplication.setName(application.getName());
            existingApplication.setDurationInMonths(application.getDurationInMonths());
            existingApplication.setStartDate(application.getStartDate());
            existingApplication.setStateAidAgreed(application.getStateAidAgreed());
            existingApplication.setResubmission(application.getResubmission());
            existingApplication.setPreviousApplicationNumber(application.getPreviousApplicationNumber());
            existingApplication.setPreviousApplicationTitle(application.getPreviousApplicationTitle());

            Application savedApplication = applicationRepository.save(existingApplication);
            return applicationMapper.mapToResource(savedApplication);
        });
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> saveApplicationSubmitDateTime(final Long id, ZonedDateTime date) {
        return getOpenApplication(id).andOnSuccessReturn(existingApplication -> {
            existingApplication.setSubmittedDate(date);
            Application savedApplication = applicationRepository.save(existingApplication);
            return applicationMapper.mapToResource(savedApplication);
        });
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> setApplicationFundingEmailDateTime(final Long applicationId, final ZonedDateTime fundingEmailDateTime) {
        return getApplication(applicationId).andOnSuccessReturn(application -> {
            application.setManageFundingEmailDate(fundingEmailDateTime);
            Application savedApplication = applicationRepository.save(application);
            return applicationMapper.mapToResource(savedApplication);
        });
    }

    @Override
    public ServiceResult<CompletedPercentageResource> getProgressPercentageByApplicationId(final Long applicationId) {
        return getApplicationById(applicationId).andOnSuccessReturn(applicationResource -> {
            CompletedPercentageResource resource = new CompletedPercentageResource();
            resource.setCompletedPercentage(applicationResource.getCompletion());
            return resource;
        });
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> updateApplicationState(final Long id, final ApplicationState state) {
        if (Collections.singletonList(ApplicationState.SUBMITTED).contains(state) && !applicationReadyToSubmit(id)) {
                return serviceFailure(CommonFailureKeys.GENERAL_FORBIDDEN);
        }
        return find(application(id)).andOnSuccess((application) -> {
            applicationWorkflowHandler.notifyFromApplicationState(application, state);
            applicationRepository.save(application);
            return serviceSuccess(applicationMapper.mapToResource(application));
        });
    }

    @Override
    public ServiceResult<Void> sendNotificationApplicationSubmitted(Long applicationId) {
        return getApplication(applicationId).andOnSuccess(application -> {
            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new ExternalUserNotificationTarget(application.getLeadApplicant().getName(), application.getLeadApplicant().getEmail());

            Map<String, Object> notificationArguments = new HashMap<>();
            Competition competition = application.getCompetition();

            notificationArguments.put("applicationName", application.getName());
            notificationArguments.put("competitionName", competition.getName());
            notificationArguments.put("webBaseUrl", webBaseUrl);

            Notification notification = new Notification(from, singletonList(to), Notifications.APPLICATION_SUBMITTED, notificationArguments);
            return notificationService.sendNotification(notification, EMAIL);
        });
    }

    @Override
    public ServiceResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(final Long competitionId,
                                                                                            final Long userId,
                                                                                            final UserRoleType role) {
        List<Application> allApps = applicationRepository.findAll();
        List<Application> filtered = simpleFilter(allApps, app -> app.getCompetition().getId().equals(competitionId) &&
                applicationContainsUserRole(app.getProcessRoles(), userId, role));
        List<ApplicationResource> filteredResource = applicationsToResources(filtered);
        return serviceSuccess(filteredResource);
    }

    private static boolean applicationContainsUserRole(List<ProcessRole> roles, final Long userId, UserRoleType role) {
        boolean contains = false;
        int i = 0;
        while (!contains && i < roles.size()) {
            contains = roles.get(i).getUser().getId().equals(userId) && roles.get(i).getRole().getName().equals(role.getName());
            i++;
        }

        return contains;
    }

    @Override
    public ServiceResult<ApplicationResource> findByProcessRole(final Long id) {
        return getProcessRole(id).andOnSuccessReturn(processRole -> {
            Long appId = processRole.getApplicationId();
            Application application = applicationRepository.findOne(appId);
            return applicationMapper.mapToResource(application);
        });
    }

    @Override
    public ServiceResult<Boolean>applicationReadyForSubmit(final Long id) {
        return serviceSuccess(applicationReadyToSubmit(id));
    }

    private boolean applicationReadyToSubmit(Long id) {
        return find(application(id), () -> getProgressPercentageBigDecimalByApplicationId(id)).andOnSuccess((application, progressPercentage) ->
                sectionService.childSectionsAreCompleteForAllOrganisations(null, id, null).andOnSuccessReturn(allSectionsComplete -> {
                    Competition competition = application.getCompetition();
                    BigDecimal researchParticipation = applicationFinanceHandler.getResearchParticipationPercentage(id);

                    boolean readyForSubmit = false;
                    if (allSectionsComplete &&
                            progressPercentage.compareTo(BigDecimal.valueOf(100)) == 0 &&
                            researchParticipation.compareTo(BigDecimal.valueOf(competition.getMaxResearchRatio())) <= 0) {
                        readyForSubmit = true;
                    }
                    return readyForSubmit;
                })
        ).getSuccessObject();
    }

    @Override
    public ServiceResult<List<Application>> getApplicationsByCompetitionIdAndState(Long competitionId, Collection<ApplicationState> applicationStates) {
        Collection<State> states = applicationStates.stream().map(ApplicationState::getBackingState).collect(Collectors.toList());
        List<Application> applicationResults = applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, states);
        return serviceSuccess(applicationResults);
    }

    @Override
    public ServiceResult<BigDecimal> getProgressPercentageBigDecimalByApplicationId(final Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(this::progressPercentageForApplication);
    }

    @Override
    public ServiceResult<Void> notifyApplicantsByCompetition(Long competitionId) {
        List<ProcessRole> applicants = applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId,
                ApplicationSummaryServiceImpl.FUNDING_DECISIONS_MADE_STATUSES)
                .stream()
                .flatMap(x -> x.getProcessRoles().stream())
                .filter(ProcessRole::isLeadApplicantOrCollaborator)
                .collect(toList());

        return processAnyFailuresOrSucceed(applicants
                .stream()
                .map(this::sendNotification)
                .collect(toList()));
    }

    @Override
    @Transactional
    public ServiceResult<Void> markAsIneligible(long applicationId, IneligibleOutcome reason) {
        return find(application(applicationId)).andOnSuccess((application) -> {
            if (!applicationWorkflowHandler.markIneligible(application, reason)) {
                return serviceFailure(APPLICATION_MUST_BE_SUBMITTED);
            }
            applicationRepository.save(application);
            return serviceSuccess();
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> informIneligible(long applicationId, ApplicationIneligibleSendResource applicationIneligibleSendResource) {
        return getApplication(applicationId).andOnSuccess(application -> {

            if (!applicationWorkflowHandler.informIneligible(application)) {
                return serviceFailure(APPLICATION_MUST_BE_INELIGIBLE);
            }

            applicationRepository.save(application);
            String bodyPlain = stripHtml(applicationIneligibleSendResource.getContent());
            String bodyHtml = plainTextToHtml(bodyPlain);

            NotificationTarget recipient =
                    new ExternalUserNotificationTarget(application.getLeadApplicant().getName(), application.getLeadApplicant().getEmail());
            Notification notification = new Notification(
                    systemNotificationSource,
                    singletonList(recipient),
                    Notifications.APPLICATION_INELIGIBLE,
                    asMap("subject", applicationIneligibleSendResource.getSubject(),
                            "bodyPlain", bodyPlain,
                            "bodyHtml", bodyHtml));
            return notificationSender.sendNotification(notification);
        }).andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Boolean> showApplicationTeam(Long applicationId, Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId)).andOnSuccess((user) -> serviceSuccess(org.innovateuk.ifs.security.SecurityRuleUtil.isInternal(user)));
    }

    private ServiceResult<List<EmailAddress>> sendNotification(ProcessRole processRole) {
        Application application = applicationRepository.findOne(processRole.getApplicationId());

        NotificationTarget recipient =
                new ExternalUserNotificationTarget(processRole.getUser().getName(), processRole.getUser().getEmail());

        Notification notification = new Notification(
                systemNotificationSource,
                singletonList(recipient),
                Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                asMap("name", processRole.getUser().getName(),
                        "applicationName", application.getName(),
                        "competitionName", application.getCompetition().getName(),
                        "dashboardUrl", webBaseUrl + "/" + processRole.getRole().getUrl()));

        EmailContent content = notificationSender.renderTemplates(notification).getSuccessObject().get(recipient);

        return notificationSender.sendEmailWithContent(notification, recipient, content);
    }

    private BigDecimal progressPercentageForApplication(Application application) {
        List<Section> sections = application.getCompetition().getSections();

        List<Question> questions = sections.stream()
                .flatMap(section -> section.getQuestions().stream())
                .filter(Question::isMarkAsCompletedEnabled)
                .collect(toList());

        List<ProcessRole> processRoles = application.getProcessRoles();

        Set<Organisation> organisations = processRoles.stream()
                .filter(p -> p.getRole().getName().equals(LEADAPPLICANT.getName())
                        || p.getRole().getName().equals(UserRoleType.APPLICANT.getName())
                        || p.getRole().getName().equals(UserRoleType.COLLABORATOR.getName()))
                .map(processRole -> organisationRepository.findOne(processRole.getOrganisationId())).collect(Collectors.toSet());

        Long countMultipleStatusQuestionsCompleted = organisations.stream()
                .mapToLong(org -> questions.stream()
                        .filter(Question::getMarkAsCompletedEnabled)
                        .filter(q -> q.hasMultipleStatuses() && questionService.isMarkedAsComplete(q, application.getId(), org.getId()).getSuccessObject()).count())
                .sum();

        Long countSingleStatusQuestionsCompleted = questions.stream()
                .filter(Question::getMarkAsCompletedEnabled)
                .filter(q -> !q.hasMultipleStatuses() && questionService.isMarkedAsComplete(q, application.getId(), 0L).getSuccessObject()).count();

        Long countCompleted = countMultipleStatusQuestionsCompleted + countSingleStatusQuestionsCompleted;

        Long totalMultipleStatusQuestions = questions.stream().filter(Question::hasMultipleStatuses).count() * organisations.size();
        Long totalSingleStatusQuestions = questions.stream().filter(q -> !q.hasMultipleStatuses()).count();

        Long totalQuestions = totalMultipleStatusQuestions + totalSingleStatusQuestions;

        return percentage(countCompleted, totalQuestions);
    }

    private List<ApplicationResource> applicationsToResources(List<Application> filtered) {
        return simpleMap(filtered, application -> applicationMapper.mapToResource(application));
    }

    private boolean applicationBelongsToOpenCompetition(Long applicationId) {
        Application application = applicationRepository.findOne(applicationId);
        return !(application != null && application.getCompetition() != null) || CompetitionStatus.OPEN.equals(application.getCompetition().getCompetitionStatus());
    }
}
