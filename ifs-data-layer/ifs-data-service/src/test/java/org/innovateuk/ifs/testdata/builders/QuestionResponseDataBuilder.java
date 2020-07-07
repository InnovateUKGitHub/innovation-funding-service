package org.innovateuk.ifs.testdata.builders;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.testdata.builders.data.ApplicationQuestionResponseData;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertTrue;

/**
 * Handles applicant responses to Questions
 */
public class QuestionResponseDataBuilder extends BaseDataBuilder<ApplicationQuestionResponseData, QuestionResponseDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionResponseDataBuilder.class);

    private static Cache<Pair<Long, String>, List<FormInputResource>> formInputsByCompetitionIdAndQuestionName = CacheBuilder.newBuilder().build();

    public QuestionResponseDataBuilder withExistingResponse(ApplicationQuestionResponseData response) {
        return with(data -> {
           data.setApplication(response.getApplication());
           data.setQuestionName(response.getQuestionName());
        });
    }

    public QuestionResponseDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public QuestionResponseDataBuilder forQuestion(String questionName) {
        return with(data -> data.setQuestionName(questionName));
    }

    public QuestionResponseDataBuilder withAnswer(String value, String updatedBy) {
        return with(data -> {
            UserResource updateUser = retrieveUserByEmail(updatedBy);
            doAs(updateUser, () -> doAnswerQuestion(data.getQuestionName(), value, updateUser, data));
        });
    }

    public QuestionResponseDataBuilder withChoice(MultipleChoiceOptionResource choice, String updatedBy) {
        return with(data -> {
            UserResource updateUser = retrieveUserByEmail(updatedBy);
            doAs(updateUser, () ->
                testService.doWithinTransaction(() -> {
                    FormInputResource choiceInput = getFormInputsForQuestion(data.getQuestionName(), data).stream()
                            .filter(fi ->  fi.getScope().equals(FormInputScope.APPLICATION) && fi.getType().equals(FormInputType.MULTIPLE_CHOICE))
                            .findFirst()
                            .get();

                    List<FormInputResponse> response = formInputResponseRepository.findByApplicationIdAndFormInputId(data.getApplication().getId(), choiceInput.getId());

                    Optional<FormInputResponse> formInputResponseForUser =
                            simpleFindFirst(response, fir -> updateUser.getId().equals(fir.getUpdatedBy().getUser().getId()));
                    FormInputResponse formInputResponse = formInputResponseForUser.orElseGet(() -> {

                        ProcessRoleResource processRole = retrieveApplicantByEmail(updatedBy, data.getApplication().getId());
                        ProcessRole processRoleEntity = processRoleRepository.findById(processRole.getId()).orElse(null);
                        Application application = applicationRepository.findById(data.getApplication().getId()).orElse(null);
                        FormInput formInputEntity = formInputRepository.findById(choiceInput.getId()).orElse(null);
                        return new FormInputResponse(ZonedDateTime.now(), String.valueOf(choice.getId()), processRoleEntity, formInputEntity, application);
                    });

                    FormInput formInput = formInputResponse.getFormInput();
                    MultipleChoiceOption multipleChoiceOption = formInput.getMultipleChoiceOptions().stream()
                            .filter(option -> option.getId().equals(choice.getId()))
                            .findFirst().orElse(null);

                    formInputResponse.setMultipleChoiceOption(multipleChoiceOption);
                    formInputResponse.setValue(choice.getText());
                    formInputResponse.setUpdateDate(ZonedDateTime.now());
                    formInputResponseRepository.save(formInputResponse);
                })
            );
        });
    }

    public QuestionResponseDataBuilder withFileUploads(List<String> fileUploads, String uploadedBy) {

        return with(data -> {

            UserResource updateUser = retrieveUserByEmail(uploadedBy);

            doAs(updateUser, () -> {

                testService.doWithinTransaction(() -> {

                    List<FormInputResource> formInputs = getFormInputsForQuestion(data.getQuestionName(), data);
                    List<FormInputResource> fileUploadInputs = simpleFilter(formInputs, fi ->  fi.getScope().equals(FormInputScope.APPLICATION) && FormInputType.FILEUPLOAD == fi.getType());

                    zip(fileUploadInputs, fileUploads, (input, filename) -> {

                        FileEntry fileEntry = fileEntryRepository.save(new FileEntry(null, filename, "application/pdf", 7945));

                        List<FormInputResponse> response = formInputResponseRepository.findByApplicationIdAndFormInputId(data.getApplication().getId(), input.getId());

                        Optional<FormInputResponse> formInputResponseForUser =
                                simpleFindFirst(response, fir -> updateUser.getId().equals(fir.getUpdatedBy().getUser().getId()));

                        FormInputResponse formInputResponse = formInputResponseForUser.orElseGet(() -> {

                            ProcessRoleResource processRole = retrieveApplicantByEmail(uploadedBy, data.getApplication().getId());
                            ProcessRole processRoleEntity = processRoleRepository.findById(processRole.getId()).orElse(null);
                            Application application = applicationRepository.findById(data.getApplication().getId()).orElse(null);
                            FormInput formInputEntity = formInputRepository.findById(input.getId()).orElse(null);
                            return new FormInputResponse(ZonedDateTime.now(), singletonList(fileEntry), processRoleEntity, formInputEntity, application);
                        });

                        formInputResponse.setFileEntries(singletonList(fileEntry));
                        formInputResponse.setUpdateDate(ZonedDateTime.now());
                        formInputResponseRepository.save(formInputResponse);
                    });
                });
            });
        });
    }

    public QuestionResponseDataBuilder withAssignee(String assignee) {
        return with(data -> {

            QuestionResource question = retrieveQuestionByCompetitionAndName(data.getQuestionName(), data.getApplication().getCompetition());
            ProcessRoleResource assigneeUser = retrieveApplicantByEmail(assignee, data.getApplication().getId());
            ProcessRoleResource assignedByUser = retrieveLeadApplicant(data.getApplication().getId());
            UserResource assigningUser = retrieveUserById(assignedByUser.getUser());

            doAs(assigningUser, () ->
                    questionStatusService.assign(new QuestionApplicationCompositeId(question.getId(), data.getApplication().getId()),
                        assigneeUser.getId(), assignedByUser.getId())
            );
        });
    }

    public QuestionResponseDataBuilder markAsComplete(boolean updateApplicationCompletionStatus) {
        return with(data -> {
            QuestionResource question = retrieveQuestionByCompetitionAndName(data.getQuestionName(), data.getApplication().getCompetition());
            ProcessRoleResource lead = retrieveLeadApplicant(data.getApplication().getId());
            UserResource leadUser = retrieveUserById(lead.getUser());

            doAs(leadUser, () -> {
                QuestionApplicationCompositeId questionKey = new QuestionApplicationCompositeId(question.getId(), data.getApplication().getId());

                List<ValidationMessages> validationMessages;
                if (updateApplicationCompletionStatus) {
                    validationMessages = questionStatusService.markAsComplete(questionKey, lead.getId()).getSuccess();
                } else {
                    validationMessages = testQuestionService.markAsCompleteWithoutApplicationCompletionStatusUpdate(questionKey, lead.getId()).getSuccess();
                }
                assertTrue("Marking question as complete has returned errors " + toJson(validationMessages),
                        validationMessages.isEmpty());
            });
        });
    }

    private void doAnswerQuestion(String questionName, String value, UserResource user, ApplicationQuestionResponseData data) {

        List<FormInputResource> formInputs = getFormInputsForQuestion(questionName, data);

        Optional<FormInputResource> applicantFormInput = simpleFindFirst(formInputs, fi -> FormInputScope.APPLICATION.equals(fi.getScope()));

        if (!applicantFormInput.isPresent()) {
            throw new IFSRuntimeException(String.format("Missing form input for question %s, app %s", questionName, data.getApplication().getId()));
        }

        FormInputResponseCommand updateRequest = new FormInputResponseCommand(
                applicantFormInput.get().getId(), data.getApplication().getId(), user.getId(), value);

        formInputResponseService.saveQuestionResponse(updateRequest).getSuccess();
    }

    private List<FormInputResource> getFormInputsForQuestion(String questionName, ApplicationQuestionResponseData data) {
        Long competitionId = data.getApplication().getCompetition();

        return fromCache(Pair.of(competitionId, questionName), formInputsByCompetitionIdAndQuestionName, () -> {
            QuestionResource question = retrieveQuestionByCompetitionAndName(questionName, competitionId);
            return formInputService.findByQuestionId(question.getId()).getSuccess();
        });
    }

    public static QuestionResponseDataBuilder newApplicationQuestionResponseData(ServiceLocator serviceLocator) {

        return new QuestionResponseDataBuilder(emptyList(), serviceLocator);
    }

    private QuestionResponseDataBuilder(List<BiConsumer<Integer, ApplicationQuestionResponseData>> multiActions,
                                        ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected QuestionResponseDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationQuestionResponseData>> actions) {
        return new QuestionResponseDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ApplicationQuestionResponseData createInitial() {
        return new ApplicationQuestionResponseData();
    }

    @Override
    protected void postProcess(int index, ApplicationQuestionResponseData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Response to Application '{}', Question '{}'", instance.getApplication().getName(), instance.getQuestionName());
    }
}
