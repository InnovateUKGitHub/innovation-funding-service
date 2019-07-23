package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class QuestionStatusResourceBuilder extends BaseBuilder<QuestionStatusResource, QuestionStatusResourceBuilder> {

    private QuestionStatusResourceBuilder(List<BiConsumer<Integer, QuestionStatusResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionStatusResource>> actions) {
        return new QuestionStatusResourceBuilder(actions);
    }

    public static QuestionStatusResourceBuilder newQuestionStatusResource() {
        return new QuestionStatusResourceBuilder(emptyList()).with(uniqueIds());
    }

    public QuestionStatusResourceBuilder withApplication(ApplicationResource... applications) {
        return withArray((application, status) -> status.setApplication(application.getId()), applications);
    }

    public QuestionStatusResourceBuilder withMarkedAsComplete(Boolean... markedAsCompletes) {
        return withArray((markedAsComplete, status) -> status.setMarkedAsComplete(markedAsComplete), markedAsCompletes);
    }

    public QuestionStatusResourceBuilder withQuestion(Long... questions) {
        return withArray((question, status) -> status.setQuestion(question), questions);
    }

    public QuestionStatusResourceBuilder withMarkedAsCompleteByOrganisationId(Long... organisationIds) {
        return withArray((organisationId, status) -> status.setMarkedAsCompleteByOrganisationId(organisationId), organisationIds);
    }

    public QuestionStatusResourceBuilder withAssignee(Long... assignees) {
        return withArray((assignee, status) -> status.setAssignee(assignee), assignees);
    }

    public QuestionStatusResourceBuilder withMarkedAsCompleteOn(ZonedDateTime... dates) {
        return withArray((date, questionStatusResource) -> questionStatusResource.setMarkedAsCompleteOn(date), dates);
    }

    public QuestionStatusResourceBuilder withMarkedAsCompleteByUserId(Long... userIds) {
        return withArray((userId, questionStatusResource) -> questionStatusResource.setMarkedAsCompleteByUserId(userId), userIds);
    }

    public QuestionStatusResourceBuilder withMarkedAsCompleteByUserName(String... names) {
        return withArray((name, questionStatusResource) -> questionStatusResource.setMarkedAsCompleteByUserName(name), names);
    }

    @Override
    protected QuestionStatusResource createInitial() {
        return new QuestionStatusResource();
    }
}
