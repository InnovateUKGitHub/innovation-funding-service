package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseCommand;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationQuestionResponseDataBuilder extends BaseDataBuilder<ApplicationQuestionResponseData, ApplicationQuestionResponseDataBuilder> {

    public ApplicationQuestionResponseDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public ApplicationQuestionResponseDataBuilder forQuestion(String questionName) {
        return with(data -> data.setQuestionName(questionName));
    }

    public ApplicationQuestionResponseDataBuilder withAnswer(String value, String updatedBy) {
        return with(data -> {
            UserResource updateUser = retrieveUserByEmail(updatedBy);
            doAs(updateUser, () -> doAnswerQuestion(data.getQuestionName(), value, updateUser, data));
        });
    }

    private void doAnswerQuestion(String questionName, String value, UserResource user, ApplicationQuestionResponseData data) {

        QuestionResource question = retrieveQuestionByCompetitionAndName(questionName, data.getApplication().getCompetition());
        List<FormInputResource> formInputs = formInputService.findByQuestionId(question.getId()).getSuccessObjectOrThrowException();

        FormInputResponseCommand updateRequest = new FormInputResponseCommand(
                formInputs.get(0).getId(), data.getApplication().getId(), user.getId(), value);

        FormInputResponse response = formInputService.saveQuestionResponse(updateRequest).getSuccessObjectOrThrowException();
        formInputResponseRepository.save(response);
    }

    public static ApplicationQuestionResponseDataBuilder newApplicationQuestionResponseData(ServiceLocator serviceLocator) {

        return new ApplicationQuestionResponseDataBuilder(emptyList(), serviceLocator);
    }

    private ApplicationQuestionResponseDataBuilder(List<BiConsumer<Integer, ApplicationQuestionResponseData>> multiActions,
                                                   ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected ApplicationQuestionResponseDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationQuestionResponseData>> actions) {
        return new ApplicationQuestionResponseDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ApplicationQuestionResponseData createInitial() {
        return new ApplicationQuestionResponseData();
    }
}
