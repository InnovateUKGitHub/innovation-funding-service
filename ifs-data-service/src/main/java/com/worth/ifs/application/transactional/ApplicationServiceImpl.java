package com.worth.ifs.application.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationResourceHateoas;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.application.resourceassembler.ApplicationResourceAssembler;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.file.transactional.FileService;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.application.transactional.ApplicationServiceImpl.ServiceFailures.*;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.*;
import static com.worth.ifs.transactional.ServiceFailure.error;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.Either.right;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class ApplicationServiceImpl extends BaseTransactionalService implements ApplicationService {

    public enum ServiceFailures {
        UNABLE_TO_CREATE_FILE, //
        FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE, //
        UNABLE_TO_UPDATE_FILE, //
        UNABLE_TO_DELETE_FILE, //
        UNABLE_TO_FIND_FILE, //
    }

    @Autowired
    private FileService fileService;
    @Autowired
    private FormInputResponseRepository formInputResponseRepository;
    @Autowired
    private FormInputRepository formInputRepository;
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ProcessRoleRepository processRoleRepository;
    @Autowired
    ApplicationStatusRepository applicationStatusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    ApplicationResourceAssembler applicationResourceAssembler;


    private final Log log = LogFactory.getLog(getClass());

    @Override
    public Application createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, Long competitionId, Long userId) {

        User user = userRepository.findOne(userId);

        Application application = new Application();
        application.setName(applicationName);
        LocalDate currentDate = LocalDate.now();
        application.setStartDate(currentDate);

        String name = ApplicationStatusConstants.CREATED.getName();

        List<ApplicationStatus> applicationStatusList = applicationStatusRepository.findByName(name);
        ApplicationStatus applicationStatus = applicationStatusList.get(0);

        application.setApplicationStatus(applicationStatus);
        application.setDurationInMonths(3L);

        List<Role> roles = roleRepository.findByName(UserRoleType.LEADAPPLICANT.getName());
        Role role = roles.get(0);

        Organisation userOrganisation = user.getProcessRoles().get(0).getOrganisation();

        Competition competition = competitionRepository.findOne(competitionId);
        ProcessRole processRole = new ProcessRole(user, application, role, userOrganisation);

        List<ProcessRole> processRoles = new ArrayList<>();
        processRoles.add(processRole);

        application.setProcessRoles(processRoles);
        application.setCompetition(competition);

        applicationRepository.save(application);
        processRoleRepository.save(processRole);

        return application;
    }

    @Override
    public Either<ServiceFailure, Pair<File, FormInputResponseFileEntryResource>> createFormInputResponseFileUpload(FormInputResponseFileEntryResource formInputResponseFile, Supplier<InputStream> inputStreamSupplier) {

        return handlingErrors(UNABLE_TO_CREATE_FILE, () -> {

            long applicationId = formInputResponseFile.getCompoundId().getApplicationId();
            long processRoleId = formInputResponseFile.getCompoundId().getProcessRoleId();
            long formInputId = formInputResponseFile.getCompoundId().getFormInputId();

            FormInputResponse existingResponse = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(applicationId, processRoleId, formInputId);

            if (existingResponse != null && existingResponse.getFileEntry() != null) {
                return errorResponse(FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE);
            } else {

                return fileService.createFile(formInputResponseFile.getFileEntryResource(), inputStreamSupplier).map(successfulFile -> {

                    FileEntry fileEntry = successfulFile.getValue();

                    if (existingResponse != null) {

                        existingResponse.setFileEntry(fileEntry);
                        formInputResponseRepository.save(existingResponse);
                        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(FileEntryResourceAssembler.valueOf(fileEntry), formInputResponseFile.getCompoundId());
                        return successResponse(Pair.of(successfulFile.getKey(), fileEntryResource));

                    } else {

                        return getProcessRole(processRoleId).
                                map(processRole -> getFormInput(formInputId).
                                map(formInput -> getApplication(applicationId).
                                map(application -> {
                                    FormInputResponse newFormInputResponse = new FormInputResponse(LocalDateTime.now(), fileEntry, processRole, formInput, application);
                                    formInputResponseRepository.save(newFormInputResponse);
                                    FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(FileEntryResourceAssembler.valueOf(fileEntry), formInputId, applicationId, processRoleId);
                                    return successResponse(Pair.of(successfulFile.getKey(), fileEntryResource));
                        })));
                    }
                });
            }
        });
    }

    @Override
    public Either<ServiceFailure, Pair<File, FormInputResponseFileEntryResource>> updateFormInputResponseFileUpload(FormInputResponseFileEntryResource formInputResponseFile, Supplier<InputStream> inputStreamSupplier) {

        return handlingErrors(UNABLE_TO_UPDATE_FILE, () -> {

            Either<ServiceFailure, Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> existingFileResult =
                    getFormInputResponseFileUpload(formInputResponseFile.getCompoundId());
            return existingFileResult.map(existingFile -> {

                FormInputResponseFileEntryResource existingFormInputResource = existingFile.getKey();

                FileEntryResource existingFileResource = existingFormInputResource.getFileEntryResource();
                FileEntryResource updatedFileDetails = formInputResponseFile.getFileEntryResource();
                FileEntryResource updatedFileDetailsWithId = new FileEntryResource(existingFileResource.getId(), updatedFileDetails.getName(), updatedFileDetails.getMediaType(), updatedFileDetails.getFilesizeBytes());

                return fileService.updateFile(updatedFileDetailsWithId, inputStreamSupplier).map(updatedFile ->
                    successResponse(Pair.of(updatedFile.getKey(), existingFormInputResource))
                );
            });
        });
    }

    @Override
    public Either<ServiceFailure, FormInputResponse> deleteFormInputResponseFileUpload(FormInputResponseFileEntryId formInputResponseFileId) {

        return handlingErrors(UNABLE_TO_DELETE_FILE, () -> {

            Either<ServiceFailure, Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> existingFileResult =
                    getFormInputResponseFileUpload(formInputResponseFileId);

            return existingFileResult.map(existingFile -> {

                FormInputResponseFileEntryResource formInputFileEntryResource = existingFile.getKey();
                Long fileEntryId = formInputFileEntryResource.getFileEntryResource().getId();

                return fileService.deleteFile(fileEntryId).
                    map(deletedFile -> getFormInputResponse(formInputFileEntryResource.getCompoundId()).
                    map(formInputResponse -> unlinkFileEntryFromFormInputResponse(formInputResponse)).
                    map(unlinkedFileEntryResource -> successResponse(unlinkedFileEntryResource)
                ));
            });
        });
    }

    private Either<ServiceFailure, FormInputResponse> unlinkFileEntryFromFormInputResponse(FormInputResponse formInputResponse) {
        formInputResponse.setFileEntry(null);
        FormInputResponse unlinkedResponse = formInputResponseRepository.save(formInputResponse);
        return right(unlinkedResponse);
    }

    @Override
    public Either<ServiceFailure, Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> getFormInputResponseFileUpload(FormInputResponseFileEntryId fileEntryId) {
        return handlingErrors(UNABLE_TO_FIND_FILE, () -> getFormInputResponse(fileEntryId).
                map(formInputResponse -> fileService.getFileByFileEntryId(formInputResponse.getFileEntry().getId()).
                map(inputStreamSupplier -> successResponse(Pair.of(formInputResponseFileEntryResource(formInputResponse.getFileEntry(), fileEntryId), inputStreamSupplier))
        )));
    }

    private Either<ServiceFailure, FormInput> getFormInput(long formInputId) {
        return getOrFail(() -> formInputRepository.findOne(formInputId), () -> error(FORM_INPUT_NOT_FOUND));
    }

    private Either<ServiceFailure, Application> getApplication(long applicationId) {
        return getOrFail(() -> applicationRepository.findOne(applicationId), () -> error(APPLICATION_NOT_FOUND));
    }

    private FormInputResponseFileEntryResource formInputResponseFileEntryResource(FileEntry fileEntry, FormInputResponseFileEntryId fileEntryId) {
        FileEntryResource fileEntryResource = FileEntryResourceAssembler.valueOf(fileEntry);
        return new FormInputResponseFileEntryResource(fileEntryResource, fileEntryId.getFormInputId(), fileEntryId.getApplicationId(), fileEntryId.getProcessRoleId());
    }

    private Either<ServiceFailure, FormInputResponse> getFormInputResponse(FormInputResponseFileEntryId fileEntry) {
        return getOrFail(() -> formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(fileEntry.getApplicationId(), fileEntry.getProcessRoleId(), fileEntry.getFormInputId()), () -> error(FORM_INPUT_RESPONSE_NOT_FOUND));
    }

    @Override
    public ApplicationResourceHateoas getApplicationByIdHateoas(final Long id) {
        Application application = applicationRepository.findOne(id);
        return applicationResourceAssembler.toResource(application);
    }

    @Override
    public Resources<ApplicationResourceHateoas> findAllHateoas() {
        List<Application> applications = applicationRepository.findAll();
        return applicationResourceAssembler.toEmbeddedList(applications);
    }

    @Override
    public Application getApplicationById(final Long id) {
        return applicationRepository.findOne(id);
    }

    @Override
    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    @Override
    public List<Application> findByUserId(final Long userId) {
        User user = userRepository.findOne(userId);
        List<ProcessRole> roles = processRoleRepository.findByUser(user);
        return simpleMap(roles,role -> role.getApplication());
    }

    @Override
    public ResponseEntity<String> saveApplicationDetails(final Long id,
                                                         ApplicationResource application) {

        Application applicationDb = applicationRepository.findOne(id);
        HttpStatus status;

        if (applicationDb != null) {
            applicationDb.setName(application.getName());
            applicationDb.setDurationInMonths(application.getDurationInMonths());
            applicationDb.setStartDate(application.getStartDate());
            applicationRepository.save(applicationDb);

            status = HttpStatus.OK;

        } else {
            log.error("NOT_FOUND " + id);
            status = HttpStatus.NOT_FOUND;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(headers, status);
    }


    @Override
    public ObjectNode getProgressPercentageByApplicationId(final Long applicationId) {
        Application application = applicationRepository.findOne(applicationId);
        List<Section> sections = application.getCompetition().getSections();

        List<Question> questions = sections.stream()
                .flatMap(section -> section.getQuestions().stream())
                .filter(Question::isMarkAsCompletedEnabled)
                .collect(Collectors.toList());

        List<ProcessRole> processRoles = application.getProcessRoles();
        Set<Organisation> organisations = processRoles.stream().map(ProcessRole::getOrganisation).collect(Collectors.toSet());

        Long countMultipleStatusQuestionsCompleted = organisations.stream()
                .mapToLong(org -> questions.stream()
                        .filter(q -> q.hasMultipleStatuses() && questionService.isMarkedAsComplete(q, applicationId, org.getId())).count())
                .sum();
        Long countSingleStatusQuestionsCompleted = questions.stream()
                .filter(q -> !q.hasMultipleStatuses() && questionService.isMarkedAsComplete(q, applicationId, 0L)).count();
        Long countCompleted = countMultipleStatusQuestionsCompleted + countSingleStatusQuestionsCompleted;

        Long totalMultipleStatusQuestions = questions.stream().filter(Question::hasMultipleStatuses).count() * organisations.size();
        Long totalSingleStatusQuestions = questions.stream().filter(q -> !q.hasMultipleStatuses()).count();

        Long totalQuestions = totalMultipleStatusQuestions + totalSingleStatusQuestions;
        log.info("Total questions" + totalQuestions);
        log.info("Total completed questions" + countCompleted);

        double percentageCompleted;
        if (questions.isEmpty()) {
            percentageCompleted = 0;
        } else {
            percentageCompleted = (100.0 / totalQuestions) * countCompleted;
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("completedPercentage", percentageCompleted);
        return node;
    }

    @Override
    public ResponseEntity<String> updateApplicationStatus(final Long id,
                                                          final Long statusId) {

        Application application = applicationRepository.findOne(id);
        ApplicationStatus applicationStatus = applicationStatusRepository.findOne(statusId);
        application.setApplicationStatus(applicationStatus);
        applicationRepository.save(application);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(headers, status);

    }


    @Override
    public List<Application> getApplicationsByCompetitionIdAndUserId(final Long competitionId,
                                                                             final Long userId,
                                                                             final UserRoleType role) {

        List<Application> allApps = applicationRepository.findAll();
        return allApps.stream()
                .filter(app -> app.getCompetition().getId().equals(competitionId) && applicationContainsUserRole(app.getProcessRoles(), userId, role))
                .collect(Collectors.toList());
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
    public Application createApplicationByApplicationNameForUserIdAndCompetitionId(
            final Long competitionId,
            final Long userId,
            JsonNode jsonObj) {

        User user = userRepository.findOne(userId);

        String applicationName = jsonObj.get("name").textValue();
        Application application = new Application();
        application.setName(applicationName);
        LocalDate currentDate = LocalDate.now();
        application.setStartDate(currentDate);

        String name = ApplicationStatusConstants.CREATED.getName();

        List<ApplicationStatus> applicationStatusList = applicationStatusRepository.findByName(name);
        ApplicationStatus applicationStatus = applicationStatusList.get(0);

        application.setApplicationStatus(applicationStatus);
        application.setDurationInMonths(3L);

        List<Role> roles = roleRepository.findByName("leadapplicant");
        Role role = roles.get(0);

        Organisation userOrganisation = user.getOrganisations().get(0);

        Competition competition = competitionRepository.findOne(competitionId);
        ProcessRole processRole = new ProcessRole(user, application, role, userOrganisation);

        List<ProcessRole> processRoles = new ArrayList<>();
        processRoles.add(processRole);

        application.setProcessRoles(processRoles);
        application.setCompetition(competition);

        applicationRepository.save(application);
        processRoleRepository.save(processRole);

        return application;
    }

}
