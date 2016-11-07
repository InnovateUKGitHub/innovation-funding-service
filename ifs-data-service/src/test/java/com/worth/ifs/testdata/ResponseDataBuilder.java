package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionApplicationCompositeId;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseCommand;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ResponseDataBuilder extends BaseDataBuilder<ApplicationQuestionResponseData, ResponseDataBuilder> {

    public ResponseDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public ResponseDataBuilder forQuestion(String questionName) {
        return with(data -> data.setQuestionName(questionName));
    }

    public ResponseDataBuilder withAnswer(String value, String updatedBy) {
        return with(data -> {
            UserResource updateUser = retrieveUserByEmail(updatedBy);
            doAs(updateUser, () -> doAnswerQuestion(data.getQuestionName(), value, updateUser, data));
        });
    }

    public ResponseDataBuilder withAssignee(String assignee) {
        return with(data -> {

            QuestionResource question = retrieveQuestionByCompetitionAndName(data.getQuestionName(), data.getApplication().getCompetition());
            ProcessRoleResource assigneeUser = retrieveApplicantByEmail(assignee, data.getApplication().getId());
            ProcessRoleResource assignedByUser = retrieveLeadApplicant(data.getApplication().getId());
            UserResource assigningUser = retrieveUserById(assignedByUser.getUser());

            doAs(assigningUser, () ->
                questionService.assign(new QuestionApplicationCompositeId(question.getId(), data.getApplication().getId()),
                        assigneeUser.getId(), assignedByUser.getId())
            );
        });
    }

    public ResponseDataBuilder markAsComplete() {
        return with(data -> {
            QuestionResource question = retrieveQuestionByCompetitionAndName(data.getQuestionName(), data.getApplication().getCompetition());
            ProcessRoleResource lead = retrieveLeadApplicant(data.getApplication().getId());
            UserResource leadUser = retrieveUserById(lead.getUser());

            doAs(leadUser, () ->
                questionService.markAsComplete(new QuestionApplicationCompositeId(question.getId(), data.getApplication().getId()), lead.getId()));
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

    public static ResponseDataBuilder newApplicationQuestionResponseData(ServiceLocator serviceLocator) {

        return new ResponseDataBuilder(emptyList(), serviceLocator);
    }

    private ResponseDataBuilder(List<BiConsumer<Integer, ApplicationQuestionResponseData>> multiActions,
                                ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected ResponseDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationQuestionResponseData>> actions) {
        return new ResponseDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ApplicationQuestionResponseData createInitial() {
        return new ApplicationQuestionResponseData();
    }
}
